package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import entities.Chercheur;
import utils.MyDatabase;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import services.ChercheurService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ModifierProfileChercheurController {

    @FXML private TextField txtNom, txtPrenom, txtEmail, txtTel, txtDomaine, txtCV, txtPhoto;
    @FXML private ComboBox<String> specialiteCombo;
    @FXML private Button btnAnnuler, btnEnregistrer;
    @FXML private ImageView imageView;
    private Chercheur chercheur;
    private String selectedCVPath = null;
    private String selectedPhotoPath = null;

    private final Map<String, ObservableList<String>> domainesSpecialites = new HashMap<>();

    @FXML
    private void initialize() {
        // Initialiser les spécialités pour chaque domaine
        domainesSpecialites.put("Intelligence Artificielle", FXCollections.observableArrayList(
            "Machine Learning",
            "Deep Learning",
            "Computer Vision",
            "Natural Language Processing",
            "Robotics"
        ));

        domainesSpecialites.put("Sciences des données", FXCollections.observableArrayList(
            "Data Mining",
            "Big Data",
            "Data Visualization",
            "Statistical Analysis",
            "Business Intelligence"
        ));

        domainesSpecialites.put("Cybersécurité", FXCollections.observableArrayList(
            "Sécurité des réseaux",
            "Cryptographie",
            "Forensic",
            "Sécurité des applications",
            "Gestion des risques"
        ));

        domainesSpecialites.put("Réseaux et télécommunications", FXCollections.observableArrayList(
            "Réseaux IP",
            "Télécommunications mobiles",
            "Cloud Computing",
            "IoT",
            "5G/6G"
        ));

        domainesSpecialites.put("Systèmes embarqués", FXCollections.observableArrayList(
            "Microcontrôleurs",
            "IoT",
            "Temps réel",
            "Systèmes critiques",
            "Électronique numérique"
        ));

        // Ajouter un listener sur le domaine pour mettre à jour les spécialités
        txtDomaine.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && domainesSpecialites.containsKey(newVal)) {
                specialiteCombo.setItems(domainesSpecialites.get(newVal));
            }
        });
    }

    public void setChercheur(Chercheur chercheur) {
        this.chercheur = chercheur;
        populateFields();
    }

    private void populateFields() {
        if (chercheur != null) {
            try {
                // Récupérer les données à jour depuis la base de données
                Connection cnx = MyDatabase.getInstance().getCnx();
                PreparedStatement ps = cnx.prepareStatement(
                    "SELECT u.nom, u.prenom, u.email, u.tel, u.domaine, " +
                    "c.specialite, c.cv, c.photo " +
                    "FROM user u " +
                    "INNER JOIN chercheur c ON u.id = c.id_user " +
                    "WHERE u.id = ?");
                ps.setInt(1, chercheur.getId());
                java.sql.ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    // Mettre à jour les champs avec les données récupérées
                    txtNom.setText(rs.getString("nom"));
                    txtPrenom.setText(rs.getString("prenom"));
                    txtEmail.setText(rs.getString("email"));
                    txtTel.setText(rs.getString("tel"));
                    txtDomaine.setText(rs.getString("domaine"));
                    
                    // Mettre à jour les spécialités disponibles en fonction du domaine
                    String domaine = rs.getString("domaine");
                    if (domaine != null && domainesSpecialites.containsKey(domaine)) {
                        specialiteCombo.setItems(domainesSpecialites.get(domaine));
                        specialiteCombo.setValue(rs.getString("specialite"));
                    }
                    
                    // Mettre à jour les chemins des fichiers
                    String cvPath = rs.getString("cv");
                    String photoPath = rs.getString("photo");
                    
                    if (cvPath != null && !cvPath.isEmpty()) {
                        txtCV.setText(new File(cvPath).getName());
                    }
                    
                    if (photoPath != null && !photoPath.isEmpty()) {
                        txtPhoto.setText(new File(photoPath).getName());
                        // Mettre à jour l'objet chercheur avec les chemins complets
                        chercheur.setCv(cvPath);
                        chercheur.setPhoto(photoPath);
                    }

                    // Charger l'image
                    loadImage();
                    
                    // Debug: Afficher les valeurs chargées
                    System.out.println("Données chargées dans le formulaire de modification:");
                    System.out.println("Nom: " + txtNom.getText());
                    System.out.println("Prénom: " + txtPrenom.getText());
                    System.out.println("Email: " + txtEmail.getText());
                    System.out.println("Téléphone: " + txtTel.getText());
                    System.out.println("Domaine: " + txtDomaine.getText());
                    System.out.println("Spécialité: " + specialiteCombo.getValue());
                    System.out.println("CV: " + txtCV.getText());
                    System.out.println("Photo: " + txtPhoto.getText());
                } else {
                    System.err.println("Aucun chercheur trouvé avec l'ID: " + chercheur.getId());
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les données du profil.");
                }
                
                rs.close();
                ps.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la récupération des données du chercheur : " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les données du profil.");
            }
        }
    }

    private void loadImage() {
        String photoPath = chercheur.getPhoto();
        InputStream inputStream = null;

        try {
            if (photoPath != null && !photoPath.isEmpty()) {
                File photoFile = new File(photoPath);
                if (photoFile.exists()) {
                    inputStream = new FileInputStream(photoFile);
                }
            }

            if (inputStream == null) {
                System.err.println("Image non trouvée, chargement d'une image par défaut");
                // Essayer de charger l'image par défaut depuis les ressources
                inputStream = getClass().getResourceAsStream("/images/profile_icon.jpeg");
                if (inputStream == null) {
                    System.err.println("Image par défaut non trouvée dans /images/profile_icon.jpeg");
                    // Créer une image vide
                    imageView.setImage(null);
                    return;
                }
            }

            Image image = new Image(inputStream);
            imageView.setImage(image);

        } catch (IOException e) {
            e.printStackTrace();
            imageView.setImage(null);
        } finally {
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
    private void handleEnregistrer() {
        if (validateFields()) {
            updateChercheur();
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (txtNom.getText() == null || txtNom.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le champ nom est obligatoire.");
            txtNom.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (txtPrenom.getText() == null || txtPrenom.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le champ prénom est obligatoire.");
            txtPrenom.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le champ email est obligatoire.");
            txtEmail.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (txtTel.getText() == null || txtTel.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le champ téléphone est obligatoire.");
            txtTel.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (txtDomaine.getText() == null || txtDomaine.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le champ domaine est obligatoire.");
            txtDomaine.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (specialiteCombo.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le champ spécialité est obligatoire.");
            specialiteCombo.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        return isValid;
    }

    private void updateChercheur() {
        Connection connection = null;
        PreparedStatement stmtUser = null;
        PreparedStatement stmtChercheur = null;

        try {
            connection = MyDatabase.getInstance().getCnx();
            connection.setAutoCommit(false);

            // Requête pour la table user
            String queryUser = "UPDATE user SET nom = ?, prenom = ?, email = ?, tel = ?, domaine = ? WHERE id = ?";
            stmtUser = connection.prepareStatement(queryUser);
            stmtUser.setString(1, txtNom.getText());
            stmtUser.setString(2, txtPrenom.getText());
            stmtUser.setString(3, txtEmail.getText());
            stmtUser.setString(4, txtTel.getText());
            stmtUser.setString(5, txtDomaine.getText());
            stmtUser.setInt(6, chercheur.getId());

            // Requête pour la table chercheur
            String queryChercheur = "UPDATE chercheur SET specialite = ?, cv = ?, photo = ? WHERE id = ?";
            stmtChercheur = connection.prepareStatement(queryChercheur);
            
            // Gérer les chemins si non modifiés
            String cvPath = selectedCVPath != null ? selectedCVPath : (chercheur.getCv() != null ? chercheur.getCv() : "");
            String photoPath = selectedPhotoPath != null ? selectedPhotoPath : (chercheur.getPhoto() != null ? chercheur.getPhoto() : "");

            stmtChercheur.setString(1, specialiteCombo.getValue());
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
                chercheur.setSpecialite(specialiteCombo.getValue());
                chercheur.setCv(cvPath);
                chercheur.setPhoto(photoPath);

                closeWindow();
            } else {
                connection.rollback();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour du profil. Veuillez vérifier que tous les champs sont correctement remplis.");
            }

        } catch (SQLException e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            String errorMessage = "Une erreur est survenue lors de la modification : ";
            if (e.getMessage().contains("specialite")) {
                errorMessage += "Erreur lors de la mise à jour de la spécialité.";
            } else {
                errorMessage += e.getMessage();
            }
            showAlert(Alert.AlertType.ERROR, "Erreur", errorMessage);
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
            
            // Afficher l'aperçu de la nouvelle image
            try {
                Image image = new Image(new FileInputStream(file));
                imageView.setImage(image);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'aperçu de l'image.");
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

