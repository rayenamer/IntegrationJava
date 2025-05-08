package utils;

import com.github.cage.Cage;
import com.github.cage.GCage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

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
    private static ImageView currentCaptchaImageView;

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
            imageView.setFitWidth(200); // Ajuster la taille selon vos besoins
            imageView.setPreserveRatio(true);
            
            currentCaptchaImageView = imageView;
            return imageView;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void refreshCaptcha(ImageView captchaImageView) {
        if (captchaImageView != null) {
            ImageView newCaptcha = generateCaptcha();
            if (newCaptcha != null) {
                captchaImageView.setImage(newCaptcha.getImage());
            }
        }
    }

    // Nouvelle méthode simplifiée pour la validation basique
    public static boolean validateCaptcha(String userInput) {
        if (currentCaptchaText == null || userInput == null) {
            return false;
        }
        return currentCaptchaText.equalsIgnoreCase(userInput.trim());
    }

    // Méthode étendue avec gestion des erreurs visuelles
    public static boolean validateCaptcha(String userInput, TextField captchaField, Label errorLabel) {
        if (currentCaptchaText == null || userInput == null) {
            showError("Veuillez entrer le code captcha", captchaField, errorLabel);
            return false;
        }

        boolean isValid = currentCaptchaText.equalsIgnoreCase(userInput.trim());
        if (!isValid) {
            showError("Code captcha incorrect", captchaField, errorLabel);
            refreshCaptcha(currentCaptchaImageView);
        } else {
            clearError(captchaField, errorLabel);
        }
        return isValid;
    }

    private static void showError(String message, TextField field, Label errorLabel) {
        if (field != null) {
            field.setStyle("-fx-border-color: red;");
        }
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private static void clearError(TextField field, Label errorLabel) {
        if (field != null) {
            field.setStyle("");
        }
        if (errorLabel != null) {
            errorLabel.setText("");
        }
    }

    private static String generateRandomText() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Caractères plus lisibles
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
