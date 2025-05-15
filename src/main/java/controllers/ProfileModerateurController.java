package controllers;

import entities.Moderateur;
import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import utils.Session;
import utils.MyDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfileModerateurController {

    @FXML private ImageView imageProfil;
    @FXML private Label lblNom;
    @FXML private Label lblPrenom;
    @FXML private Label lblEmail;
    @FXML private Label lblTel;
    @FXML private Label lblDomaine;
    @FXML private Label lblSociete;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnClose;

    private Moderateur moderateur;

    @FXML
    public void initialize() {
        // Charger les vraies données du modérateur depuis la base
        User sessionUser = Session.getCurrentUser();
        if (sessionUser != null) {
            try {
                Connection cnx = MyDatabase.getInstance().getCnx();
                PreparedStatement ps = cnx.prepareStatement(
                    "SELECT u.nom, u.prenom, u.email, u.tel, u.domaine, m.societe " +
                    "FROM user u JOIN moderateur m ON u.id = m.id WHERE u.id = ?");
                ps.setInt(1, sessionUser.getId());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    moderateur = new Moderateur();
                    moderateur.setId(sessionUser.getId());
                    moderateur.setNom(rs.getString("nom"));
                    moderateur.setPrenom(rs.getString("prenom"));
                    moderateur.setEmail(rs.getString("email"));
                    moderateur.setTel(rs.getString("tel"));
                    moderateur.setDomaine(rs.getString("domaine"));
                    moderateur.setSociete(rs.getString("societe"));
                } else {
                    moderateur = new Moderateur();
                }
                rs.close();
                ps.close();
            } catch (Exception e) {
                moderateur = new Moderateur();
            }
        } else {
            moderateur = new Moderateur();
        }
        updateLabels();
        loadProfileImage();
    }

    private void updateLabels() {
        if (moderateur != null) {
            lblNom.setText(moderateur.getNom() != null ? moderateur.getNom() : "");
            lblPrenom.setText(moderateur.getPrenom() != null ? moderateur.getPrenom() : "");
            lblEmail.setText(moderateur.getEmail() != null ? moderateur.getEmail() : "");
            lblTel.setText(moderateur.getTel() != null ? moderateur.getTel() : "");
            lblDomaine.setText(moderateur.getDomaine() != null ? moderateur.getDomaine() : "");
            lblSociete.setText(moderateur.getSociete() != null ? moderateur.getSociete() : "");
        }
    }

    private void loadProfileImage() {
        InputStream inputStream = null;
        try {
            inputStream = getClass().getResourceAsStream("/images/profile_icon.jpeg");
            if (inputStream != null) {
                Image image = new Image(inputStream);
                imageProfil.setImage(image);
            }
        } catch (Exception e) {
            imageProfil.setImage(null);
        } finally {
            try { if (inputStream != null) inputStream.close(); } catch (Exception ignored) {}
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleModifier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProfileModerateur.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Modifier le profil modérateur");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Après modification, rafraîchir les labels
            updateLabels();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page de modification.");
        }
    }

    @FXML
    private void handleSupprimer() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Suppression du compte");
        confirm.setHeaderText("Êtes-vous sûr de vouloir supprimer votre compte ?");
        confirm.setContentText("Cette action est irréversible.");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Suppression d'abord dans moderateur, puis user
                    int id = moderateur.getId();
                    java.sql.Connection cnx = MyDatabase.getInstance().getCnx();
                    java.sql.PreparedStatement ps1 = cnx.prepareStatement("DELETE FROM moderateur WHERE id=?");
                    ps1.setInt(1, id);
                    ps1.executeUpdate();
                    java.sql.PreparedStatement ps2 = cnx.prepareStatement("DELETE FROM user WHERE id=?");
                    ps2.setInt(1, id);
                    ps2.executeUpdate();
                    // Déconnexion et fermeture
                    Session.setCurrentUser(null);
                    showAlert(Alert.AlertType.INFORMATION, "Compte supprimé", "Votre compte a bien été supprimé.");
                    Stage stage = (Stage) btnSupprimer.getScene().getWindow();
                    stage.close();
                    // Optionnel : fermer l'application ou revenir à la page de connexion
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression du compte : " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 