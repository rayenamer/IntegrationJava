package controllers;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import entities.Candidature;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import services.CandidatureService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

public class AfficherCandidatureController {

    @FXML
    private ListView<Candidature> listView;

    @FXML
    private Button supprimerBtn;

    @FXML
    private Button exportPdfBtn;

    @FXML
    public void initialize() {
        refreshListView();
    }

    private void refreshListView() {
        try {
            ObservableList<Candidature> list = FXCollections.observableArrayList(new CandidatureService().getAll());
            listView.setItems(list);
        } catch (SQLException e) {
            showAlert("Erreur chargement des candidatures : " + e.getMessage());
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
}