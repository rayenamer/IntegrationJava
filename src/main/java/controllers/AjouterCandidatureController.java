package controllers;

import entities.Candidature;
import entities.Offre;
import entities.Candidature.StatutCandidature;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import services.CandidatureService;
import services.OffreService;
import utils.Session;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AjouterCandidatureController {

    @FXML private ComboBox<String> offreNomCB;
    @FXML private TextField utilisateurTF;
    @FXML private TextField cvTF;
    @FXML private TextArea lettreMotivationTA;
    @FXML private ComboBox<String> statutCB;
    @FXML private DatePicker dateSoumissionDP;
    @FXML private Button ajouterBtn;

    private final CandidatureService candidatureService = new CandidatureService();
    private final OffreService offreService = new OffreService();

    // Pour faire le lien nom -> Offre
    private final Map<String, Offre> nomToOffreMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Vérifier si le ComboBox "statutCB" est null
        if (statutCB != null) {
            statutCB.getItems().addAll("EN_ATTENTE");
            statutCB.setValue("EN_ATTENTE"); // Valeur par défaut
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur d'initialisation du ComboBox des statuts.");
        }

        // Définir la date système par défaut
        if (dateSoumissionDP != null) {
            dateSoumissionDP.setValue(LocalDate.now());
        }

        // Charger les offres et remplir la ComboBox "offreNomCB"
        if (offreNomCB != null) {
            try {
                List<Offre> offres = offreService.getAll();
                for (Offre o : offres) {
                    nomToOffreMap.put(o.getNomposte(), o);
                    offreNomCB.getItems().add(o.getNomposte());
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement des offres : " + e.getMessage());
            }
        }

        // Remplir le champ utilisateur avec l'utilisateur courant
        User currentUser = Session.getCurrentUser();
        if (currentUser != null) {
            utilisateurTF.setText(currentUser.getNom() + " " + currentUser.getPrenom());
            utilisateurTF.setEditable(false); // Désactiver l'édition du champ
        } else {
            showAlert(Alert.AlertType.ERROR, "Aucun utilisateur connecté.");
        }
    }

    @FXML
    public void ajouterCandidature(ActionEvent event) {
        try {
            String nomOffre = offreNomCB.getValue();
            String utilisateur = utilisateurTF.getText();
            String cv = cvTF.getText();
            String lettre = lettreMotivationTA.getText();
            String statutStr = statutCB.getValue();
            LocalDate date = dateSoumissionDP.getValue();

            // Validation des champs
            if (nomOffre == null || utilisateur.isEmpty() || cv.isEmpty() || lettre.isEmpty() || date == null) {
                showAlert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs.");
                return;
            }

            // Vérification si l'offre est présente dans la map
            Offre offre = nomToOffreMap.get(nomOffre);
            if (offre == null) {
                showAlert(Alert.AlertType.ERROR, "Offre introuvable.");
                return;
            }

            // Créer la candidature
            Candidature candidature = new Candidature();
            candidature.setOffre(offre);
            candidature.setUtilisateur(utilisateur);
            candidature.setCv(cv);
            candidature.setLettreMotivation(lettre);
            candidature.setStatut(StatutCandidature.valueOf(statutStr));
            candidature.setDateSoumission(LocalDateTime.of(date, LocalTime.now()));

            // Ajouter la candidature
            candidatureService.ajouter(candidature);
            showAlert(Alert.AlertType.INFORMATION, "Candidature ajoutée avec succès.");

            // Fermer la fenêtre après l'ajout
            Stage stage = (Stage) ajouterBtn.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL : " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur inattendue : " + e.getMessage());
        }
    }

    @FXML
    private void afficherCandidature() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCandidature.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement : " + e.getMessage());
        }
    }

    @FXML
    public void afficherOffre(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherOffresUser.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement : " + e.getMessage());
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

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
