package wiemwebsite.demo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import wiemwebsite.demo.models.DemandeMission;
import wiemwebsite.demo.models.Missionfreelencer;
import wiemwebsite.demo.services.DemandeMissionService;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostulerMissionController {

    private static final Logger LOGGER = Logger.getLogger(PostulerMissionController.class.getName());

    @FXML private Label missionTitleLabel;
    @FXML private TextField offreMissionIdField;
    @FXML private TextField userIdField;
    @FXML private TextField freelancerEmailField;
    @FXML private TextField portfolioField;
    @FXML private TextArea motivationTextArea;
    @FXML private Button submitButton;
    @FXML private Button cancelButton;

    private Missionfreelencer mission;
    private final DemandeMissionService demandeMissionService = new DemandeMissionService();
    private final int currentUserId = 1; // À remplacer par la logique de session

    @FXML
    public void initialize() {
        LOGGER.info("Initialisation de PostulerMissionController");
        if (userIdField != null) {
            userIdField.setText(String.valueOf(currentUserId));
            LOGGER.info("userIdField rempli avec ID=" + currentUserId);
        } else {
            LOGGER.severe("userIdField est null dans le FXML");
        }
    }

    public void setMission(Missionfreelencer mission) {
        this.mission = mission;
        LOGGER.info("Mission reçue : ID=" + mission.getId() + ", Titre=" + mission.getTitre());

        if (missionTitleLabel != null) {
            missionTitleLabel.setText("Candidature pour : " + (mission.getTitre() != null ? mission.getTitre() : "Mission #" + mission.getId()));
        }
        if (offreMissionIdField != null) {
            offreMissionIdField.setText(String.valueOf(mission.getId()));
            LOGGER.info("offreMissionIdField rempli avec ID=" + mission.getId());
        } else {
            LOGGER.severe("offreMissionIdField est null dans le FXML");
        }
    }

    @FXML
    private void handleSubmit() {
        LOGGER.info("Soumission du formulaire pour mission ID=" + (offreMissionIdField != null ? offreMissionIdField.getText() : "inconnu"));

        // Valider les champs
        if (freelancerEmailField.getText().isEmpty() || motivationTextArea.getText().isEmpty() || portfolioField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }
        if (offreMissionIdField.getText().isEmpty() || offreMissionIdField.getText().equals("0")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID de la mission manquant ou invalide.");
            return;
        }
        if (userIdField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID de l'utilisateur manquant.");
            return;
        }

        try {
            DemandeMission demande = new DemandeMission(
                    0, // ID généré par la base
                    Integer.parseInt(offreMissionIdField.getText()),
                    motivationTextArea.getText(),
                    portfolioField.getText(),
                    "En attente",
                    Integer.parseInt(userIdField.getText())
            );

            demandeMissionService.ajouterDemande(demande);
            LOGGER.info("Candidature enregistrée : " + demande.toString());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Votre candidature a été soumise avec succès !");
            closeWindow();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la soumission : " + e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement de la candidature : " + e.getMessage());
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Erreur de format pour offreMissionId ou userId : " + e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de format dans les données.");
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la soumission : " + e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}