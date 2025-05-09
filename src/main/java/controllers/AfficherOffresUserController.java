package controllers;

import entities.Offre;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherOffresUserController {

    @FXML
    private ListView<Offre> listView;

    @FXML
    private TextField searchField;

    private ObservableList<Offre> obs;
    private final OffreService offreService = new OffreService();
     @FXML
    private Button btnRetour;

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/Acceuilfc.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            javafx.stage.Stage stage = (javafx.stage.Stage) btnRetour.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
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

                    // Clic sur l'icône de localisation pour ouvrir la carte
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
    public void ajouterCandidature(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCandidature.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une Candidature");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir la page d'ajout de candidature : " + e.getMessage());
        }
    }

    @FXML
    private void startQuiz(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Quiz.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Quiz de Carrière");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger le quiz : " + e.getMessage());
        }
    }

    @FXML
    private void chatbot(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatbot.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Chatbot");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger le chatbot : " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
