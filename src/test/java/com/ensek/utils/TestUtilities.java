package com.ensek.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestUtilities {

    public static String readTestDataFromFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public static int generateRandomNumber(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }
}
