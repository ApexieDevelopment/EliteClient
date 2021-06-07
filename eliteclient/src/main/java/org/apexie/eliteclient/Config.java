package org.apexie.eliteclient;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;

public class Config {

    private static final Dotenv dotenv = Dotenv.load();

    public static String get(String key) {
        File f = new File(System.getProperty("user.dir") + "/.env");
        if(f.exists()) {
            return dotenv.get(key.toUpperCase());
        } else {
            return System.getenv().get(key.toUpperCase());
        }
    }

}
