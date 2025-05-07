package controllers;

import entities.Freelancer;
import entities.Chercheur;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;
import javafx.stage.Stage;
import utils.Session;

import java.io.IOException;

public class AccueilControllerfc {

    @FXML
    private AnchorPane contentPane;
    @FXML
    private Button profileButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button offreButton;

    private String userEmail;
    private String userPassword;

    @FXML
    public void initialize() {
        this.userEmail = "utilisateur@example.com";
        this.userPassword = "";
    }

    public void setUserCredentials(String email, String password) {
        this.userEmail = email;
        this.userPassword = password;
    }

    @FXML
    private void handleOffreClick(ActionEvent event) {
        loadUI("/AjouterOffre.fxml");
    }

    @FXML
    private void handleDeconnexion(ActionEvent event) {
        try {
            Session.clear();
            this.userEmail = "";
            this.userPassword = "";

            Parent root = FXMLLoader.load(getClass().getResource("/index.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de se déconnecter.");
        }
    }

    private void loadUI(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la vue : " + fxmlPath);
        }
    }

    @FXML
    private void handleProfile() {
        User currentUser = Session.getCurrentUser();
        System.out.println("Tentative d'affichage du profil...");
        
        if (currentUser == null) {
            System.err.println("Aucun utilisateur trouvé dans la session");
            showAlert(Alert.AlertType.WARNING, "Non connecté", "Aucun utilisateur connecté. Veuillez vous reconnecter.");
            return;
        }

        try {
            String fxmlPath;
            if (currentUser instanceof Freelancer) {
                fxmlPath = "/ProfileCRUD.fxml";
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                
                ProfileCRUDController controller = loader.getController();
                controller.setFreelancer((Freelancer) currentUser);
                
                Stage stage = new Stage();
                stage.setTitle("Mon Profil Freelancer");
                stage.setScene(new Scene(root));
                stage.show();
            } else if (currentUser instanceof Chercheur) {
                fxmlPath = "/ProfileChercheur.fxml";
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                
                ProfileChercheurController controller = loader.getController();
                controller.setChercheur((Chercheur) currentUser);
                
                Stage stage = new Stage();
                stage.setTitle("Mon Profil Chercheur");
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                System.err.println("Type d'utilisateur non supporté : " + currentUser.getType());
                showAlert(Alert.AlertType.ERROR, "Erreur", "Type d'utilisateur non supporté.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page du profil.");
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
