package controllers;

import entities.Freelancer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.MyDatabase;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModifierProfileController {

    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtTel;
    @FXML
    private TextField txtAdresse;
    @FXML
    private TextField txtDomaine;
    @FXML
    private TextField txtAnneesExperience;
    @FXML
    private TextField txtCV;
    @FXML
    private Button btnAnnuler;
    @FXML
    private Button btnEnregistrer;

    private Freelancer freelancer;
    private String selectedCVPath;

    public void setFreelancer(Freelancer freelancer) {
        this.freelancer = freelancer;
        populateFields();
    }

    private void populateFields() {
        if (freelancer != null) {
            txtNom.setText(freelancer.getNom());
            txtPrenom.setText(freelancer.getPrenom());
            txtEmail.setText(freelancer.getEmail());
            txtTel.setText(freelancer.getTel());
            txtAdresse.setText(freelancer.getAdresse());
            txtDomaine.setText(freelancer.getDomaine());
            txtAnneesExperience.setText(String.valueOf(freelancer.getAnnees_Experience()));
            txtCV.setText(freelancer.getCv() != null ? freelancer.getCv() : "");
        }
    }

    @FXML
    private void handleParcourirCV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un CV");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Documents PDF", "*.pdf")
        );

        File selectedFile = fileChooser.showOpenDialog(btnAnnuler.getScene().getWindow());
        if (selectedFile != null) {
            selectedCVPath = selectedFile.getAbsolutePath();
            txtCV.setText(selectedFile.getName());
        }
    }

    @FXML
    private void handleEnregistrer() {
        if (validateFields()) {
            updateFreelancer();
        }
    }

    private boolean validateFields() {
        if (txtNom.getText().isEmpty() || txtPrenom.getText().isEmpty() || 
            txtEmail.getText().isEmpty() || txtTel.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs obligatoires.");
            return false;
        }
        return true;
    }

    private void updateFreelancer() {
        Connection connection = null;
        PreparedStatement stmtUser = null;
        PreparedStatement stmtFreelancer = null;

        try {
            connection = MyDatabase.getInstance().getCnx();
            connection.setAutoCommit(false); // Démarrer une transaction

            // Mise à jour de la table user
            String queryUser = "UPDATE user SET nom = ?, prenom = ?, email = ?, tel = ?, " +
                             "domaine = ? WHERE id = ?";
            
            System.out.println("Mise à jour du profil pour l'utilisateur ID: " + freelancer.getId());
            System.out.println("Nouvelles valeurs:");
            System.out.println("Nom: " + txtNom.getText());
            System.out.println("Prénom: " + txtPrenom.getText());
            System.out.println("Email: " + txtEmail.getText());
            System.out.println("Téléphone: " + txtTel.getText());
            System.out.println("Adresse: " + txtAdresse.getText());
            System.out.println("Domaine: " + txtDomaine.getText());
            System.out.println("Années d'expérience: " + txtAnneesExperience.getText());
            
            stmtUser = connection.prepareStatement(queryUser);
            stmtUser.setString(1, txtNom.getText());
            stmtUser.setString(2, txtPrenom.getText());
            stmtUser.setString(3, txtEmail.getText());
            stmtUser.setString(4, txtTel.getText());
            stmtUser.setString(5, txtDomaine.getText());
            stmtUser.setInt(6, freelancer.getId());

            int rowsAffectedUser = stmtUser.executeUpdate();
            System.out.println("Nombre de lignes affectées dans user: " + rowsAffectedUser);

            // Mise à jour de la table freelancer
            String queryFreelancer = "UPDATE freelancer SET adresse = ?, annees_experience = ?, cv = ? WHERE user_id = ?";
            stmtFreelancer = connection.prepareStatement(queryFreelancer);
            stmtFreelancer.setString(1, txtAdresse.getText());
            stmtFreelancer.setInt(2, Integer.parseInt(txtAnneesExperience.getText()));
            
            // Gestion du CV pour éviter les valeurs NULL
            String cvPath = selectedCVPath;
            if (cvPath == null) {
                cvPath = freelancer.getCv();
            }
            if (cvPath == null) {
                cvPath = ""; // Valeur par défaut si aucun CV n'est défini
            }
            stmtFreelancer.setString(3, cvPath);
            stmtFreelancer.setInt(4, freelancer.getId());

            int rowsAffectedFreelancer = stmtFreelancer.executeUpdate();
            System.out.println("Nombre de lignes affectées dans freelancer: " + rowsAffectedFreelancer);

            // Valider la transaction
            connection.commit();
            
            if (rowsAffectedUser > 0 || rowsAffectedFreelancer > 0) {
                // Mettre à jour l'objet Freelancer en session
                freelancer.setNom(txtNom.getText());
                freelancer.setPrenom(txtPrenom.getText());
                freelancer.setEmail(txtEmail.getText());
                freelancer.setTel(txtTel.getText());
                freelancer.setAdresse(txtAdresse.getText());
                freelancer.setDomaine(txtDomaine.getText());
                freelancer.setAnnees_Experience(Integer.parseInt(txtAnneesExperience.getText()));
                freelancer.setCv(cvPath);
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Profil mis à jour avec succès.");
                closeWindow();
            } else {
                connection.rollback();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour le profil.");
            }
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            System.err.println("Erreur SQL lors de la mise à jour: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la mise à jour du profil: " + e.getMessage());
        } finally {
            try {
                if (stmtUser != null) stmtUser.close();
                if (stmtFreelancer != null) stmtFreelancer.close();
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAnnuler() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 