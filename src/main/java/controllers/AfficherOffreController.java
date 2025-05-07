package controllers;

import entities.Offre;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.OffreService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ResourceBundle;

public class AfficherOffreController implements Initializable {

    @FXML
    private ListView<Offre> listView;

    @FXML
    private TextField searchField;

    private ObservableList<Offre> obs;
    private final OffreService offreService = new OffreService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadOffres();
        } catch (SQLException e) {
            showError("Erreur lors du chargement des offres", e.getMessage());
        }
    }

    private void loadOffres() throws SQLException {
        List<Offre> offres = offreService.recuperer();
        obs = FXCollections.observableArrayList(offres);
        listView.setItems(obs);

        listView.setCellFactory(param -> new ListCell<Offre>() {
            @Override
            protected void updateItem(Offre item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    // Nom du poste en gras
                    Label nomPosteLabel = new Label("Nom du poste: " + item.getNomposte());
                    nomPosteLabel.setStyle("-fx-font-weight: bold;");

                    // Autres infos
                    Label entrepriseLabel = new Label(" | Entreprise: " + item.getEntreprise() + " | Localisation: ");

                    FontAwesomeIconView locationIcon = new FontAwesomeIconView(FontAwesomeIcon.MAP_MARKER);
                    locationIcon.setGlyphSize(17);
                    locationIcon.setFill(Color.RED);
                    locationIcon.setStyle("-fx-cursor: hand; -fx-font-family: FontAwesome;");

                    Label locationLabel = new Label(item.getLocalisation());
                    locationLabel.setStyle("-fx-text-fill: #1976D2;");

                    locationIcon.setOnMouseClicked(e -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MapView.fxml"));
                            Parent root = loader.load();
                            MapViewController controller = loader.getController();
                            controller.setLocation(item.getLocalisation());
                            Stage stage = new Stage();
                            stage.setTitle("Localisation de l'offre");
                            stage.setScene(new Scene(root));
                            stage.show();
                        } catch (IOException ex) {
                            showError("Erreur carte", "Impossible d'ouvrir la carte : " + ex.getMessage());
                        }
                    });

                    Label remainingInfo = new Label(
                            " | Salaire: " + item.getSalaire() + " | " +
                                    (item.isDisponibilite() ? "Disponible" : "Indisponible") + " | " +
                                    "Type Offre: " + item.getTypeOffre().getNom() + " | " +
                                    "Type Contrat: " + item.getTypeContrat().getNom()
                    );

                    hbox.getChildren().addAll(nomPosteLabel, entrepriseLabel, locationIcon, locationLabel, remainingInfo);
                    setGraphic(hbox);
                    setText(null);
                }
            }
        });
    }



    @FXML
    public void supprimerOffre(ActionEvent actionEvent) {
        Offre selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            offreService.supprimer(selected);
            obs.remove(selected);
        } else {
            showError("Suppression", "Veuillez sélectionner une offre à supprimer.");
        }
    }

    @FXML
    public void rechercherOffre(ActionEvent actionEvent) {
        String searchText = searchField.getText().toLowerCase().trim();
        try {
            List<Offre> toutesLesOffres = offreService.recuperer();
            if (searchText.isEmpty()) {
                obs = FXCollections.observableArrayList(toutesLesOffres);
            } else {
                List<Offre> offresFiltrees = toutesLesOffres.stream()
                        .filter(o -> o.getNomposte().toLowerCase().contains(searchText)
                                || o.getTypeOffre().getNom().toLowerCase().contains(searchText)
                                || o.getTypeContrat().getNom().toLowerCase().contains(searchText)
                                || o.getLocalisation().toLowerCase().contains(searchText))
                        .collect(Collectors.toList());
                obs = FXCollections.observableArrayList(offresFiltrees);
            }
            listView.setItems(obs);
        } catch (SQLException e) {
            showError("Erreur lors de la recherche", e.getMessage());
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

    @FXML
    public void ajouterOffre(ActionEvent actionEvent) {
        changerDeScene("/AjouterOffre.fxml", actionEvent);
    }

    @FXML
    public void afficherCandidature(ActionEvent actionEvent) {
        changerDeScene("/AfficherCandidaturesUser.fxml", actionEvent);
    }

    @FXML
    public void ajouterCandidature(ActionEvent actionEvent) {
        changerDeScene("/AjouterCandidature.fxml", actionEvent);
    }

    @FXML
    private void startQuiz(ActionEvent actionEvent) {
        changerDeScene("/Quiz.fxml", actionEvent);
    }

    @FXML
    private void chatbot(ActionEvent actionEvent) {
        changerDeScene("/chatbot.fxml", actionEvent);
    }

    @FXML
    private void modifierOffre(ActionEvent event) {
        try {
            Offre selected = listView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une offre.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierOffre.fxml"));
            Parent root = loader.load();

            ModifierOffreController controller = loader.getController();
            controller.setOffre(selected);

            Stage stage = new Stage();
            stage.setTitle("Modifier une Offre");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showError("Erreur lors de l'ouverture", e.getMessage());
        }
    }

    private void changerDeScene(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la nouvelle scène : " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void afficherStatistiques(ActionEvent event) {
        changerDeScene("/Statistiques.fxml", event);
    }
}
