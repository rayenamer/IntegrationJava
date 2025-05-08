package controllers;

import entities.Moderateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import services.MailService;
import services.ModerateurService;
import javafx.scene.Parent;

import java.util.Optional;
import java.util.regex.Pattern;

public class AjouterModerateurController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> sexeComboBox;
    @FXML private TextField telField;
    @FXML private TextField societeField;
    @FXML private Label statusLabel;

    private final ModerateurService moderateurService = new ModerateurService();

    // Expressions régulières pour validation
    private static final String NAME_PATTERN = "^[a-zA-Z]{2,}(?:\\s[a-zA-Z]{2,})*$";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    private static final String PHONE_PATTERN = "^[0-9]{8}$";

    @FXML
    private void initialize() {
        sexeComboBox.getItems().addAll("Femme", "Homme", "Autre");
        sexeComboBox.getSelectionModel().selectFirst();
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
    private void ajouterModerateur(ActionEvent event) {
        try {
            // Validation du formulaire
            if (!validateForm()) {
                showStatus("Veuillez corriger les erreurs dans le formulaire", Color.RED);
                return;
            }

            // Vérification de l'email avant d'envoyer le code
            try {
                if (moderateurService.emailExists(emailField.getText())) {
                    showStatus("Un utilisateur avec cet email existe déjà.", Color.RED);
                    emailField.setStyle("-fx-border-color: red;");
                    return;
                }
            } catch (RuntimeException e) {
                showStatus("Erreur lors de la vérification de l'email: " + e.getMessage(), Color.RED);
                return;
            }

            // Génération du code de vérification
            String code = utils.CodeGenerator.genererCode();
            System.out.println("Code généré : " + code);

            // Envoi du mail
            boolean envoyerMail = MailService.envoyerMail(emailField.getText(), code);
            if (!envoyerMail) {
                showStatus("Erreur lors de l'envoi du mail.", Color.RED);
                return;
            }

            // Boîte de dialogue pour la saisie du code utilisateur
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Vérification Email");
            dialog.setHeaderText("Un code de vérification a été envoyé à " + emailField.getText());
            dialog.setContentText("Veuillez entrer le code :");

            Optional<String> result = dialog.showAndWait();

            // Si l'utilisateur n'a rien saisi ou a annulé
            if (!result.isPresent()) {
                showStatus("Saisie annulée. Inscription interrompue.", Color.RED);
                return;
            }

            String codeSaisi = result.get().trim();

            // Vérification du code saisi
            if (!codeSaisi.equals(code)) {
                showStatus("Code incorrect. Inscription annulée.", Color.RED);
                return;
            }

            // Création du modérateur à partir du formulaire
            Moderateur moderateur = createModerateurFromForm();

            // Ajout du modérateur via le service
            boolean success = moderateurService.ajouterModerateur(moderateur);
            if (success) {
                showStatus("Modérateur ajouté avec succès !", Color.GREEN);
                clearForm();
            }
        } catch (RuntimeException e) {
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

        if (societeField.getText().isEmpty()) {
            societeField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        return isValid;
    }

    private Moderateur createModerateurFromForm() {
        Moderateur moderateur = new Moderateur();
        moderateur.setNom(nomField.getText());
        moderateur.setPrenom(prenomField.getText());
        moderateur.setEmail(emailField.getText());
        String encryptedPassword = BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt());
        moderateur.setPassword(encryptedPassword);  // Utilisation du mot de passe crypté

        moderateur.setSexe(sexeComboBox.getValue());
        moderateur.setTel(telField.getText());
        moderateur.setSociete(societeField.getText());
        moderateur.setType("moderateur");
        moderateur.setRoles("[\"ROLE_MODERATEUR\"]");

        return moderateur;
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
        societeField.clear();
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
        societeField.setStyle("");
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