package controllers;

import entities.TypeContrat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.TypeContratService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherTypeContratController {
    @FXML
    private ListView<TypeContrat> listView;

    @FXML
    private TextField searchField;

    @FXML
    private TextField nomTypeContratTF;

    private final TypeContratService typeContratService = new TypeContratService();
    private ObservableList<TypeContrat> obs;

    @FXML
    public void initialize() {
        try {
            List<TypeContrat> typeContrats = typeContratService.recuperer();
            obs = FXCollections.observableArrayList(typeContrats);
            listView.setItems(obs);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des données : " + e.getMessage());
        }
    }

    @FXML
    public void supprimerTypeContrat(ActionEvent actionEvent) {
        TypeContrat selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un TypeContrat à supprimer.");
            return;
        }

        try {
            typeContratService.supprimer(selected.getId());
            obs.remove(selected);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "TypeContrat supprimé avec succès.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression : " + e.getMessage());
        }
    }
    @FXML
    private void modifTypeContrat(ActionEvent event) {
        try {
            TypeContrat selected = listView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un élément.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierTypeContrat.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la vue chargée
            ModifierTypeContratController controller = loader.getController();
            controller.setTypeContrat(selected); // On lui passe l'objet à modifier

            Stage stage = new Stage();
            stage.setTitle("Modifier un Type de Contrat");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @FXML
    public void rechercher(ActionEvent actionEvent) {
        String searchText = searchField.getText().toLowerCase().trim();

        try {
            List<TypeContrat> typeContrats = typeContratService.recuperer();
            List<TypeContrat> filtered = typeContrats.stream()
                    .filter(t -> t.getNom().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
            obs = FXCollections.observableArrayList(filtered);
            listView.setItems(obs);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche : " + e.getMessage());
        }
    }

    @FXML
    private void ajouterTypeContrat(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterTypeContrat.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Type de Contrat");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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

    @FXML
    public void ajouterOffre(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterOffre.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
