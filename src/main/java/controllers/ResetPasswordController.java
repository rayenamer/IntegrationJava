package controllers;

import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.PasswordHasher;

public class ResetPasswordController {

    private static final Logger LOGGER = Logger.getLogger(ResetPasswordController.class.getName());
    private static final String DB_URL = "jdbc:mysql://localhost:3306/careera";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    @FXML private TextField codeField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;

    @FXML
    private void resetPassword() {
        String code = codeField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (code.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        if (!newPassword.matches(PASSWORD_PATTERN)) {
            showError("Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Les mots de passe ne correspondent pas");
            return;
        }

        try {
            if (verifyAndUpdatePassword(code, newPassword)) {
                showSuccess("Mot de passe réinitialisé avec succès");
                closeWindow();
            } else {
                showError("Code invalide ou expiré");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL : " + e.getMessage(), e);
            showError("Erreur lors de la réinitialisation du mot de passe");
        }
    }

    private boolean verifyAndUpdatePassword(String code, String newPassword) throws SQLException {
        String query = "SELECT id FROM user WHERE reset_token = ? AND token_expiry > NOW()";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String hashedPassword = PasswordHasher.hashpw(newPassword);
                    // Mettre à jour le mot de passe et effacer le token
                    String updateQuery = "UPDATE user SET password = ?, reset_token = NULL, token_expiry = NULL WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, hashedPassword);
                        updateStmt.setInt(2, userId);
                        return updateStmt.executeUpdate() > 0;
                    }
                }
                return false;
            }
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #d32f2f;");
    }

    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #388e3c;");
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) codeField.getScene().getWindow();
        stage.close();
    }
}
