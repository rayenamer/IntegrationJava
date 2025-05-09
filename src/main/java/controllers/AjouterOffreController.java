package controllers;

import entities.Offre;
import entities.TypeContrat;
import entities.TypeOffre;
import entities.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.OffreService;
import services.TypeContratService;
import services.TypeOffreService;
import utils.Session;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class AjouterOffreController implements Initializable {

    @FXML
    public ComboBox<String> typeContratCB;
    @FXML
    public ComboBox<String> typeOffreCB;
    public TextField posteTF;
    public TextField entrepriseTF;
    public TextField localisationTF;
    public TextField salaireTF;
    public CheckBox disponibleCB;
    public TextField utilisateurTF;
    @FXML
    public TextField imageTF;
    @FXML
    public Button gererContratBtn;
    public Button gererOffreBtn;

    private Map<String, TypeContrat> mapTypeContrat = new HashMap<>();
    private Map<String, TypeOffre> mapTypeOffre = new HashMap<>();

    private final OffreService offreService = new OffreService();
    private final TypeContratService typeContratService = new TypeContratService();
    private final TypeOffreService typeOffreService = new TypeOffreService();

    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTypeContrats();
        loadTypeOffres();

        currentUser = Session.getCurrentUser();
        if (currentUser != null) {
            // On met à jour utilisateurTF pour afficher le nom complet (Nom + Prénom)
            utilisateurTF.setText(currentUser.getNom() + " " + currentUser.getPrenom());
            utilisateurTF.setDisable(true);
        }

        disponibleCB.setSelected(true);
    }

    private void loadTypeContrats() {
        try {
            typeContratService.recuperer().forEach(typeContrat -> {
                typeContratCB.getItems().add(typeContrat.getNom());
                mapTypeContrat.put(typeContrat.getNom(), typeContrat);
            });
        } catch (SQLException e) {
            showError("Erreur de chargement des types de contrat", e.getMessage());
        }
    }

    private void loadTypeOffres() {
        try {
            typeOffreService.recuperer().forEach(typeOffre -> {
                typeOffreCB.getItems().add(typeOffre.getNom());
                mapTypeOffre.put(typeOffre.getNom(), typeOffre);
            });
        } catch (SQLException e) {
            showError("Erreur de chargement des types d'offre", e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void afficherTypeContrat(ActionEvent event) {
        loadNewScene("/AfficherTypeContrat.fxml");
    }

    public void afficherTypeOffres(ActionEvent event) {
        loadNewScene("/AfficherTypeOffre.fxml");
    }

    public void afficherOffres(ActionEvent actionEvent) {
        loadNewScene("/AfficherOffre.fxml");
    }

    private void loadNewScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            if (gererContratBtn.getScene() != null) {
                gererContratBtn.getScene().setRoot(root);
            }
        } catch (IOException e) {
            showError("Erreur de chargement de la scène", e.getMessage());
        }
    }

    public void ajouterOffre(ActionEvent actionEvent) {
        try {
            if (validateInputs()) {
                double salaire = parseSalaire();
                if (salaire <= 0) {
                    throw new IllegalArgumentException("Le salaire doit être positif.");
                }

                TypeContrat typeContrat = mapTypeContrat.get(typeContratCB.getValue());
                TypeOffre typeOffre = mapTypeOffre.get(typeOffreCB.getValue());

                if (typeContrat == null || typeOffre == null) {
                    throw new IllegalArgumentException("Type de contrat ou d'offre non valide.");
                }

                if (currentUser == null) {
                    throw new IllegalStateException("Aucun utilisateur connecté.");
                }

                // Crée une nouvelle offre avec le nom complet (nom + prénom)
                Offre offre = new Offre(
                        typeContrat,
                        typeOffre,
                        posteTF.getText().trim(),
                        entrepriseTF.getText().trim(),
                        localisationTF.getText().trim(),
                        salaire,
                        disponibleCB.isSelected(),
                        imageTF.getText().trim(),
                        currentUser.getNom() + " " + currentUser.getPrenom() // Ajout du prénom
                );

                offreService.ajouter(offre);
                showSuccess("Offre ajoutée !", "L'offre a été ajoutée avec succès.");
                clearInputs();
            }
        } catch (SQLException | IllegalArgumentException e) {
            showError("Erreur lors de l'ajout de l'offre", e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (posteTF.getText().isEmpty() || entrepriseTF.getText().isEmpty() ||
                localisationTF.getText().isEmpty() || salaireTF.getText().isEmpty() ||
                typeContratCB.getValue() == null || typeOffreCB.getValue() == null) {
            showError("Erreur", "Veuillez remplir tous les champs obligatoires");
            return false;
        }
        try {
            Double.parseDouble(salaireTF.getText());
        } catch (NumberFormatException e) {
            showError("Erreur", "Le salaire doit être un nombre valide");
            return false;
        }
        return true;
    }

    private void clearInputs() {
        posteTF.clear();
        entrepriseTF.clear();
        localisationTF.clear();
        salaireTF.clear();
        disponibleCB.setSelected(false);
        typeContratCB.setValue(null);
        typeOffreCB.setValue(null);
        utilisateurTF.clear();
        imageTF.clear();
    }

    private double parseSalaire() {
        try {
            return Double.parseDouble(salaireTF.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Le salaire doit être un nombre valide.");
        }
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(title);
        alert.setContentText(message);
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
    private Button btnRetour;

    @FXML
    private void handleRetour(ActionEvent event) {

         FXMLLoader loader = new FXMLLoader(getClass().getResource("Acceuil.fxml"));
        // Parent root = loader.load();
        // stage.setScene(new Scene(root));
    }
}
