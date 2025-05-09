package controllers;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import entities.Candidature;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import services.CandidatureService;
import utils.Session;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static utils.Session.getCurrentUser;

public class AfficherCandidatureController {

    @FXML
    private ListView<Candidature> listView;

    @FXML
    private Button supprimerBtn;

    @FXML
    private Button exportPdfBtn;

    @FXML
    public void initialize() {
        // Masquer le bouton Export PDF au départ
        exportPdfBtn.setVisible(false);
        refreshListView();

        // Ajouter un listener pour la sélection de la candidature
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Vérifier le statut de la candidature
                if ("ACCEPTEE".equalsIgnoreCase(newValue.getStatut().toString())) {
                    exportPdfBtn.setVisible(true);  // Afficher le bouton si le statut est "ACCEPTEE"
                } else {
                    exportPdfBtn.setVisible(false); // Masquer le bouton sinon
                }
            }
        });
    }

    private void refreshListView() {
        try {
            // Obtenir l'utilisateur courant
            User utilisateurCourant = Session.getCurrentUser();

            if (utilisateurCourant == null) {
                showAlert("Aucun utilisateur connecté.");
                return;
            }

            // Construire le nom complet
            String nomComplet = utilisateurCourant.getNom() + " " + utilisateurCourant.getPrenom();

            // Récupérer et filtrer les candidatures appartenant à cet utilisateur
            ObservableList<Candidature> list = FXCollections.observableArrayList(
                    new CandidatureService().getAll().stream()
                            .filter(c -> c.getUtilisateur() != null && c.getUtilisateur().equalsIgnoreCase(nomComplet))
                            .collect(Collectors.toList())
            );
            listView.setItems(list);

            listView.setCellFactory(param -> new ListCell<Candidature>() {
                @Override
                protected void updateItem(Candidature candidature, boolean empty) {
                    super.updateItem(candidature, empty);
                    if (empty || candidature == null) {
                        setText(null);
                        setStyle(null);
                    } else {
                        // Affichage des informations de la candidature
                        setText(
                                "ID Candidature : " + candidature.getId() + " || " +
                                        "Offre ID : " + candidature.getOffre().getId() + " || " +
                                        "Statut : " + candidature.getStatut() + " || " +
                                        "Date de Soumission : " + candidature.getDateSoumission()


                        );

                        // Utilisation de toString() pour comparer l'énumération avec des chaînes
                        if ("ACCEPTEE".equalsIgnoreCase(candidature.getStatut().toString())) {
                            setStyle("-fx-background-color: green;");
                        } else if ("REFUSEE".equalsIgnoreCase(candidature.getStatut().toString())) {
                            setStyle("-fx-background-color: RED;");
                        } else {
                            setStyle("-fx-background-color: orange;");
                        }
                    }
                }
            });

        } catch (SQLException e) {
            showAlert("Erreur lors du chargement des candidatures : " + e.getMessage());
        }
    }

    @FXML
    private void supprimerCandidature(ActionEvent event) {
        Candidature selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                new CandidatureService().supprimer(selected.getId());
                refreshListView();
            } catch (SQLException e) {
                showAlert("Erreur suppression : " + e.getMessage());
            }
        } else {
            showAlert("Veuillez sélectionner une candidature à supprimer.");
        }
    }

    @FXML
    public void exporterPDF(ActionEvent actionEvent) {
        Candidature selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Veuillez sélectionner une candidature à exporter.");
            return;
        }

        Document document = new Document();
        try {
            // Le nom du fichier sera TOUJOURS "Candidature.pdf"
            String fileName = "Candidature.pdf";

            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            document.add(new Paragraph("Détails de la Candidature"));
            document.add(new Paragraph("------------------------------"));
            document.add(new Paragraph("ID Candidature : " + selected.getId()));
            document.add(new Paragraph("Offre ID : " + selected.getOffre().getId()));
            document.add(new Paragraph("Statut : " + selected.getStatut()));
            document.add(new Paragraph("Date de Soumission : " + selected.getDateSoumission()));
            document.add(new Paragraph("Utilisateur : " + selected.getUtilisateur()));
            document.add(new Paragraph("CV : " + selected.getCv()));
            document.add(new Paragraph("Lettre de Motivation : " + selected.getLettreMotivation()));

            showAlert("PDF exporté avec succès : " + fileName);

        } catch (DocumentException | IOException e) {
            showAlert("Erreur lors de l'export PDF : " + e.getMessage());
        } finally {
            document.close();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode fictive pour obtenir l'utilisateur courant (ajuster selon ton système d'authentification)
    private User getUtilisateurCourant() {
        // À ajuster selon ta logique d'authentification, par exemple :
        return getCurrentUser(); // Remplace cette ligne par ton mécanisme d'obtention de l'utilisateur courant
    }
}
