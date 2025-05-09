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

public class ModifierTypeOffreController {

    @FXML
    private TextField nomTypeOffreTF;

    private TypeOffre typeOffre;
    private final TypeOffreService typeOffreService = new TypeOffreService();

    // Cette méthode est appelée depuis le contrôleur principal pour pré-remplir les données
    public void setTypeOffre(TypeOffre to) {
        this.typeOffre = to;
        nomTypeOffreTF.setText(to.getNom());
    }

    @FXML
    private void modifierTypeOffre(ActionEvent event) {
        String nouveauNom = nomTypeOffreTF.getText().trim();

        if (nouveauNom.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez saisir un nom.");
            return;
        }

        try {
            if (!typeOffre.getNom().equals(nouveauNom)) {
                typeOffre.setNom(nouveauNom);
                typeOffreService.modifier(typeOffre);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Type d'offre modifié avec succès.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Info", "Aucune modification détectée.");
            }

            // Fermer la fenêtre après modification
            Stage stage = (Stage) nomTypeOffreTF.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
}
