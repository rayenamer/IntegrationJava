package controllers;

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

public class AjouterFreelancerController {

    // Champs du formulaire
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> sexeComboBox;
    @FXML private TextField telField;
    @FXML private TextField adresseField;
    @FXML private TextField experienceField;
    @FXML private Label statusLabel;
    @FXML private ImageView photoPreview;

    @FXML private Label cvLabel; // Changé de Text à Label

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
        // Initialisation de la ComboBox
        sexeComboBox.getItems().addAll("Femme", "Homme", "Autre");
        sexeComboBox.getSelectionModel().selectFirst();


        // Configuration des validateurs
        setupFieldValidators();
    }

    private void setupFieldValidators() {
        // Validation en temps réel pour chaque champ
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
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg"));

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
    private void ajouterFreelancer(ActionEvent event) {
        if (!validateForm()) {
            showStatus("Veuillez corriger les erreurs dans le formulaire", Color.RED);
            return;
        }

        Freelancer freelancer = createFreelancerFromForm();

        try {
            userService.ajouterUser(freelancer);
            showStatus("Freelancer ajouté avec succès !", Color.GREEN);
            clearForm();
        } catch (Exception e) {
            showStatus("Erreur: " + e.getMessage(), Color.RED);
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Validation des champs obligatoires
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

        if (adresseField.getText().isEmpty()) {
            adresseField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        try {
            int exp = Integer.parseInt(experienceField.getText());
            if (exp < 0) {
                experienceField.setStyle("-fx-border-color: red;");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            experienceField.setStyle("-fx-border-color: red;");
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

    private Freelancer createFreelancerFromForm() {
        Freelancer freelancer = new Freelancer();
        freelancer.setNom(nomField.getText());
        freelancer.setPrenom(prenomField.getText());
        freelancer.setEmail(emailField.getText());
        String encryptedPassword = BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt());
        freelancer.setPassword(encryptedPassword);
        freelancer.setSexe(sexeComboBox.getValue());
        freelancer.setTel(telField.getText());
        freelancer.setPhoto(selectedPhoto.getAbsolutePath());
        freelancer.setCv(selectedCV.getAbsolutePath());
        freelancer.setAdresse(adresseField.getText());
        freelancer.setType("freelancer");
        freelancer.setRoles("[\"ROLE_FREELANCER\"]");
        freelancer.setAnnees_Experience(Integer.parseInt(experienceField.getText()));

        return freelancer;
    }

    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setTextFill(color);
    }

    private void clearForm() {
        // Réinitialisation des champs
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        sexeComboBox.getSelectionModel().selectFirst();
        telField.clear();
        adresseField.clear();
        experienceField.clear();
        photoPreview.setImage(null);
        cvLabel.setText("Aucun CV choisi");
        selectedPhoto = null;
        selectedCV = null;

        // Réinitialisation des styles
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
        adresseField.setStyle("");
        experienceField.setStyle("");
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