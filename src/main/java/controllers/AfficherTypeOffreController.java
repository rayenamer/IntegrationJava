package controllers;

import entities.TypeContrat;
import entities.TypeOffre;
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
import services.TypeOffreService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherTypeOffreController {

    @FXML
    private ListView<TypeOffre> listView;

    @FXML
    private TextField searchField;

    @FXML
    private TextField nomTypeOffreTF;

    private final TypeOffreService typeOffreService = new TypeOffreService();
    private ObservableList<TypeOffre> obs;

    @FXML
    public void initialize() {
        try {
            List<TypeOffre> typeOffres = typeOffreService.recuperer(); // Pas "rechercher", sauf si tu veux filtrer
            obs = FXCollections.observableArrayList(typeOffres);
            listView.setItems(obs);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des données : " + e.getMessage());
        }
    }


    @FXML
    public void supprimerTypeOffre(ActionEvent actionEvent) {
        TypeOffre selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un TypeOffre à supprimer.");
            return;
        }

        try {
            typeOffreService.supprimer(selected.getId());
            obs.remove(selected);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "TypeOffre supprimé avec succès.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression : " + e.getMessage());
        }
    }
    @FXML
    private void handleModifierClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javas/FXML/ModifierTypeOffre.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Modifier Type Offre");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOffreClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javas/FXML/AjouterOffre.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une Offre");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAjouterClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javas/FXML/AjouterTypeOffre.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter Type Offre");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRetourClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javas/FXML/AjouterOffre.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
            List<TypeOffre> typeOffres = typeOffreService.recuperer(); // Rechercher dans la liste complète
            List<TypeOffre> filtered = typeOffres.stream()
                    .filter(t -> t.getNom().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
            obs = FXCollections.observableArrayList(filtered);
            listView.setItems(obs);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche : " + e.getMessage());
        }
    }
    @javafx.fxml.FXML
    private void ajouterTypeOffre(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javas/FXML/AjouterTypeOffre.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Type d'Offre");
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
    public void ajouterOffre(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javas/FXML/AjouterOffre.fxml"));
            Object root = loader.load();

            // Récupérer la scène actuelle à partir de l'événement
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            // Afficher la nouvelle scène
            Scene scene = new Scene((Parent) root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
