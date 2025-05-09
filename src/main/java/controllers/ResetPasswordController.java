package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class ResetPasswordController {

    @FXML private TextField tokenField;
    @FXML private PasswordField newPasswordField;
    @FXML private Label statusLabel;


    @FXML
    private void openResetPasswordPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResetPassword.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Réinitialisation de mot de passe");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean verifyTokenAndUpdatePassword(String token, String newPassword) {
        boolean updated = false;
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/careera", "root", "")) {

            String query = "SELECT email FROM user WHERE reset_token = ? AND token_expiry > NOW()";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, token);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String email = rs.getString("email");

                // Update password et reset le token
                String updateQuery = "UPDATE user SET password = ?, reset_token = NULL, token_expiry = NULL WHERE email = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, newPassword);
                updateStmt.setString(2, email);

                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    updated = true;
                }

                updateStmt.close();
            }

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updated;
    }

    @FXML
    private void changePassword() {
        String token = tokenField.getText();
        String newPassword = newPasswordField.getText();

        if (token.isEmpty() || newPassword.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        if (verifyTokenAndUpdatePassword(token, newPassword)) {
            statusLabel.setText("Mot de passe changé avec succès !");
        } else {
            statusLabel.setText("Token invalide ou expiré.");
        }
    }


    private boolean updatePasswordWithToken(String token, String newPassword) {
        boolean updated = false;
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/careera", "root", "")) {

            String query = "SELECT email FROM user WHERE reset_token = ? AND token_expiry > NOW()";
            PreparedStatement checkStmt = connection.prepareStatement(query);
            checkStmt.setString(1, token);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String email = rs.getString("email");

                String updateQuery = "UPDATE user SET password = ?, reset_token = NULL, token_expiry = NULL WHERE email = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setString(1, newPassword);
                updateStmt.setString(2, email);
                int rowsUpdated = updateStmt.executeUpdate();

                if (rowsUpdated > 0) {
                    updated = true;
                }

                updateStmt.close();
            }

            checkStmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updated;
    }
}
