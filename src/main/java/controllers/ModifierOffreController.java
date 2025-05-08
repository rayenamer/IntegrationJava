package controllers;

import entities.Offre;
import entities.TypeContrat;
import entities.TypeOffre;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.OffreService;
import services.TypeContratService;
import services.TypeOffreService;

import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ModifierOffreController implements Initializable {

    @FXML
    private ComboBox<String> typeContratCB;
    @FXML
    private ComboBox<String> typeOffreCB;
    @FXML
    private TextField posteTF;
    @FXML
    private TextField entrepriseTF;
    @FXML
    private TextField localisationTF;
    @FXML
    private TextField salaireTF;
    @FXML
    private CheckBox disponibleCB;
    @FXML
    private TextField imageTF;
    @FXML
    private TextField utilisateurTF;

    private Map<String, TypeContrat> mapTypeContrat = new HashMap<>();
    private Map<String, TypeOffre> mapTypeOffre = new HashMap<>();

    private Offre currentOffre;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Load TypeContrat
            TypeContratService tcs = new TypeContratService();
            List<TypeContrat> listContrats = tcs.recuperer();
            for (TypeContrat tc : listContrats) {
                mapTypeContrat.put(tc.getNom(), tc);
            }
            typeContratCB.setItems(FXCollections.observableArrayList(mapTypeContrat.keySet()));

            // Load TypeOffre
            TypeOffreService tos = new TypeOffreService();
            List<TypeOffre> listOffres = tos.recuperer();
            for (TypeOffre to : listOffres) {
                mapTypeOffre.put(to.getNom(), to);
            }
            typeOffreCB.setItems(FXCollections.observableArrayList(mapTypeOffre.keySet()));

            disponibleCB.setSelected(true);

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur de chargement", "Impossible de charger les données depuis la base.");
        }
    }

    public void setOffre(Offre offre) {
        this.currentOffre = offre;
        posteTF.setText(offre.getNomposte());
        entrepriseTF.setText(offre.getEntreprise());
        localisationTF.setText(offre.getLocalisation());
        salaireTF.setText(String.valueOf(offre.getSalaire()));
        disponibleCB.setSelected(offre.isDisponibilite());
        imageTF.setText(offre.getImage());
        utilisateurTF.setText(offre.getUtilisateur());
        if (offre.getTypeContrat() != null) {
            typeContratCB.setValue(offre.getTypeContrat().getNom());
        }
        if (offre.getTypeOffre() != null) {
            typeOffreCB.setValue(offre.getTypeOffre().getNom());
        }
    }

    @FXML
    public void modifierOffre(ActionEvent actionEvent) {
        OffreService os = new OffreService();
        try {
            if (typeContratCB.getValue() == null || typeOffreCB.getValue() == null ||
                    posteTF.getText().trim().isEmpty() || entrepriseTF.getText().trim().isEmpty() ||
                    localisationTF.getText().trim().isEmpty() || salaireTF.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Tous les champs obligatoires doivent être remplis.");
            }

            double salaire;
            try {
                salaire = Double.parseDouble(salaireTF.getText().trim());
                if (salaire <= 0) throw new IllegalArgumentException("Le salaire doit être positif.");
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Le salaire doit être un nombre valide.");
            }

            TypeContrat typeContrat = mapTypeContrat.get(typeContratCB.getValue());
            TypeOffre typeOffre = mapTypeOffre.get(typeOffreCB.getValue());

            if (typeContrat == null || typeOffre == null) {
                throw new IllegalArgumentException("Type de contrat ou d'offre non valide.");
            }

            currentOffre.setTypeContrat(typeContrat);
            currentOffre.setTypeOffre(typeOffre);
            currentOffre.setNomposte(posteTF.getText().trim());
            currentOffre.setEntreprise(entrepriseTF.getText().trim());
            currentOffre.setLocalisation(localisationTF.getText().trim());
            currentOffre.setSalaire(salaire);
            currentOffre.setDisponibilite(disponibleCB.isSelected());
            currentOffre.setImage(imageTF.getText().trim());
            currentOffre.setUtilisateur(utilisateurTF.getText().trim());

            os.modifier(currentOffre);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Offre modifiée !");
            alert.setContentText("L'offre a été modifiée avec succès.");
            alert.showAndWait();

        } catch (IllegalArgumentException e) {
            showError("Erreur lors de la modification de l'offre", e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
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
}
