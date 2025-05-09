package controllers;

import entities.TypeOffre;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.TypeOffreService;

import java.io.IOException;

public class AjouterTypeOffreController {

    @FXML
    private TextField nomTypeOffreTF; // Champ de texte pour le nom

    private final TypeOffreService typeOffreService = new TypeOffreService(); // Instance du service

    // Méthode appelée lors du clic sur le bouton "Ajouter"
    @FXML
    private void ajouterTypeOffre(ActionEvent event) {
        String nom = nomTypeOffreTF.getText().trim();

        // Contrôle pour vérifier si le champ est vide
        if (nom.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom du TypeOffre ne peut pas être vide.");
            return;
        }

        // Contrôle pour vérifier la longueur du nom (par exemple, entre 3 et 50 caractères)
        if (nom.length() < 3 || nom.length() > 50) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom du TypeOffre doit comporter entre 3 et 50 caractères.");
            return;
        }

        // Contrôle pour vérifier les caractères non valides (ex : chiffres ou caractères spéciaux indésirables)
        if (!nom.matches("[A-Za-zÀ-ÿ\\s]+")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom du TypeOffre ne peut contenir que des lettres et des espaces.");
            return;
        }

        // Si toutes les validations passent, on crée et on ajoute le TypeOffre
        TypeOffre typeOffre = new TypeOffre(nom);
        try {
            typeOffreService.ajouter(typeOffre);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "TypeOffre ajouté avec succès !");
            nomTypeOffreTF.clear(); // Réinitialiser le champ
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }
    @FXML
    private void handleOffreClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AjouterOffre.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une Offre");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour naviguer vers la liste des TypeOffres
    @FXML
    private void afficherTypeOffres(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AfficherTypeOffre.fxml"));
            Parent root = loader.load();
            nomTypeOffreTF.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'affichage des types d'offres.");
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
