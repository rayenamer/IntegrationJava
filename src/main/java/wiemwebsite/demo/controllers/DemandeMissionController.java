package wiemwebsite.demo.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import wiemwebsite.demo.models.CandidatureMission;
import wiemwebsite.demo.services.DemandeMissionService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DemandeMissionController {

    private static final Logger LOGGER = Logger.getLogger(DemandeMissionController.class.getName());
    private final DemandeMissionService demandeMissionService = new DemandeMissionService();
    private int missionId = -1;

    @FXML private TableView<CandidatureMission> candidatureTable;
    @FXML private TableColumn<CandidatureMission, Number> idColumn;
    @FXML private TableColumn<CandidatureMission, String> missionIdColumn;
    @FXML private TableColumn<CandidatureMission, Number> demandeIdColumn;
    @FXML private TableColumn<CandidatureMission, String> etatColumn;
    @FXML private TableColumn<CandidatureMission, Number> scoreColumn;
    @FXML private TableColumn<CandidatureMission, Number> userIdColumn;
    @FXML private TableColumn<CandidatureMission, String> userColumn;
    @FXML private Button accepterButton;
    @FXML private Button supprimerButton;

    @FXML
    public void initialize() {
        // Configure table columns
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()));
        missionIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMissionTitre()));
        demandeIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getDemandeId()));
        etatColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEtat()));
        scoreColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getScore()));
        userIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getUserid()));
        userColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUser()));

        // Configure custom cell for missionIdColumn with "Voir plus" button
        missionIdColumn.setCellFactory(column -> new TableCell<>() {
            private final Button voirPlusButton = new Button("Voir plus");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    voirPlusButton.setOnAction(event -> handleVoirPlus(getTableRow().getItem()));
                    setGraphic(voirPlusButton);
                    setText(null);
                }
            }
        });

        // Disable buttons when no selection
        accepterButton.disableProperty().bind(candidatureTable.getSelectionModel().selectedItemProperty().isNull());
        supprimerButton.disableProperty().bind(candidatureTable.getSelectionModel().selectedItemProperty().isNull());

        refreshCandidatures();
    }

    public void setMissionId(int missionId) {
        if (missionId < 0) {
            LOGGER.warning("Invalid mission ID provided: " + missionId);
            showAlert(Alert.AlertType.WARNING, "ID invalide", "L'ID de la mission doit être positif.");
            return;
        }
        this.missionId = missionId;
        refreshCandidatures();
    }

    public void refreshCandidatures() {
        try {
            ObservableList<CandidatureMission> candidatures;
            if (missionId == -1) {
                candidatures = demandeMissionService.getAllCandidatures();
            } else {
                candidatures = demandeMissionService.getCandidaturesByMissionId(missionId);
            }
            candidatureTable.setItems(candidatures);
            LOGGER.info("Candidatures chargées pour mission ID=" + missionId + " : " + candidatures.size());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des candidatures pour mission ID=" + missionId + " : " + e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des candidatures : " + e.getMessage());
        }
    }

    @FXML
    private void handleAccepter() {
        CandidatureMission selected = candidatureTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            LOGGER.warning("Tentative d'acceptation sans sélection de candidature.");
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une candidature.");
            return;
        }

        // Validation: Prevent accepting an already accepted candidature
        if ("Acceptée".equalsIgnoreCase(selected.getEtat())) {
            LOGGER.warning("Tentative d'acceptation d'une candidature déjà acceptée, ID=" + selected.getId());
            showAlert(Alert.AlertType.WARNING, "Action invalide", "Cette candidature est déjà acceptée.");
            return;
        }

        // Validation: Check if score is valid (e.g., non-negative)
        if (selected.getScore() < 0) {
            LOGGER.warning("Tentative d'acceptation d'une candidature avec score invalide, ID=" + selected.getId() + ", Score=" + selected.getScore());
            showAlert(Alert.AlertType.WARNING, "Score invalide", "Le score de la candidature doit être positif.");
            return;
        }

        // Optional: Check if another candidature is already accepted for this mission
        try {
            if (demandeMissionService.hasAcceptedCandidature(missionId)) {
                LOGGER.warning("Tentative d'acceptation alors qu'une candidature est déjà acceptée pour mission ID=" + missionId);
                showAlert(Alert.AlertType.WARNING, "Action invalide", "Une candidature a déjà été acceptée pour cette mission.");
                return;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification des candidatures acceptées pour mission ID=" + missionId + " : " + e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la validation : " + e.getMessage());
            return;
        }

        // Confirmation dialog for acceptance
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous accepter la candidature ID=" + selected.getId() + " ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                selected.setEtat("Acceptée");
                try {
                    demandeMissionService.modifierCandidature(selected);
                    String userEmail = demandeMissionService.getUserEmail(selected.getUserid());
                    if (!userEmail.isEmpty()) {
                        try {
                            demandeMissionService.sendAcceptanceEmail(userEmail, selected.getUser(), selected.getMissionTitre());
                            LOGGER.info("Email envoyé à " + userEmail + " pour la mission " + selected.getMissionTitre());
                            showAlert(Alert.AlertType.INFORMATION, "Succès", "Email envoyé avec succès à " + userEmail);
                        } catch (Exception e) {
                            LOGGER.log(Level.SEVERE, "Échec de l'envoi de l'email à " + userEmail + " : " + e.getMessage(), e);
                            showAlert(Alert.AlertType.WARNING, "Avertissement", "L'email n'a pas pu être envoyé : " + e.getMessage());
                        }
                    } else {
                        LOGGER.warning("Aucun email trouvé pour userId=" + selected.getUserid() + ", email non envoyé.");
                        showAlert(Alert.AlertType.WARNING, "Avertissement", "Aucun email trouvé pour cet utilisateur.");
                    }
                    refreshCandidatures();
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Candidature acceptée !");
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Erreur lors de l'acceptation de la candidature ID=" + selected.getId() + " : " + e.getMessage(), e);
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'acceptation : " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleSupprimer() {
        CandidatureMission selected = candidatureTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            LOGGER.warning("Tentative de suppression sans sélection de candidature.");
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une candidature.");
            return;
        }

        // Validation: Prevent deleting an accepted candidature
        if ("Acceptée".equalsIgnoreCase(selected.getEtat())) {
            LOGGER.warning("Tentative de suppression d'une candidature acceptée, ID=" + selected.getId());
            showAlert(Alert.AlertType.WARNING, "Action invalide", "Impossible de supprimer une candidature acceptée.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous supprimer la candidature ID=" + selected.getId() + " ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    demandeMissionService.supprimerCandidature(selected.getId());
                    refreshCandidatures();
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Candidature supprimée !");
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de la candidature ID=" + selected.getId() + " : " + e.getMessage(), e);
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression : " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleVoirPlus(CandidatureMission candidature) {
        if (candidature == null) {
            LOGGER.warning("Tentative de voir les détails sans sélection de candidature.");
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une candidature.");
            return;
        }

        // Validation: Check if demandeId is valid
        if (candidature.getDemandeId() <= 0) {
            LOGGER.warning("Demande ID invalide pour la candidature ID=" + candidature.getId() + ", demandeId=" + candidature.getDemandeId());
            showAlert(Alert.AlertType.WARNING, "ID invalide", "L'ID de la demande est invalide.");
            return;
        }

        try {
            // Verify FXML resource path
            String fxmlPath = "/wiemwebsite/demo/FXML/demande_mission_details.fxml";
            if (getClass().getResource(fxmlPath) == null) {
                LOGGER.severe("Fichier FXML non trouvé : " + fxmlPath);
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le fichier FXML des détails est introuvable.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            DemandeMissionDetailsController controller = loader.getController();
            controller.setDemandeId(candidature.getDemandeId());
            Stage stage = new Stage();
            stage.setTitle("Détails de la Demande");
            stage.setScene(scene);
            stage.show();
            LOGGER.info("Fenêtre des détails ouverte pour demande_id=" + candidature.getDemandeId());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de demande_mission_details.fxml : " + e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les détails : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}