package com.bank;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties props = new Properties();

    static {
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("❌ config.properties not found in project root!", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}