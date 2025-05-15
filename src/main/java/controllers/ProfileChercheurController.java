package controllers;

import entities.Chercheur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import utils.MyDatabase;
import utils.Session;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProfileChercheurController {

    @FXML
    private ImageView imageProfil;
    @FXML
    private Label lblNom;
    @FXML
    private Label lblPrenom;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblTel;
    @FXML
    private Label lblDomaine;
    @FXML
    private Label lblSpecialite;
    @FXML
    private Label lblCV;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnSupprimer;
    @FXML
    private Button btnClose;

    private Chercheur chercheur;

    public void setChercheur(Chercheur chercheur) {
        if (chercheur != null) {
            try {
                // Récupérer les données directement depuis la base de données
                Connection cnx = MyDatabase.getInstance().getCnx();
                PreparedStatement ps = cnx.prepareStatement(
                    "SELECT u.id, u.nom, u.prenom, u.email, u.tel, u.domaine, " +
                    "c.specialite, c.cv, c.photo " +
                    "FROM user u " +
                    "INNER JOIN chercheur c ON u.id = c.id_user " +
                    "WHERE u.id = ?");
                ps.setInt(1, chercheur.getId());
                java.sql.ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    // Créer un nouvel objet Chercheur avec les données récupérées
                    this.chercheur = new Chercheur();
                    this.chercheur.setId(rs.getInt("id"));
                    this.chercheur.setNom(rs.getString("nom"));
                    this.chercheur.setPrenom(rs.getString("prenom"));
                    this.chercheur.setEmail(rs.getString("email"));
                    this.chercheur.setTel(rs.getString("tel"));
                    this.chercheur.setDomaine(rs.getString("domaine"));
                    this.chercheur.setSpecialite(rs.getString("specialite"));
                    this.chercheur.setCv(rs.getString("cv"));
                    this.chercheur.setPhoto(rs.getString("photo"));
                    
                    // Debug: Afficher les valeurs récupérées
                    System.out.println("Données récupérées pour le chercheur ID " + chercheur.getId() + ":");
                    System.out.println("Nom: " + this.chercheur.getNom());
                    System.out.println("Prénom: " + this.chercheur.getPrenom());
                    System.out.println("Email: " + this.chercheur.getEmail());
                    System.out.println("Téléphone: " + this.chercheur.getTel());
                    System.out.println("Domaine: " + this.chercheur.getDomaine());
                    System.out.println("Spécialité: " + this.chercheur.getSpecialite());
                    System.out.println("CV: " + this.chercheur.getCv());
                    System.out.println("Photo: " + this.chercheur.getPhoto());
                    
                    // Mettre à jour l'interface
                    updateLabels();
                    loadProfileImage();
                } else {
                    System.err.println("Aucun chercheur trouvé avec l'ID: " + chercheur.getId());
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les données du profil.");
                }
                
                rs.close();
                ps.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la récupération du chercheur : " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les données du profil.");
            }
        }
    }

    private void updateLabels() {
        if (chercheur != null) {
            try {
                // Mettre à jour les labels avec les données
                lblNom.setText(chercheur.getNom() != null ? chercheur.getNom() : "");
                lblPrenom.setText(chercheur.getPrenom() != null ? chercheur.getPrenom() : "");
                lblEmail.setText(chercheur.getEmail() != null ? chercheur.getEmail() : "");
                lblTel.setText(chercheur.getTel() != null ? chercheur.getTel() : "");
                lblDomaine.setText(chercheur.getDomaine() != null ? chercheur.getDomaine() : "");
                lblSpecialite.setText(chercheur.getSpecialite() != null ? chercheur.getSpecialite() : "");
                lblCV.setText(chercheur.getCv() != null ? new File(chercheur.getCv()).getName() : "Aucun CV");

                // Debug: Afficher les valeurs qui seront affichées
                System.out.println("Valeurs affichées dans les labels:");
                System.out.println("Nom: " + lblNom.getText());
                System.out.println("Prénom: " + lblPrenom.getText());
                System.out.println("Email: " + lblEmail.getText());
                System.out.println("Téléphone: " + lblTel.getText());
                System.out.println("Domaine: " + lblDomaine.getText());
                System.out.println("Spécialité: " + lblSpecialite.getText());
                System.out.println("CV: " + lblCV.getText());
            } catch (Exception e) {
                System.err.println("Erreur lors de la mise à jour des labels : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("L'objet chercheur est null");
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les données du profil.");
        }
    }

    private void loadProfileImage() {
        if (chercheur != null && chercheur.getPhoto() != null && !chercheur.getPhoto().isEmpty()) {
            try {
                String photoPath = chercheur.getPhoto();
                Image image = null;
                
                // Vérifier si le chemin est absolu ou relatif
                if (photoPath.matches("^[a-zA-Z]:\\\\.*") || photoPath.startsWith("/")) {
                    // Chemin absolu
                    File photoFile = new File(photoPath);
                    if (photoFile.exists()) {
                        image = new Image("file:" + photoPath.replace("\\", "/"));
                    }
                } else {
                    // Chemin relatif
                    java.net.URL url = getClass().getResource("/images/" + photoPath);
                    if (url != null) {
                        image = new Image(url.toExternalForm());
                    }
                }
                
                if (image != null && !image.isError()) {
                    imageProfil.setImage(image);
                } else {
                    System.err.println("Impossible de charger l'image: " + photoPath);
                    imageProfil.setImage(getDefaultProfileImage());
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de la photo de profil : " + e.getMessage());
                e.printStackTrace();
                imageProfil.setImage(getDefaultProfileImage());
            }
        } else {
            System.err.println("Aucune photo spécifiée pour le chercheur");
            imageProfil.setImage(getDefaultProfileImage());
        }
    }

    private Image getDefaultProfileImage() {
        java.net.URL url = getClass().getResource("/images/profile_icon.jpeg");
        if (url != null) {
            return new Image(url.toExternalForm());
        } else {
            System.err.println("Image par défaut non trouvée dans /images/profile_icon.jpeg !");
            return null;
        }
    }

    @FXML
    private void handleModifier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProfileChercheur.fxml"));
            Parent root = loader.load();

            ModifierProfileChercheurController controller = loader.getController();
            controller.setChercheur(chercheur);

            Stage stage = new Stage();
            stage.setTitle("Modifier le profil chercheur");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) btnModifier.getScene().getWindow();
            currentStage.close();
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
        PreparedStatement stmtChercheur = null;
        PreparedStatement stmtUser = null;

        try {
            connection = MyDatabase.getInstance().getCnx();
            connection.setAutoCommit(false);

            // Supprimer d'abord de la table chercheur
            String queryChercheur = "DELETE FROM chercheur WHERE id_user = ?";
            stmtChercheur = connection.prepareStatement(queryChercheur);
            stmtChercheur.setInt(1, chercheur.getId());
            stmtChercheur.executeUpdate();

            // Ensuite supprimer de la table user
            String queryUser = "DELETE FROM user WHERE id = ?";
            stmtUser = connection.prepareStatement(queryUser);
            stmtUser.setInt(1, chercheur.getId());
            int rowsAffected = stmtUser.executeUpdate();

            if (rowsAffected > 0) {
                connection.commit();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Profil supprimé avec succès.");
                Session.clear();
                redirectToLogin();
            } else {
                connection.rollback();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le profil.");
            }
        } catch (SQLException e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la suppression du profil.");
        } finally {
            try {
                if (stmtChercheur != null) stmtChercheur.close();
                if (stmtUser != null) stmtUser.close();
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
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
                java.awt.Desktop.getDesktop().open(new File(chercheur.getCv()));
            } catch (Exception e) {
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
