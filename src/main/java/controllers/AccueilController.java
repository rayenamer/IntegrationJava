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
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;
import javafx.stage.Stage;
import utils.Session;

import java.io.IOException;
import java.net.URL;

public class AccueilController {

    @FXML
    private AnchorPane contentPane;
    @FXML
    private Button profileButton;
    @FXML
    private Button logoutButton;
    @FXML
    private MenuButton adminPanelButton;
    @FXML
    private Button offreButton;

    private String userEmail;
    private String userPassword;

    @FXML
    public void initialize() {
        this.userEmail = "utilisateur@example.com";
        this.userPassword = "";

        // Vérifier si l'utilisateur est un modérateur
        if (Session.getCurrentUser() != null && "moderateur".equalsIgnoreCase(Session.getCurrentUser().getType())) {
            adminPanelButton.setVisible(true);
        } else {
            adminPanelButton.setVisible(false);
        }
    }

    public void setUserCredentials(String email, String password) {
        this.userEmail = email;
        this.userPassword = password;
    }
    @FXML
    private void handleOffreClick(ActionEvent event) {
        try {
            // Vérifier le type d'utilisateur
            User currentUser = Session.getCurrentUser();
            System.out.println("Type d'utilisateur actuel : " + (currentUser != null ? currentUser.getType() : "null"));

            if (Session.getCurrentUser() != null && "moderateur".equalsIgnoreCase(Session.getCurrentUser().getType())) {
                // Charger la page AjouterOffre.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterOffre.fxml"));
                Parent root = loader.load();

                // Récupérer la scène actuelle
                Scene currentScene = offreButton.getScene();
                if (currentScene != null) {
                    // Remplacer le contenu de la scène
                    currentScene.setRoot(root);
                } else {
                    // Si pas de scène existante, créer une nouvelle fenêtre
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("ajouter une Offre");
                    stage.show();
                }
            } else {
                System.out.println("Accès refusé - Utilisateur non autorisé");
                showAlert(Alert.AlertType.WARNING, "Accès restreint", "Cette fonctionnalité est réservée aux freelancers et chercheurs.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page des offres : " + e.getMessage());
        }
    }

    @FXML
    private void GoForum(ActionEvent event) {
        try {
            // Vérifier le type d'utilisateur
            User currentUser = Session.getCurrentUser();
            System.out.println("Type d'utilisateur actuel : " + (currentUser != null ? currentUser.getType() : "null"));


                // Charger la page AjouterOffre.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Discussions.fxml"));
                Parent root = loader.load();

                // Récupérer la scène actuelle
                Scene currentScene = offreButton.getScene();
                if (currentScene != null) {
                    // Remplacer le contenu de la scène
                    currentScene.setRoot(root);
                } else {
                    // Si pas de scène existante, créer une nouvelle fenêtre
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("ajouter une Offre");
                    stage.show();
                }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page des offres : " + e.getMessage());
        }
    }


    @FXML
    private void handleDeconnexion(ActionEvent event) {
        try {
            Session.setCurrentUser(null);
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
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                throw new IOException("FXML file not found: " + fxmlPath);
            }
            Node node = FXMLLoader.load(url);
            contentPane.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger la vue : " + fxmlPath + "\n" +
                            "Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void handleProfile() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            showAlert(Alert.AlertType.WARNING, "Non connecté", "Aucun utilisateur connecté.");
            return;
        }

        try {
            String fxmlPath;
            if (currentUser instanceof Chercheur) {
                fxmlPath = "/ProfileChercheur.fxml";
            } else if (currentUser instanceof Freelancer) {
                fxmlPath = "/ProfileCRUD.fxml";
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Type d'utilisateur non supporté.");
                return;
            }

            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                throw new IOException("FXML file not found: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            if (currentUser instanceof Chercheur) {
                ProfileChercheurController controller = loader.getController();
                controller.setChercheur((Chercheur) currentUser);
            } else if (currentUser instanceof Freelancer) {
                ProfileCRUDController controller = loader.getController();
                controller.setFreelancer((Freelancer) currentUser);
            }

            Stage stage = new Stage();
            stage.setTitle("Mon Profil");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir la page du profil.\n" +
                            "Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void handleStatistque(ActionEvent event) {
        try {
            URL url = getClass().getResource("/statistique.fxml");
            if (url == null) {
                throw new IOException("FXML file not found: /statistique.fxml");
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Stage stage = (Stage) adminPanelButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Statistiques");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir les statistiques.\n" +
                            "Erreur: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleFreelanceClick(ActionEvent event) {
        try {
            // Vérifier le type d'utilisateur
            User currentUser = Session.getCurrentUser();
            System.out.println("Type d'utilisateur actuel : " + (currentUser != null ? currentUser.getType() : "null"));

            if (currentUser != null && "moderateur".equalsIgnoreCase(currentUser.getType())) {
                // Charger la page AjouterMission.fxml
                URL url = getClass().getResource("/Missionfreelencer.fxml");
                if (url == null) {
                    throw new IOException("FXML file not found: /Missionfreelencer.fxml");
                }
                FXMLLoader loader = new FXMLLoader(url);
                Parent root = loader.load();

                // Récupérer la scène actuelle
                Scene currentScene = ((Node) event.getSource()).getScene();
                if (currentScene != null) {
                    // Remplacer le contenu de la scène
                    currentScene.setRoot(root);
                } else {
                    // Si pas de scène existante, créer une nouvelle fenêtre
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Ajouter une Mission");
                    stage.show();
                }
            } else {
                System.out.println("Accès refusé - Utilisateur non autorisé");
                showAlert(Alert.AlertType.WARNING, "Accès restreint", "Cette fonctionnalité est réservée aux modérateurs.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page d'ajout de mission : " + e.getMessage());
        }
    }
}