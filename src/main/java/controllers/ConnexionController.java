package controllers;

import entities.Freelancer;
import entities.Chercheur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import org.mindrot.jbcrypt.BCrypt;
import services.FreelancerService;
import utils.Session;
import utils.CaptchaGenerator;
import utils.MyDatabase;

import java.io.IOException;
import java.sql.*;

public class ConnexionController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ImageView captchaImage;

    @FXML
    private TextField captchaField;

    @FXML
    public void initialize() {
        refreshCaptcha();
    }

    @FXML
    void refreshCaptcha() {
        captchaImage.setImage(CaptchaGenerator.generateCaptcha().getImage());
    }

    // Méthode pour vérifier les identifiants
    private boolean checkUserCredentials(String email, String password) {
        boolean isValid = false;
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            System.out.println("Tentative de connexion à la base de données...");
            connection = MyDatabase.getInstance().getCnx();
            if (connection == null) {
                System.err.println("❌ Erreur: Impossible d'obtenir une connexion à la base de données");
                return false;
            }
            System.out.println("✅ Connexion à la base de données établie");

            // Vérifier si la base de données existe
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet catalogs = metaData.getCatalogs();
            boolean databaseExists = false;
            while (catalogs.next()) {
                String databaseName = catalogs.getString(1);
                if ("careera".equalsIgnoreCase(databaseName)) {
                    databaseExists = true;
                    break;
                }
            }
            catalogs.close();

            if (!databaseExists) {
                System.err.println("❌ La base de données 'careera' n'existe pas");
                return false;
            }
            System.out.println("✅ Base de données 'careera' trouvée");

            // Vérifier si la table user existe
            ResultSet tables = metaData.getTables(null, null, "user", new String[] {"TABLE"});
            if (!tables.next()) {
                System.err.println("❌ La table 'user' n'existe pas");
                return false;
            }
            System.out.println("✅ Table 'user' trouvée");

            // Afficher la structure de la table user
            ResultSet columns = metaData.getColumns(null, null, "user", null);
            System.out.println("Structure de la table user :");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME");
                System.out.println("- " + columnName + " (" + columnType + ")");
            }
            columns.close();

            // Requête pour récupérer le mot de passe hashé
            String query = "SELECT * FROM user WHERE email = ?";
            System.out.println("Exécution de la requête : " + query + " avec email = " + email);
            stmt = connection.prepareStatement(query);
            stmt.setString(1, email);

            rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                System.out.println("Utilisateur trouvé avec le mot de passe hashé : " + hashedPassword);
                
                // Vérification du mot de passe
                if (BCrypt.checkpw(password, hashedPassword)) {
                    isValid = true;
                    System.out.println("✅ Mot de passe correct !");
                } else {
                    System.out.println("❌ Mot de passe incorrect !");
                }
            } else {
                System.out.println("❌ Aucun utilisateur trouvé avec l'email : " + email);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la vérification des identifiants: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isValid;
    }

    // Lors du clic sur "Se connecter"
    @FXML
    void handleConnexion(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String captchaInput = captchaField.getText().trim();

        System.out.println("Tentative de connexion avec email : " + email);

        if (email.isEmpty() || password.isEmpty() || captchaInput.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs vides");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }

        // Vérifier le CAPTCHA
        if (!CaptchaGenerator.validateCaptcha(captchaInput)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CAPTCHA incorrect");
            alert.setHeaderText(null);
            alert.setContentText("Le texte du CAPTCHA est incorrect. Veuillez réessayer.");
            alert.showAndWait();
            refreshCaptcha();
            return;
        }

        boolean isAuthenticated = checkUserCredentials(email, password);

        if (isAuthenticated) {
            // Récupérer le type d'utilisateur
            String userType = null;
            Connection connection = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                connection = MyDatabase.getInstance().getCnx();
                if (connection == null) {
                    throw new SQLException("Impossible d'obtenir une connexion à la base de données");
                }

                String query = "SELECT type FROM user WHERE email = ?";
                stmt = connection.prepareStatement(query);
                stmt.setString(1, email);
                rs = stmt.executeQuery();
                
                if (rs.next()) {
                    userType = rs.getString("type");
                    System.out.println("Type d'utilisateur trouvé : " + userType);
                }

                // Récupérer les informations complètes de l'utilisateur
                if ("chercheur".equalsIgnoreCase(userType)) {
                    String userQuery = "SELECT u.*, c.photo, c.cv FROM user u JOIN chercheur c ON u.id = c.user_id WHERE u.email = ?";
                    PreparedStatement userStmt = connection.prepareStatement(userQuery);
                    userStmt.setString(1, email);
                    ResultSet userRs = userStmt.executeQuery();
                    
                    if (userRs.next()) {
                        Chercheur chercheur = new Chercheur();
                        chercheur.setId(userRs.getInt("id"));
                        chercheur.setNom(userRs.getString("nom"));
                        chercheur.setPrenom(userRs.getString("prenom"));
                        chercheur.setEmail(userRs.getString("email"));
                        chercheur.setTel(userRs.getString("tel"));
                        chercheur.setDomaine(userRs.getString("domaine"));
                        chercheur.setPhoto(userRs.getString("photo"));
                        chercheur.setCv(userRs.getString("cv"));
                        chercheur.setType("chercheur");
                        chercheur.setRoles(userRs.getString("roles"));
                        System.out.println("Informations du chercheur récupérées avec succès");
                        
                        // Stocker le chercheur dans la session
                        Session.setCurrentUser(chercheur);
                        System.out.println("Chercheur connecté : " + chercheur.getEmail());
                    }
                    userRs.close();
                    userStmt.close();
                } else if ("freelancer".equalsIgnoreCase(userType)) {
                    String userQuery = "SELECT u.*, f.photo, f.cv FROM user u JOIN freelancer f ON u.id = f.user_id WHERE u.email = ?";
                    PreparedStatement userStmt = connection.prepareStatement(userQuery);
                    userStmt.setString(1, email);
                    ResultSet userRs = userStmt.executeQuery();
                    
                    if (userRs.next()) {
                        Freelancer freelancer = new Freelancer();
                        freelancer.setId(userRs.getInt("id"));
                        freelancer.setNom(userRs.getString("nom"));
                        freelancer.setPrenom(userRs.getString("prenom"));
                        freelancer.setEmail(userRs.getString("email"));
                        freelancer.setTel(userRs.getString("tel"));
                        freelancer.setDomaine(userRs.getString("domaine"));
                        freelancer.setPhoto(userRs.getString("photo"));
                        freelancer.setCv(userRs.getString("cv"));
                        freelancer.setType("freelancer");
                        freelancer.setRoles(userRs.getString("roles"));
                        System.out.println("Informations du freelancer récupérées avec succès");
                        
                        // Stocker le freelancer dans la session
                        Session.setCurrentUser(freelancer);
                        System.out.println("Freelancer connecté : " + freelancer.getEmail());
                    }
                    userRs.close();
                    userStmt.close();
                }

                // Rediriger selon le type d'utilisateur
                String fxmlPath;
                if ("freelancer".equalsIgnoreCase(userType) || "chercheur".equalsIgnoreCase(userType)) {
                    fxmlPath = "/Acceuilfc.fxml";
                } else if ("moderateur".equalsIgnoreCase(userType)) {
                    fxmlPath = "/Acceuil.fxml";
                } else {
                    fxmlPath = "/Acceuil.fxml"; // Page par défaut
                }

                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource(fxmlPath));
                    if (loader.getLocation() == null) {
                        throw new IOException("Impossible de trouver le fichier FXML: " + fxmlPath);
                    }
                    Parent root = loader.load();
                    Stage stage = (Stage) emailField.getScene().getWindow();
                    stage.setScene(new Scene(root));
                } catch (IOException e) {
                    System.err.println("❌ Erreur de chargement FXML: " + e.getMessage());
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur de chargement");
                    alert.setHeaderText(null);
                    alert.setContentText("Une erreur est survenue lors du chargement de la page. Veuillez réessayer.");
                    alert.showAndWait();
                }

            } catch (SQLException e) {
                System.err.println("❌ Erreur SQL: " + e.getMessage());
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de connexion");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur est survenue lors de la connexion à la base de données. Veuillez réessayer.");
                alert.showAndWait();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de connexion");
            alert.setHeaderText(null);
            alert.setContentText("Identifiants incorrects. Veuillez réessayer.");
            alert.showAndWait();
            refreshCaptcha();
        }
    }

    public void handleForgotPasswordPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ForgotPassword.fxml"));
            if (loader.getLocation() == null) {
                throw new IOException("Impossible de trouver le fichier FXML: /ForgotPassword.fxml");
            }
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Récupération du mot de passe");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Erreur de chargement FXML: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors du chargement de la page. Veuillez réessayer.");
            alert.showAndWait();
        }
    }

    // Lors du clic sur "Créer un compte"
    @FXML
    void handleCreationCompte(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/PageCreationCompte.fxml"));
            if (loader.getLocation() == null) {
                throw new IOException("Impossible de trouver le fichier FXML: /PageCreationCompte.fxml");
            }
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.err.println("❌ Erreur de chargement FXML: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors du chargement de la page. Veuillez réessayer.");
            alert.showAndWait();
        }
    }

    @FXML
    void goToForgotPassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForgotPassword.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Mot de passe oublié");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
