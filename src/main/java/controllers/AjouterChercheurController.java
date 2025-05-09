package controllers;

import entities.Chercheur;
import entities.Freelancer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import services.UserService;
import javafx.scene.Parent;

import java.io.File;
import java.util.regex.Pattern;

public class AjouterChercheurController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> sexeComboBox;
    @FXML private TextField telField;
    @FXML private ComboBox<String> domaineCombo;
    @FXML private Label statusLabel;
    @FXML private ImageView photoPreview;
    @FXML private Label cvLabel;

    private final UserService userService = new UserService();
    private File selectedPhoto;
    private File selectedCV;

    // Expressions régulières pour validation
    private static final String NAME_PATTERN = "^[a-zA-Z]{2,}(?:\\s[a-zA-Z]{2,})*$";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    private static final String PHONE_PATTERN = "^[0-9]{8}$";

    @FXML
    private void initialize() {
        sexeComboBox.getItems().addAll("Femme", "Homme", "Autre");
        sexeComboBox.getSelectionModel().selectFirst();
        
        domaineCombo.getItems().addAll(
            "Intelligence Artificielle",
            "Sciences des données",
            "Cybersécurité",
            "Réseaux et télécommunications",
            "Systèmes embarqués"
        );
        domaineCombo.getSelectionModel().selectFirst();
        
        setupFieldValidators();
    }

    private void setupFieldValidators() {
        nomField.textProperty().addListener((obs, oldVal, newVal) ->
                validateField(newVal, NAME_PATTERN, nomField));

        prenomField.textProperty().addListener((obs, oldVal, newVal) ->
                validateField(newVal, NAME_PATTERN, prenomField));

        emailField.textProperty().addListener((obs, oldVal, newVal) ->
                validateField(newVal, EMAIL_PATTERN, emailField));

        passwordField.textProperty().addListener((obs, oldVal, newVal) ->
                validateField(newVal, PASSWORD_PATTERN, passwordField));

        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.equals(passwordField.getText())) {
                confirmPasswordField.setStyle("-fx-border-color: red;");
            } else {
                confirmPasswordField.setStyle("-fx-border-color: green;");
            }
        });

        telField.textProperty().addListener((obs, oldVal, newVal) ->
                validateField(newVal, PHONE_PATTERN, telField));
    }

    private void validateField(String value, String pattern, TextInputControl field) {
        if (!value.matches(pattern)) {
            field.setStyle("-fx-border-color: red;");
        } else {
            field.setStyle("-fx-border-color: green;");
        }
    }

    @FXML
    private void handlePhotoUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", ".jpg", ".png", "*.jpeg"));

        selectedPhoto = fileChooser.showOpenDialog(photoPreview.getScene().getWindow());

        if (selectedPhoto != null) {
            photoPreview.setImage(new Image(selectedPhoto.toURI().toString()));
            photoPreview.setStyle("");
        }
    }

    @FXML
    private void handleCVUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Documents", "*.pdf"));

        selectedCV = fileChooser.showOpenDialog(cvLabel.getScene().getWindow());

        if (selectedCV != null) {
            cvLabel.setText(selectedCV.getName());
            cvLabel.setStyle("-fx-text-fill: black;");
        }
    }

    @FXML
    private void ajouterChercheur(ActionEvent event) {
        if (!validateForm()) {
            showStatus("Veuillez corriger les erreurs dans le formulaire", Color.RED);
            return;
        }

        Chercheur chercheur = createChercheurFromForm();

        try {
            userService.ajouterUser(chercheur);
            showStatus("Freelancer ajouté avec succès !", Color.GREEN);
            clearForm();
        } catch (Exception e) {
            showStatus("Erreur: " + e.getMessage(), Color.RED);
            e.printStackTrace();
        }
    }
    private boolean validateForm() {
        boolean isValid = true;

        if (nomField.getText().isEmpty() || !nomField.getText().matches(NAME_PATTERN)) {
            nomField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (prenomField.getText().isEmpty() || !prenomField.getText().matches(NAME_PATTERN)) {
            prenomField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (emailField.getText().isEmpty() || !emailField.getText().matches(EMAIL_PATTERN)) {
            emailField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (passwordField.getText().isEmpty() || !passwordField.getText().matches(PASSWORD_PATTERN)) {
            passwordField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (!confirmPasswordField.getText().equals(passwordField.getText())) {
            confirmPasswordField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (sexeComboBox.getValue() == null) {
            sexeComboBox.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (telField.getText().isEmpty() || !telField.getText().matches(PHONE_PATTERN)) {
            telField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (domaineCombo.getValue() == null) {
            domaineCombo.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (selectedPhoto == null) {
            photoPreview.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (selectedCV == null) {
            cvLabel.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        return isValid;
    }

    private Chercheur createChercheurFromForm() {
        Chercheur chercheur = new Chercheur();
        chercheur.setId(0); // Initialisation de l'id à 0, il sera auto-incrémenté par la base de données
        chercheur.setNom(nomField.getText());
        chercheur.setPrenom(prenomField.getText());
        chercheur.setEmail(emailField.getText());
        String encryptedPassword = BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt());
        chercheur.setPassword(encryptedPassword);  // Utilisation du mot de passe crypté

        chercheur.setSexe(sexeComboBox.getValue());
        chercheur.setTel(telField.getText());
        chercheur.setPhoto(selectedPhoto.getAbsolutePath());
        chercheur.setCv(selectedCV.getAbsolutePath());
        chercheur.setDomaine(domaineCombo.getValue());
        chercheur.setType("chercheur");
        chercheur.setRoles("[\"ROLE_CHERCHEUR\"]");

        return chercheur;
    }

    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setTextFill(color);
    }

    private void clearForm() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        sexeComboBox.getSelectionModel().selectFirst();
        telField.clear();
        domaineCombo.getSelectionModel().selectFirst();
        photoPreview.setImage(null);
        cvLabel.setText("Aucun CV choisi");
        selectedPhoto = null;
        selectedCV = null;
        resetFieldStyles();
    }

    private void resetFieldStyles() {
        nomField.setStyle("");
        prenomField.setStyle("");
        emailField.setStyle("");
        passwordField.setStyle("");
        confirmPasswordField.setStyle("");
        sexeComboBox.setStyle("");
        telField.setStyle("");
        domaineCombo.setStyle("");
        photoPreview.setStyle("");
        cvLabel.setStyle("-fx-text-fill: black;");
    }

    @FXML
    private void annuler(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/index.fxml"));
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showStatus("Erreur lors de la navigation", Color.RED);
            e.printStackTrace();
        }
    }
}