package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.EmailSender;
import java.sql.*;
import java.util.Random;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class ForgotPasswordController {

    @FXML private TextField emailField;
    @FXML private Label statusLabel;

    @FXML
    private void sendResetEmail() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showError("Veuillez saisir une adresse email valide.");
            return;
        }

        try {
            if (checkEmailExists(email)) {
                String resetToken = generateResetToken();
                if (saveResetToken(email, resetToken)) {
                    sendResetEmail(email, resetToken);
                    showSuccess("Email de réinitialisation envoyé avec succès à " + email);

                    // Fermer la fenêtre de "Mot de passe oublié"
                    closeCurrentWindow();

                    // Ouvrir la fenêtre de "Réinitialisation de mot de passe"
                    openResetPasswordPage();

                } else {
                    showError("Erreur lors de la génération du code de réinitialisation");
                }
            } else {
                showError("Aucun compte associé à cette adresse email.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur technique : " + e.getMessage());
        }
    }

    private String generateResetToken() {
        // Génère un code à 6 chiffres
        Random random = new Random();
        int token = 100000 + random.nextInt(900000); // Entre 100000 et 999999
        return String.valueOf(token);
    }

    private boolean checkEmailExists(String email) throws SQLException {
        String query = "SELECT id FROM user WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean saveResetToken(String email, String token) throws SQLException {
        String query = "UPDATE user SET reset_token = ?, token_expiry = DATE_ADD(NOW(), INTERVAL 1 HOUR) WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, token);
            stmt.setString(2, email);
            return stmt.executeUpdate() > 0;
        }
    }

    private void sendResetEmail(String email, String token) {
        try {
            String subject = "Réinitialisation de votre mot de passe";
            String content = "Bonjour,\n\n"
                    + "Voici votre code de réinitialisation :\n"
                    + token + "\n\n"
                    + "Ce code expirera dans 1 heure.\n\n"
                    + "Cordialement,\nL'équipe Careera";

            EmailSender.sendEmail(email, subject, content);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'envoi de l'email");
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/careera",
                "root",
                "");
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #d32f2f;");
    }

    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #388e3c;");
    }

    // Fermer la fenêtre actuelle
    private void closeCurrentWindow() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }

    // Ouvrir la fenêtre de réinitialisation de mot de passe
    private void openResetPasswordPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPassword.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Réinitialisation de mot de passe");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture de la fenêtre de réinitialisation.");
        }
    }
}