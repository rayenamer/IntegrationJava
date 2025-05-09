package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.net.URL;

public class CreationCompteController {

    @FXML
    private void handleFreelancer(ActionEvent event) {
        try {
            // Utiliser le chemin relatif au package
            String fxmlPath = "/AjouterFreelancer.fxml";
            URL fxmlUrl = getClass().getResource(fxmlPath);
            
            if (fxmlUrl == null) {
                throw new IOException("Le fichier FXML n'existe pas : " + fxmlPath);
            }

            System.out.println("Chargement du fichier FXML : " + fxmlUrl);
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            if (root == null) {
                throw new IOException("Le chargement de la page a échoué");
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ajouter un Freelancer");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page AjouterFreelancer : " + e.getMessage());
        }
    }

    @FXML
    private void handleChercheur(ActionEvent event) {
        try {
            // Utiliser le chemin relatif au package
            String fxmlPath = "/AjouterChercheur.fxml";
            URL fxmlUrl = getClass().getResource(fxmlPath);
            
            if (fxmlUrl == null) {
                throw new IOException("Le fichier FXML n'existe pas : " + fxmlPath);
            }

            System.out.println("Chargement du fichier FXML : " + fxmlUrl);
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            if (root == null) {
                throw new IOException("Le chargement de la page a échoué");
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ajouter un Chercheur");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page AjouterChercheur : " + e.getMessage());
        }
    }

    @FXML
    private void handleModerateur(ActionEvent event) {
        try {
            // Utiliser le chemin relatif au package
            String fxmlPath = "/AjouterModerateur.fxml";
            URL fxmlUrl = getClass().getResource(fxmlPath);
            
            if (fxmlUrl == null) {
                throw new IOException("Le fichier FXML n'existe pas : " + fxmlPath);
            }

            System.out.println("Chargement du fichier FXML : " + fxmlUrl);
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            if (root == null) {
                throw new IOException("Le chargement de la page a échoué");
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ajouter un Modérateur");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page AjouterModerateur : " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
