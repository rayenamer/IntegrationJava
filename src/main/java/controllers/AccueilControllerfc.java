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
import java.util.Stack;

public class AccueilControllerfc {

    @FXML
    private AnchorPane contentPane;
    @FXML
    private Button profileButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button offreButton;
    @FXML
    private Button backButton;

    private static Stack<String> navigationHistory = new Stack<>();

    private String userEmail;
    private String userPassword;

    @FXML
    public void initialize() {
        // Initialiser les informations de session
        User currentUser = Session.getCurrentUser();
        if (currentUser != null) {
            System.out.println("Session initialisée pour : " + currentUser.getEmail() + " (Type: " + currentUser.getType() + ")");
        } else {
            System.out.println("Aucun utilisateur dans la session lors de l'initialisation");
        }
    }

    public void setUserCredentials(String email, String password) {
        this.userEmail = email;
        this.userPassword = password;
    }

    @FXML
    private void handleOffreClick(ActionEvent event) {
        User currentUser = Session.getCurrentUser();
        System.out.println("Type d'utilisateur actuel : " + (currentUser != null ? currentUser.getType() : "null"));

        if (currentUser != null && ("freelancer".equalsIgnoreCase(currentUser.getType()) || "chercheur".equalsIgnoreCase(currentUser.getType()))) {
            loadUI("/AfficherOffresUser.fxml");
        } else {
            System.out.println("Accès refusé - Utilisateur non autorisé");
            showAlert(Alert.AlertType.WARNING, "Accès restreint", "Cette fonctionnalité est réservée aux freelancers et chercheurs.");
        }
    }

    @FXML
    private void GoForum(ActionEvent event) {
        User currentUser = Session.getCurrentUser();
        System.out.println("Type d'utilisateur actuel : " + (currentUser != null ? currentUser.getType() : "null"));
        loadUI("/Discussions.fxml");
    }

    @FXML
    private void GoEvent(ActionEvent event) {
        try {
            User currentUser = Session.getCurrentUser();
            System.out.println("Type d'utilisateur actuel : " + (currentUser != null ? currentUser.getType() : "null"));

            EventListController EventListController = new EventListController();
            Scene currentScene = offreButton.getScene();

            if (currentScene != null) {
                Stage stage = (Stage) currentScene.getWindow();
                EventListController.show(stage);
            } else {
                Stage newStage = new Stage();
                EventListController.show(newStage);
                newStage.setTitle("Gestion des Événements");
                newStage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            navigationHistory.push(fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene currentScene = offreButton.getScene();
            if (currentScene != null) {
                Stage stage = (Stage) currentScene.getWindow();
                stage.setScene(new Scene(root));
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible de charger la page : " + fxmlPath);
        }
    }

    @FXML
    private void handleBack() {
        if (!navigationHistory.isEmpty()) {
            navigationHistory.pop(); // Remove current page
            if (!navigationHistory.isEmpty()) {
                String previousPage = navigationHistory.pop();
                loadUI(previousPage);
            } else {
                // If no more history, go to default page
                loadUI("/Acceuilfc.fxml");
            }
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

    @FXML
    private void handleStatistque(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/statistique.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Statistiques");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir les statistiques.");
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
        User currentUser = Session.getCurrentUser();
        System.out.println("Type d'utilisateur actuel : " + (currentUser != null ? currentUser.getType() : "null"));

        if (currentUser != null && ("freelancer".equalsIgnoreCase(currentUser.getType()) || "chercheur".equalsIgnoreCase(currentUser.getType()))) {
            loadUI("/listmissionfreelencer.fxml");
        } else {
            System.out.println("Accès refusé - Utilisateur non autorisé");
            showAlert(Alert.AlertType.WARNING, "Accès restreint", "Cette fonctionnalité est réservée aux freelancers et chercheurs.");
        }
    }    }

