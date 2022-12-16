package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Ausprobieren {

    public static void main(String[] args) throws IOException, InterruptedException {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            int randomNumber = random.nextInt(10,21);
            TimeUnit.SECONDS.sleep(randomNumber);
            System.out.println(randomNumber);
        }

    }

}
