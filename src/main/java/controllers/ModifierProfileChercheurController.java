package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import entities.Chercheur;
import utils.MyDatabase;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModifierProfileChercheurController {

    @FXML private TextField txtNom, txtPrenom, txtEmail, txtTel, txtDomaine, txtCV, txtPhoto;
    @FXML private Button btnAnnuler, btnEnregistrer;
    @FXML
    private ImageView imageView;
    private Chercheur chercheur;
    private String selectedCVPath = null;
    private String selectedPhotoPath = null;

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
            txtCV.setText(chercheur.getCv() != null ? new File(chercheur.getCv()).getName() : "");
            txtPhoto.setText(chercheur.getPhoto() != null ? new File(chercheur.getPhoto()).getName() : "");

            // Attempt to load the photo or fall back to the default image
            loadImage();
        }
    }

    private void loadImage() {
        String photoPath = chercheur.getPhoto(); // This is the path to the user's photo
        InputStream inputStream = null;

        try {
                if (photoPath != null && !photoPath.isEmpty()) {
                    File photoFile = new File(photoPath);
                    if (photoFile.exists()) {
                        inputStream = new FileInputStream(photoFile);
                    }
                }

                // If the photo path is not found or the image file doesn't exist, use the default image
                if (inputStream == null) {
                    System.err.println("Image non trouvée, chargement d'une image par défaut");
                    inputStream = getClass().getResourceAsStream("/images/profile_icon.jpeg");
                }

                // Set the image to the ImageView
                if (inputStream != null) {
                    Image image = new Image(inputStream);
                    imageView.setImage(image);
                }

            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception appropriately
            } finally {
                // Ensure the InputStream is closed after use
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }



    @FXML
    private void handleParcourirCV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un CV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
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
                new FileChooser.ExtensionFilter("Fichiers image", "*.png", "*.jpg", "*.jpeg")
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

            // Requête pour la table user
            String queryUser = "UPDATE user SET nom = ?, prenom = ?, email = ?, tel = ? WHERE id = ?";
            stmtUser = connection.prepareStatement(queryUser);
            stmtUser.setString(1, txtNom.getText());
            stmtUser.setString(2, txtPrenom.getText());
            stmtUser.setString(3, txtEmail.getText());
            stmtUser.setString(4, txtTel.getText());
            stmtUser.setInt(5, chercheur.getId());

            // Requête pour la table chercheur
            String queryChercheur = "UPDATE chercheur SET domaine = ?, cv = ?, photo = ? WHERE id = ?";
            stmtChercheur = connection.prepareStatement(queryChercheur);
            stmtChercheur.setString(1, txtDomaine.getText());

            // Gérer les chemins si non modifiés
            String cvPath = selectedCVPath != null ? selectedCVPath : (chercheur.getCv() != null ? chercheur.getCv() : "");
            String photoPath = selectedPhotoPath != null ? selectedPhotoPath : (chercheur.getPhoto() != null ? chercheur.getPhoto() : "");

            stmtChercheur.setString(2, cvPath);
            stmtChercheur.setString(3, photoPath);
            stmtChercheur.setInt(4, chercheur.getId());

            int rowsUser = stmtUser.executeUpdate();
            int rowsChercheur = stmtChercheur.executeUpdate();

            if (rowsUser > 0 && rowsChercheur > 0) {
                connection.commit();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Profil modifié avec succès.");

                // Mettre à jour l'objet chercheur
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
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour du profil.");
            }

        } catch (SQLException e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la modification.");
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
