package controllers;

import entities.Freelancer;
import entities.Chercheur;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import org.mindrot.jbcrypt.BCrypt;
import utils.PasswordHasher;
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
                if (PasswordHasher.checkpw(password, hashedPassword)) {
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

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String captchaInput = captchaField.getText().trim();

        if (email.isEmpty() || password.isEmpty() || captchaInput.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        if (!CaptchaGenerator.validateCaptcha(captchaInput)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Code captcha incorrect.");
            refreshCaptcha();
            return;
        }

        try {
            Connection connection = MyDatabase.getInstance().getCnx();
            String query = "SELECT * FROM user WHERE email = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (PasswordHasher.checkpw(password, hashedPassword)) {
                    String userType = rs.getString("type");
                    System.out.println("Connexion réussie pour l'utilisateur de type : " + userType);

                    // Créer l'objet utilisateur approprié
                    User user = null;
                    if ("chercheur".equalsIgnoreCase(userType)) {
                        Chercheur chercheur = new Chercheur();
                        chercheur.setId(rs.getInt("id"));
                        chercheur.setNom(rs.getString("nom"));
                        chercheur.setPrenom(rs.getString("prenom"));
                        chercheur.setEmail(rs.getString("email"));
                        chercheur.setTel(rs.getString("tel"));
                        chercheur.setDomaine(rs.getString("domaine"));
                        chercheur.setType("chercheur");
                        chercheur.setRoles(rs.getString("roles"));
                        user = chercheur;
                    } else if ("freelancer".equalsIgnoreCase(userType)) {
                        Freelancer freelancer = new Freelancer();
                        freelancer.setId(rs.getInt("id"));
                        freelancer.setNom(rs.getString("nom"));
                        freelancer.setPrenom(rs.getString("prenom"));
                        freelancer.setEmail(rs.getString("email"));
                        freelancer.setTel(rs.getString("tel"));
                        freelancer.setDomaine(rs.getString("domaine"));
                        freelancer.setType("freelancer");
                        freelancer.setRoles(rs.getString("roles"));
                        user = freelancer;
                    } else if ("moderateur".equalsIgnoreCase(userType)) {
                        User moderateur = new User();
                        moderateur.setId(rs.getInt("id"));
                        moderateur.setNom(rs.getString("nom"));
                        moderateur.setPrenom(rs.getString("prenom"));
                        moderateur.setEmail(rs.getString("email"));
                        moderateur.setTel(rs.getString("tel"));
                        moderateur.setDomaine(rs.getString("domaine"));
                        moderateur.setType("moderateur");
                        moderateur.setRoles(rs.getString("roles"));
                        user = moderateur;
                    }

                    if (user != null) {
                        // Stocker l'utilisateur dans la session
                        Session.setCurrentUser(user);
                        System.out.println("Utilisateur stocké dans la session : " + user.getEmail() + " (Type: " + user.getType() + ")");

                        // Rediriger selon le type d'utilisateur
                        String fxmlPath;
                        if ("moderateur".equalsIgnoreCase(userType)) {
                            fxmlPath = "/Acceuil.fxml";
                        } else {
                            fxmlPath = "/Acceuilfc.fxml";
                        }

                        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                        Parent root = loader.load();
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Accueil");
                        stage.show();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Type d'utilisateur non reconnu.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Mot de passe incorrect.");
                    refreshCaptcha();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun compte trouvé avec cet email.");
                refreshCaptcha();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la connexion : " + e.getMessage());
            refreshCaptcha();
        }
    }

    public void handleForgotPasswordPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/javas/FXML/ForgotPassword.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleCreationCompte(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PageCreationCompte.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToForgotPassword(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ForgotPassword.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}