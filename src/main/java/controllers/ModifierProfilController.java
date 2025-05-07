package controllers;

import entities.Freelancer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.FreelancerService;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ModifierProfilController implements Initializable {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> sexeComboBox;
    @FXML private TextField telField;
    @FXML private ImageView photoPreview;
    @FXML private Label cvLabel;
    @FXML private TextField adresseField;
    @FXML private TextField annees_experienceField;
    @FXML private Label statusLabel;

    private File photoFile;
    private File cvFile;
    private Freelancer freelancer;
    private boolean modificationConfirmee = false;

    private final FreelancerService freelancerService = new FreelancerService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sexeComboBox.getItems().addAll("Femme", "Homme", "Autre");
    }

    public void setFreelancer(Freelancer freelancer) {
        this.freelancer = freelancer;
        remplirChamps();
    }

    private void remplirChamps() {
        if (freelancer != null) {
            nomField.setText(freelancer.getNom());
            prenomField.setText(freelancer.getPrenom());
            emailField.setText(freelancer.getEmail());
            sexeComboBox.setValue(freelancer.getSexe());
            telField.setText(freelancer.getTel());
            adresseField.setText(freelancer.getAdresse());
            annees_experienceField.setText(String.valueOf(freelancer.getAnnees_Experience()));

            if (freelancer.getPhoto() != null && !freelancer.getPhoto().isEmpty()) {
                Image img = new Image("file:" + freelancer.getPhoto());
                photoPreview.setImage(img);
            }

            cvLabel.setText(freelancer.getCv() != null ? new File(freelancer.getCv()).getName() : "Aucun fichier");
        }
    }

    @FXML
    private void handlePhotoUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une photo");
        photoFile = fileChooser.showOpenDialog(null);
        if (photoFile != null) {
            photoPreview.setImage(new Image(photoFile.toURI().toString()));
        }
    }

    @FXML
    private void handleCVUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un CV");
        cvFile = fileChooser.showOpenDialog(null);
        if (cvFile != null) {
            cvLabel.setText(cvFile.getName());
        }
    }

    @FXML
    private void modifierFreelancer() {
        if (!validerChamps()) return;

        // Mise à jour des données
        freelancer.setNom(nomField.getText());
        freelancer.setPrenom(prenomField.getText());
        freelancer.setEmail(emailField.getText());
        freelancer.setSexe(sexeComboBox.getValue());
        freelancer.setTel(telField.getText());
        freelancer.setAdresse(adresseField.getText());
        freelancer.setAnnees_Experience(Integer.parseInt(annees_experienceField.getText()));

        if (!passwordField.getText().isEmpty()) {
            freelancer.setPassword(passwordField.getText());
        }

        if (photoFile != null) {
            freelancer.setPhoto(photoFile.getAbsolutePath());
        }

        if (cvFile != null) {
            freelancer.setCv(cvFile.getAbsolutePath());
        }

        boolean success = freelancerService.ModifierProfilController(freelancer);

        if (success) {
            modificationConfirmee = true;
            closeWindow();
        } else {
            statusLabel.setText("Erreur lors de la modification.");
        }
    }

    private boolean validerChamps() {
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            statusLabel.setText("Les mots de passe ne correspondent pas.");
            return false;
        }

        if (annees_experienceField.getText().isEmpty() || !annees_experienceField.getText().matches("\\d+")) {
            statusLabel.setText("L'expérience doit être un nombre valide.");
            return false;
        }

        return true;
    }

    @FXML
    private void annuler() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    public boolean isModificationConfirmee() {
        return modificationConfirmee;
    }
}
