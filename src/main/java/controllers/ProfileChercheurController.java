// src/main/java/controllers/ProfileChercheurController.java
package controllers;

import entities.Chercheur;
import entities.Freelancer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import services.ChercheurService;
import utils.MyDatabase;
import utils.Session;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.awt.Desktop;

public class ProfileChercheurController {

    @FXML private TableView<Chercheur> profileTable;
    @FXML private TableColumn<Chercheur, String> colNom;
    @FXML private TableColumn<Chercheur, String> colPrenom;
    @FXML private TableColumn<Chercheur, String> colEmail;
    @FXML private TableColumn<Chercheur, String> colDomaine;
    @FXML private TableColumn<Chercheur, String> colTel;

    @FXML private ImageView imageProfil;
    @FXML private Label lblNom, lblPrenom, lblEmail, lblTel, lblDomaine, lblCV;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnClose;

    private final ChercheurService chercheurService = new ChercheurService();
    private String userEmail;
    private Chercheur chercheur;

    @FXML
    public void initialize() {
        initializeTable();
    }

    public void setChercheur(Chercheur chercheur) {
        this.chercheur = chercheur;
        updateLabels();
        loadProfileImage();
    }

    private void updateLabels() {
        if (chercheur != null) {
            lblNom.setText(chercheur.getNom() != null ? chercheur.getNom() : "");
            lblPrenom.setText(chercheur.getPrenom() != null ? chercheur.getPrenom() : "");
            lblEmail.setText(chercheur.getEmail() != null ? chercheur.getEmail() : "");
            lblTel.setText(chercheur.getTel() != null ? chercheur.getTel() : "");
            lblDomaine.setText(chercheur.getDomaine() != null ? chercheur.getDomaine() : "");
            lblCV.setText(chercheur.getCv() != null ? new File(chercheur.getCv()).getName() : "Aucun CV");
        }
    }

    private void loadProfileImage() {
        if (chercheur != null && chercheur.getPhoto() != null && !chercheur.getPhoto().isEmpty()) {
            try {
                String photoPath = chercheur.getPhoto();
                // Si le chemin commence par 'c:', ajouter le préfixe 'file:///' pour le rendre valide
                if (photoPath.startsWith("c:")) {
                    photoPath = "file:///" + photoPath.replace("\\", "/");
                }
                Image image = new Image(photoPath);
                imageProfil.setImage(image);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
                // Charger une image par défaut en cas d'erreur
                loadDefaultImage();
            }
        } else {
            loadDefaultImage();
        }
    }

    private void loadDefaultImage() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-profile.png"));
            imageProfil.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image par défaut : " + e.getMessage());
        }
    }

    private void initializeTable() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDomaine.setCellValueFactory(new PropertyValueFactory<>("domaine"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("tel"));
    }

    private void refreshTable() {
        if (userEmail == null || userEmail.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Email utilisateur invalide.");
            return;
        }
        ObservableList<Chercheur> data = FXCollections.observableArrayList(
                chercheurService.afficherChercheurByEmail(userEmail)
        );
        profileTable.setItems(data);
    }

    @FXML
    private void handleModifier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProfileChercheur.fxml"));
            Parent root = loader.load();
            ModifierProfileChercheurController controller = loader.getController();
            controller.setChercheur(chercheur);

            Stage stage = new Stage();
            stage.setTitle("Modifier le profil");
            stage.setScene(new Scene(root));
            stage.show();

            // Ferme cette fenêtre
            ((Stage) btnModifier.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page de modification.");
        }
    }

    @FXML
    private void handleSupprimer() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer votre profil ? Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                deleteChercheur();
            }
        });
    }

    private void deleteChercheur() {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = MyDatabase.getInstance().getCnx();
            String query = "DELETE FROM user WHERE id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, chercheur.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Profil supprimé avec succès.");
                Session.clear();
                redirectToLogin();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le profil.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la suppression du profil.");
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void redirectToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/PageConnexion.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Connexion");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) btnClose.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleTelechargerCV() {
        if (chercheur != null && chercheur.getCv() != null && !chercheur.getCv().isEmpty()) {
            try {
                File file = new File(chercheur.getCv());
                if (file.exists()) {
                    Desktop.getDesktop().open(file);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Le fichier CV n'existe pas.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le fichier CV.");
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Information", "Aucun CV disponible.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
