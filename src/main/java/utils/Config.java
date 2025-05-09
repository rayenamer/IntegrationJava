package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();
    private static Config instance;

    private Config() {
        try {
            // Try to load from .env file first
            try (FileInputStream envFile = new FileInputStream(".env")) {
                properties.load(envFile);
            } catch (IOException e) {
                System.out.println("No .env file found, using default values");
            }
        } catch (Exception e) {
            System.err.println("Error loading configuration: " + e.getMessage());
        }
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    // Database configuration
    public String getDbUrl() {
        return properties.getProperty("DB_URL", "jdbc:mysql://localhost:3306/careera");
    }

    public String getDbUser() {
        return properties.getProperty("DB_USER", "root");
    }

    public String getDbPassword() {
        return properties.getProperty("DB_PASSWORD", "");
    }

    // API Keys
    public String getOpenAiApiKey() {
        return properties.getProperty("OPENAI_API_KEY");
    }

    public String getWeatherApiKey() {
        return properties.getProperty("WEATHER_API_KEY");
    }

    public String getDeepLApiKey() {
        return properties.getProperty("DEEPL_API_KEY");
    }

    public String getGeminiApiKey() {
        return properties.getProperty("GEMINI_API_KEY");
    }
} 