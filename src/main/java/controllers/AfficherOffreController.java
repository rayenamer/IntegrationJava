package controllers;

import entities.Offre;
import entities.User;
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
import utils.Session;
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

    @FXML
    private Button ajouterOffreBtn;

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
        // Récupérer l'utilisateur courant
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            showError("Erreur", "Aucun utilisateur connecté.");
            return;
        }

        // Récupérer toutes les offres
        List<Offre> allOffres = offreService.recuperer();
        
        // Filtrer pour n'avoir que les offres de l'utilisateur courant
        String currentUserName = currentUser.getNom() + " " + currentUser.getPrenom();
        List<Offre> userOffres = allOffres.stream()
            .filter(o -> o.getUtilisateur().equals(currentUserName))
            .collect(Collectors.toList());

        // Afficher les offres filtrées
        obs = FXCollections.observableArrayList(userOffres);
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
                    hbox.setStyle("-fx-padding: 5;");

                    Label nomPosteLabel = new Label(item.getNomposte());
                    nomPosteLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    Label entrepriseLabel = new Label(" | " + item.getEntreprise());
                    entrepriseLabel.setStyle("-fx-font-size: 14px;");

                    Label locationIcon = new Label("📍");
                    locationIcon.setStyle("-fx-font-size: 14px;");

                    Label locationLabel = new Label(item.getLocalisation());
                    locationLabel.setStyle("-fx-font-size: 14px;");

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
            try {
                offreService.supprimer(selected);
                obs.remove(selected);
                showError("Succès", "Offre supprimée avec succès.");
            } catch (Exception e) {
                showError("Erreur", "Erreur lors de la suppression : " + e.getMessage());
            }
        } else {
            showError("Attention", "Veuillez sélectionner une offre à supprimer.");
        }
    }

    @FXML
    public void rechercherOffre(ActionEvent actionEvent) {
        String searchText = searchField.getText().toLowerCase().trim();
        try {
            // Récupérer l'utilisateur courant
            User currentUser = Session.getCurrentUser();
            if (currentUser == null) {
                showError("Erreur", "Aucun utilisateur connecté.");
                return;
            }

            // Récupérer toutes les offres
            List<Offre> allOffres = offreService.recuperer();
            
            // Filtrer pour n'avoir que les offres de l'utilisateur courant
            String currentUserName = currentUser.getNom() + " " + currentUser.getPrenom();
            List<Offre> userOffres = allOffres.stream()
                .filter(o -> o.getUtilisateur().equals(currentUserName))
                .collect(Collectors.toList());

            // Appliquer le filtre de recherche si nécessaire
            if (!searchText.isEmpty()) {
                userOffres = userOffres.stream()
                    .filter(o -> o.getNomposte().toLowerCase().contains(searchText)
                            || o.getTypeOffre().getNom().toLowerCase().contains(searchText)
                            || o.getTypeContrat().getNom().toLowerCase().contains(searchText)
                            || o.getLocalisation().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
            }

            obs = FXCollections.observableArrayList(userOffres);
            listView.setItems(obs);
        } catch (SQLException e) {
            showError("Erreur", "Erreur lors de la recherche : " + e.getMessage());
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
            showError("Erreur", "Impossible d'ouvrir la page d'ajout d'offre : " + e.getMessage());
        }
    }

    @FXML
    private void ajouterOffre(ActionEvent event) {
        try {
            // Vérifier si l'utilisateur est un modérateur
            if (Session.getCurrentUser() != null && "moderateur".equalsIgnoreCase(Session.getCurrentUser().getType())) {
                // Charger la page AjouterOffre.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterOffre.fxml"));
                Parent root = loader.load();
                
                // Récupérer la scène actuelle
                Scene currentScene = ajouterOffreBtn.getScene();
                if (currentScene != null) {
                    // Remplacer le contenu de la scène
                    currentScene.setRoot(root);
                } else {
                    // Si pas de scène existante, créer une nouvelle fenêtre
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Ajouter une Offre");
                    stage.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Accès restreint");
                alert.setHeaderText(null);
                alert.setContentText("Cette fonctionnalité est réservée aux modérateurs.");
                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de charger la page d'ajout d'offre : " + e.getMessage());
            alert.showAndWait();
        }
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
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la page : " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
