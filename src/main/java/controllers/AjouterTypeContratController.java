package controllers;

import entities.TypeContrat;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.TypeContratService;

import java.io.IOException;

public class AjouterTypeContratController {
    @FXML
    private TextField nomTypeContratTF; // Champ de texte pour le nom

    private final TypeContratService typeContratService = new TypeContratService(); // Instance du service

    // Méthode appelée lors du clic sur le bouton "Ajouter"
    @FXML
    private void ajouterTypeContrat(ActionEvent event) {
        String nom = nomTypeContratTF.getText().trim();

        // Contrôle pour vérifier si le champ est vide
        if (nom.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom du TypeContrat ne peut pas être vide.");
            return;
        }

        // Contrôle de la longueur du nom
        if (nom.length() < 3 || nom.length() > 50) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom du TypeContrat doit comporter entre 3 et 50 caractères.");
            return;
        }

        // Contrôle des caractères valides
        if (!nom.matches("[A-Za-zÀ-ÿ\\s]+")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom du TypeContrat ne peut contenir que des lettres et des espaces.");
            return;
        }

        // Création de l'objet et ajout via le service
        TypeContrat typeContrat = new TypeContrat(nom);
        try {
            typeContratService.ajouter(typeContrat);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "TypeContrat ajouté avec succès !");
            nomTypeContratTF.clear(); // Réinitialiser le champ
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    // Méthode pour naviguer vers la liste des TypeContrats
    @FXML
    private void afficherTypeContrats(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherTypeContrat.fxml"));
            Parent root = loader.load();
            nomTypeContratTF.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'affichage des types de contrat.");
        }
    }
    @FXML
    private void handleOffreClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterOffre.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une Offre");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode utilitaire pour afficher une alerte
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
