package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminDashboardController {

    private static final Logger LOGGER = Logger.getLogger(AdminDashboardController.class.getName());

    @FXML private Button consulterCandidaturesButton;

    @FXML
    private void handleConsulterCandidatures() {
        try {
            // Charger demande_mission.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/wiemwebsite/demo/demande_mission.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Gestion des Candidatures");
            stage.setScene(scene);
            stage.show();
            LOGGER.info("Interface de gestion des candidatures ouverte.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de demande_mission.fxml : " + e.getMessage(), e);
            showAlert("Erreur", "Impossible de charger l'interface des candidatures : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}