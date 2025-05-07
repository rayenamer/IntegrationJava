package utils;

import com.github.cage.Cage;
import com.github.cage.GCage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CaptchaGenerator {
    private static final Cage cage = new GCage();
    private static final Map<String, String> captchaStore = new HashMap<>();
    private static final Random random = new Random();
    private static String currentCaptchaText;

    public static ImageView generateCaptcha() {
        try {
            // Générer un texte aléatoire pour le CAPTCHA
            currentCaptchaText = generateRandomText();

            // Générer l'image CAPTCHA
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            cage.draw(currentCaptchaText, outputStream);

            // Convertir en Image JavaFX
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            Image image = new Image(inputStream);
            ImageView imageView = new ImageView(image);

            return imageView;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean validateCaptcha(String userInput) {
        if (currentCaptchaText == null || userInput == null) {
            return false;
        }
        return currentCaptchaText.equalsIgnoreCase(userInput.trim());
    }

    private static String generateRandomText() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String getCurrentCaptchaText() {
        return currentCaptchaText;
    }
}
