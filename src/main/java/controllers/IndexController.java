package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class IndexController {

    @FXML
    private Button seConnecterButton;

    @FXML
    void handleConnexion(ActionEvent event) {
        try {

            Pane page = FXMLLoader.load(getClass().getResource("/PageConnexion.fxml"));

            // Obtenir la fenêtre actuelle
            Stage stage = (Stage) seConnecterButton.getScene().getWindow();

            // Créer une nouvelle scène avec le conteneur chargé et l'afficher dans la fenêtre
            stage.setScene(new Scene(page));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
