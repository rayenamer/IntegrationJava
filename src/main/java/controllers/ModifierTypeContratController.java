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

public class ModifierTypeContratController {

    @FXML
    private TextField nomTypeContratTF;

    private TypeContrat typeContrat;
    private final TypeContratService typeContratService = new TypeContratService();

    // Cette méthode est appelée depuis le contrôleur principal pour pré-remplir les données
    public void setTypeContrat(TypeContrat tc) {
        this.typeContrat = tc;
        nomTypeContratTF.setText(tc.getNom());
    }

    @FXML
    private void modifierTypeContrat(ActionEvent event) {
        String nouveauNom = nomTypeContratTF.getText().trim();

        if (nouveauNom.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez saisir un nom.");
            return;
        }

        try {
            if (!typeContrat.getNom().equals(nouveauNom)) {
                typeContrat.setNom(nouveauNom);
                typeContratService.modifier(typeContrat);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "TypeContrat modifié avec succès.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Info", "Aucune modification détectée.");
            }

            // Fermer la fenêtre après modification
            Stage stage = (Stage) nomTypeContratTF.getScene().getWindow();
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
