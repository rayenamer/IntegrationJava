package controllers;

import entities.Freelancer;
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
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.DriverManager.println;

public class ProfileCRUDController {

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
    private Label lblAdresse;
    @FXML
    private Label lblAnneesExperience;
    @FXML
    private Label lblCV;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnSupprimer;
    @FXML
    private Button btnClose;

    private Freelancer freelancer;

    public void setFreelancer(Freelancer freelancer) {
        // Charger toutes les données du freelancer depuis la base
        this.freelancer = fetchFreelancerFromDB(freelancer.getId());
        updateLabels();
        loadProfileImage();
    }

    private Freelancer fetchFreelancerFromDB(int id) {
        Freelancer f = new Freelancer();
        try {
            Connection cnx = MyDatabase.getInstance().getCnx();
            PreparedStatement ps = cnx.prepareStatement(
                "SELECT u.nom, u.prenom, u.email, u.tel, u.domaine, u.sexe, f.photo, f.cv, f.adresse, f.annees_experience " +
                "FROM user u JOIN freelancer f ON u.id = f.id WHERE u.id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                f.setId(id);
                f.setNom(rs.getString("nom"));
                f.setPrenom(rs.getString("prenom"));
                f.setEmail(rs.getString("email"));
                f.setTel(rs.getString("tel"));
                f.setDomaine(rs.getString("domaine"));
                f.setSexe(rs.getString("sexe"));
                f.setPhoto(rs.getString("photo"));
                f.setCv(rs.getString("cv"));
                f.setAdresse(rs.getString("adresse"));
                f.setAnnees_Experience(rs.getInt("annees_experience"));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du freelancer : " + e.getMessage());
        }
        return f;
    }

    private void updateLabels() {
        if (freelancer != null) {
            lblNom.setText(freelancer.getNom() != null ? freelancer.getNom() : "");
            lblPrenom.setText(freelancer.getPrenom() != null ? freelancer.getPrenom() : "");
            lblEmail.setText(freelancer.getEmail() != null ? freelancer.getEmail() : "");
            lblTel.setText(freelancer.getTel() != null ? freelancer.getTel() : "");
            lblAdresse.setText(freelancer.getAdresse() != null ? freelancer.getAdresse() : "");
            lblDomaine.setText(freelancer.getDomaine() != null ? freelancer.getDomaine() : "");
            lblAnneesExperience.setText(freelancer.getAnnees_Experience() > 0 ? String.valueOf(freelancer.getAnnees_Experience()) : "0");
            lblCV.setText(freelancer.getCv() != null ? new File(freelancer.getCv()).getName() : "Aucun CV");
        }
    }

    private void loadProfileImage() {
        if (freelancer != null && freelancer.getPhoto() != null && !freelancer.getPhoto().isEmpty()) {
            try {
                String photoPath = freelancer.getPhoto();
                Image image = null;
                // Gestion des chemins absolus (Windows, Unix) et relatifs
                if (photoPath.matches("^[a-zA-Z]:\\\\.*") || photoPath.startsWith("/")) {
                    // Chemin absolu (Windows ou Unix)
                    image = new Image("file:" + photoPath.replace("\\", "/"));
                } else {
                    // Chemin relatif ou ressource
                    java.net.URL url = getClass().getResource("/image/" + photoPath);
                    if (url != null) {
                        image = new Image(url.toExternalForm());
                    }
                }
                if (image != null && !image.isError()) {
                    imageProfil.setImage(image);
                } else {
                    imageProfil.setImage(getDefaultProfileImage());
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de la photo de profil : " + e.getMessage());
                imageProfil.setImage(getDefaultProfileImage());
            }
        } else {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProfile.fxml"));
            Parent root = loader.load();

            ModifierProfileController controller = loader.getController();
            controller.setFreelancer(freelancer);

            Stage stage = new Stage();
            stage.setTitle("Modifier le profil");
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
                deleteFreelancer();
            }
        });
    }

    private void deleteFreelancer() {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = MyDatabase.getInstance().getCnx();
            String query = "DELETE FROM user WHERE id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, freelancer.getId());

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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}