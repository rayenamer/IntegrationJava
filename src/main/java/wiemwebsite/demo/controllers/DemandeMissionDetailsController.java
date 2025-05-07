package wiemwebsite.demo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import wiemwebsite.demo.models.DemandeMission;
import wiemwebsite.demo.services.DemandeMissionService;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DemandeMissionDetailsController {

    private static final Logger LOGGER = Logger.getLogger(DemandeMissionDetailsController.class.getName());
    private final DemandeMissionService demandeMissionService = new DemandeMissionService();

    @FXML private Label idLabel;
    @FXML private Label missionTitreLabel;
    @FXML private Label offreMissionIdLabel;
    @FXML private TextArea messageArea;
    @FXML private Hyperlink portfolioLink;
    @FXML private Label statusLabel;
    @FXML private Label userIdLabel;
    @FXML private Button fermerButton;

    private int demandeId = -1;

    @FXML
    public void initialize() {
        // Initialisation vide, les champs sont remplis dans setDemandeId
    }

    // Définir l'ID de la demande
    public void setDemandeId(int demandeId) {
        this.demandeId = demandeId;
        if (demandeId != -1) {
            loadDemandeDetails();
        } else {
            LOGGER.warning("ID de demande invalide : " + demandeId);
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID de demande invalide.");
        }
    }

    // Charger les détails de la demande
    private void loadDemandeDetails() {
        try {
            DemandeMission demande = demandeMissionService.getDemandeById(demandeId);
            if (demande != null) {
                idLabel.setText(String.valueOf(demande.getId()));
                missionTitreLabel.setText(demandeMissionService.getMissionTitreByOffreMissionId(demande.getOffremissionId()));
                offreMissionIdLabel.setText(String.valueOf(demande.getOffremissionId()));
                messageArea.setText(demande.getMessage() != null ? demande.getMessage() : "Aucune lettre de motivation");
                portfolioLink.setText(demande.getPortfolio() != null && !demande.getPortfolio().isEmpty() ? demande.getPortfolio() : "Aucun portfolio");
                statusLabel.setText(demande.getStatus() != null ? demande.getStatus() : "N/A");
                userIdLabel.setText(String.valueOf(demande.getUserid()));
                LOGGER.info("Détails de la demande chargés pour demande_id=" + demandeId);
            } else {
                LOGGER.warning("Aucune demande trouvée pour demande_id=" + demandeId);
                showAlert(Alert.AlertType.WARNING, "Aucune donnée", "Aucune demande trouvée pour ID=" + demandeId);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des détails de la demande : " + e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement : " + e.getMessage());
        }
    }

    // Gérer le clic sur le lien portfolio
    @FXML
    private void handlePortfolioClick() {
        String portfolioUrl = portfolioLink.getText();
        if (portfolioUrl != null && !portfolioUrl.equals("Aucun portfolio")) {
            try {
                Desktop.getDesktop().browse(new URI(portfolioUrl));
                LOGGER.info("Ouverture du portfolio : " + portfolioUrl);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de l'ouverture du portfolio : " + e.getMessage(), e);
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le portfolio : " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun portfolio", "Aucun portfolio disponible pour cette demande.");
        }
    }

    // Gérer le clic sur "Fermer"
    @FXML
    private void handleFermer() {
        Stage stage = (Stage) fermerButton.getScene().getWindow();
        stage.close();
        LOGGER.info("Fenêtre des détails de la demande fermée");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}