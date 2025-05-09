package controllers;

import entities.Candidature;
import entities.Candidature.StatutCandidature;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import services.CandidatureService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherCandidaturesUserController {

    @FXML private ListView<Candidature> listView;
    @FXML private Button exportPdfBtn;
    private ObservableList<Candidature> obs;
    private final CandidatureService cs = new CandidatureService();

 @FXML
    private Button btnRetour;

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/Acceuil.fxml"));
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
            chargerCandidatures();
            // Add selection listener to show/hide PDF button
            listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && newVal.getStatut() == StatutCandidature.ACCEPTEE) {
                    exportPdfBtn.setVisible(true);
                } else {
                    exportPdfBtn.setVisible(false);
                }
            });
        } catch (SQLException e) {
            showAlert("Erreur lors du chargement des candidatures : " + e.getMessage());
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

    public void chargerCandidatures() throws SQLException {
        List<Candidature> candidatures = cs.recuperer(50, 0);
        obs = FXCollections.observableArrayList(candidatures);
        listView.setItems(obs);

        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Candidature candidature, boolean empty) {
                super.updateItem(candidature, empty);
                if (empty || candidature == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("Candidature #%d - Offre ID: %d - Statut: %s",
                            candidature.getId(),
                            candidature.getOffre().getId(),
                            candidature.getStatut()));

                    // Appliquer la couleur selon le statut
                    switch (candidature.getStatut()) {
                        case ACCEPTEE:
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            break;
                        case REFUSEE:
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            break;
                        case EN_ATTENTE:
                            setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });
    }


    @FXML
    public void supprimerCandidature(ActionEvent actionEvent) {
        Candidature selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                boolean success = cs.supprimer(selected.getId());
                if (success) {
                    obs.remove(selected);
                    showAlert("Candidature supprimée avec succès.");
                } else {
                    showAlert("Aucune candidature trouvée à supprimer.");
                }
            } catch (SQLException e) {
                showAlert("Erreur lors de la suppression : " + e.getMessage());
            }
        } else {
            showAlert("Veuillez sélectionner une candidature à supprimer.");
        }
    }

    @FXML
    public void rafraichirListe() {
        try {
            chargerCandidatures();
        } catch (SQLException e) {
            showAlert("Erreur lors du rafraîchissement : " + e.getMessage());
        }
    }

    @FXML
    private void retourAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCandidature.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors du retour : " + e.getMessage());
        }
    }

    @FXML
    public void accepterCandidature() {
        changerStatut(StatutCandidature.ACCEPTEE, "acceptée");
    }

    @FXML
    public void refuserCandidature() {
        changerStatut(StatutCandidature.REFUSEE, "refusée");
    }

    private void changerStatut(StatutCandidature statut, String label) {
        Candidature selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatut(statut);
            try {
                cs.modifier(selected);
                rafraichirListe();
                showAlert("Candidature " + label + ".");
            } catch (SQLException e) {
                showAlert("Erreur lors du changement de statut : " + e.getMessage());
            }
        } else {
            showAlert("Veuillez sélectionner une candidature à traiter.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void exporterPDF(ActionEvent actionEvent) {
        Candidature selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getStatut() == StatutCandidature.ACCEPTEE) {
            try {
                // Use the same path as in the service
                String userHome = System.getProperty("user.home");
                String pdfPath = userHome + File.separator + "Downloads" + File.separator + "candidature_" + selected.getId() + ".pdf";
                cs.generatePDFForAcceptedCandidature(selected);

                // Open PDF with system default application
                File pdfFile = new File(pdfPath);
                if (pdfFile.exists()) {
                    try {
                        java.awt.Desktop.getDesktop().open(pdfFile);
                        showAlert("PDF généré avec succès pour la candidature #" + selected.getId() + "\nEmplacement: " + pdfFile.getAbsolutePath());
                    } catch (IOException e) {
                        showAlert("Le PDF a été généré mais n'a pas pu être ouvert automatiquement.\nVous pouvez le trouver ici: " + pdfFile.getAbsolutePath());
                    }
                } else {
                    showAlert("Erreur: Le fichier PDF n'a pas été créé correctement à l'emplacement: " + pdfFile.getAbsolutePath());
                }
            } catch (Exception e) {
                showAlert("Erreur lors de la génération du PDF : " + e.getMessage());
            }
        } else {
            showAlert("Veuillez sélectionner une candidature acceptée pour générer le PDF.");
        }
    }
}