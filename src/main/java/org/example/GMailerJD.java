package org.example;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.api.services.gmail.GmailScopes.GMAIL_SEND;
import static javax.mail.Message.RecipientType.TO;


public class GMailerJD {

    private static final String TEST_EMAIL = "furi1837@gmail.com";

    private static final String RECIPIENT_EMAIL = "hannesfandrich@gmail.com";
//    private static final String RECIPIENT_EMAIL = "Onlinehilfe@JD-Sports.de";
    private final Gmail service;

    public GMailerJD() throws Exception {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        service = new Gmail.Builder(httpTransport, jsonFactory, getCredentials(httpTransport, jsonFactory))
                .setApplicationName("Test Mailer")
                .build();
    }

    private static Credential getCredentials(final NetHttpTransport httpTransport, GsonFactory jsonFactory)
            throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(GMailerFootlocker.class.getResourceAsStream("/client_secret_33861712024-d3dngs7jvj50n95972b51c7i7jfrd2u8.apps.googleusercontent.com.json")));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, Set.of(GMAIL_SEND))
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile()))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public void sendMail(String subject, String message) throws Exception {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(TEST_EMAIL));
        email.addRecipient(TO, new InternetAddress(RECIPIENT_EMAIL));
        email.setSubject(subject);
        email.setText(message);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message msg = new Message();
        msg.setRaw(encodedEmail);

        try {
            msg = service.users().messages().send("me", msg).execute();
            System.out.println("Message id: " + msg.getId());
            System.out.println(msg.toPrettyString());
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                System.err.println("Unable to send message: " + e.getDetails());
            } else {
                throw e;
            }
        }
    }

    public static void main(String[] args) throws Exception {

        GMailerFootlocker gmailer = new GMailerFootlocker();

        String file = "C:\\Users\\Hanne\\IdeaProjects\\RechnungManagement\\src\\main\\resources\\JDTemplate 15.12.Teil1.csv";
        BufferedReader reader = null;
        String line = null;
        Random random = new Random();
        int i = 0;
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                if (i > 0) {
                    if (row[2].equals("")) {
                        gmailer.sendMail(
                                "Rechnung ",
                                "Sehr geehrte Damen und Herren,\n\n" +
                                        "ich benötige eine Rechnung für meine Bestellung.\n\n" +
                                        "Meine Bestellinformationen sind:\n\n" +
                                        row[1] + " " + row[2] + "\n" +
                                        row[3] + "\n" +
                                        row[4] + " " + row[5] + "\n" +
                                        row[6] + "\n" +
                                        "Email: " +
                                        row[7] + "\n" +
                                        "Order Nummer: " + row[0] + "\n\n" +
                                        "Mir ist aufgefallen das im Namen ein Fehler ist. Könnten Sie das bitte ändern\n" +
                                        "Der Richtige Name ist Sneaker Unlimited GmbH\n" +
                                        "Der Rest ist so richtig. Danke schön.\n\n" +
                                        "Mit freundlichen Grüßen");
                        row[8] = "sendet";
                    } else {
                        gmailer.sendMail(
                                "Rechnung ",
                                "Sehr geehrte Damen und Herren,\n\n" +
                                        "ich benötige eine Rechnung für meine Bestellung.\n\n" +
                                        "Meine Bestellinformationen sind:\n\n" +
                                        row[1] + "\n" +
                                        row[2] + "\n" +
                                        row[3] + "\n" +
                                        row[4] + " " + row[5] + "\n" +
                                        row[6] + "\n" +
                                        "Email: " +
                                        row[7] + "\n" +
                                        "Order Nummer: " + row[0] + "\n\n" +

                                        "Mit freundlichen Grüßen");
                        row[7] = "sendet";
                    }
                }
                i++;
                System.out.println("An JD verschickte Mail " + i);
                int randomNumber = random.nextInt(1, 2);
                TimeUnit.SECONDS.sleep(randomNumber);
                System.out.println("Delay: " + randomNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }
    }
}






