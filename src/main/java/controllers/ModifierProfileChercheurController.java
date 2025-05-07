package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import entities.Chercheur;
import utils.MyDatabase;
import utils.Session;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModifierProfileChercheurController {
    @FXML private TextField txtNom, txtPrenom, txtEmail, txtTel, txtDomaine, txtCV, txtPhoto;
    @FXML private Button btnAnnuler, btnEnregistrer;
    
    private Chercheur chercheur;
    private String selectedCVPath;
    private String selectedPhotoPath;
    
    public void setChercheur(Chercheur chercheur) {
        this.chercheur = chercheur;
        populateFields();
    }
    
    private void populateFields() {
        if (chercheur != null) {
            txtNom.setText(chercheur.getNom());
            txtPrenom.setText(chercheur.getPrenom());
            txtEmail.setText(chercheur.getEmail());
            txtTel.setText(chercheur.getTel());
            txtDomaine.setText(chercheur.getDomaine());
            txtCV.setText(chercheur.getCv());
            txtPhoto.setText(chercheur.getPhoto());
        }
    }
    
    @FXML
    private void handleParcourirCV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un CV");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        
        File file = fileChooser.showOpenDialog(txtCV.getScene().getWindow());
        if (file != null) {
            selectedCVPath = file.getAbsolutePath();
            txtCV.setText(file.getName());
        }
    }
    
    @FXML
    private void handleParcourirPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une photo");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        
        File file = fileChooser.showOpenDialog(txtPhoto.getScene().getWindow());
        if (file != null) {
            selectedPhotoPath = file.getAbsolutePath();
            txtPhoto.setText(file.getName());
        }
    }
    
    @FXML
    private void handleEnregistrer() {
        if (!validateFields()) {
            return;
        }
        
        updateChercheur();
    }
    
    private boolean validateFields() {
        if (txtNom.getText().isEmpty() || txtPrenom.getText().isEmpty() || 
            txtEmail.getText().isEmpty() || txtTel.getText().isEmpty() || 
            txtDomaine.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs obligatoires.");
            return false;
        }
        return true;
    }
    
    private void updateChercheur() {
        Connection connection = null;
        PreparedStatement stmtUser = null;
        PreparedStatement stmtChercheur = null;
        
        try {
            connection = MyDatabase.getInstance().getCnx();
            connection.setAutoCommit(false);
            
            // Mise à jour de la table user
            String queryUser = "UPDATE user SET nom = ?, prenom = ?, email = ?, tel = ? WHERE id = ?";
            stmtUser = connection.prepareStatement(queryUser);
            stmtUser.setString(1, txtNom.getText());
            stmtUser.setString(2, txtPrenom.getText());
            stmtUser.setString(3, txtEmail.getText());
            stmtUser.setString(4, txtTel.getText());
            stmtUser.setInt(5, chercheur.getId());
            
            // Mise à jour de la table chercheur
            String queryChercheur = "UPDATE chercheur SET domaine = ?, cv = ?, photo = ? WHERE user_id = ?";
            stmtChercheur = connection.prepareStatement(queryChercheur);
            stmtChercheur.setString(1, txtDomaine.getText());
            
            // Gestion du CV
            String cvPath = selectedCVPath;
            if (cvPath == null) {
                cvPath = chercheur.getCv();
            }
            if (cvPath == null) {
                cvPath = ""; // Valeur par défaut si aucun CV n'est défini
            }
            stmtChercheur.setString(2, cvPath);
            
            // Gestion de la photo
            String photoPath = selectedPhotoPath;
            if (photoPath == null) {
                photoPath = chercheur.getPhoto();
            }
            if (photoPath == null) {
                photoPath = ""; // Valeur par défaut si aucune photo n'est définie
            }
            stmtChercheur.setString(3, photoPath);
            
            stmtChercheur.setInt(4, chercheur.getId());
            
            // Exécution des mises à jour
            int rowsAffectedUser = stmtUser.executeUpdate();
            int rowsAffectedChercheur = stmtChercheur.executeUpdate();
            
            if (rowsAffectedUser > 0 && rowsAffectedChercheur > 0) {
                connection.commit();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Profil modifié avec succès.");
                
                // Mise à jour de l'objet Chercheur dans la session
                chercheur.setNom(txtNom.getText());
                chercheur.setPrenom(txtPrenom.getText());
                chercheur.setEmail(txtEmail.getText());
                chercheur.setTel(txtTel.getText());
                chercheur.setDomaine(txtDomaine.getText());
                chercheur.setCv(cvPath);
                chercheur.setPhoto(photoPath);
                
                closeWindow();
            } else {
                connection.rollback();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier le profil.");
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la modification du profil.");
        } finally {
            try {
                if (stmtUser != null) stmtUser.close();
                if (stmtChercheur != null) stmtChercheur.close();
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