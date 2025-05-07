package controllers;

import entities.Offre;
import entities.TypeContrat;
import entities.TypeOffre;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.OffreService;
import services.TypeContratService;
import services.TypeOffreService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class AjouterOffreController implements Initializable {

    @FXML
    public ComboBox<String> typeContratCB;
    @FXML
    public ComboBox<String> typeOffreCB;
    public TextField posteTF;
    public TextField entrepriseTF;
    public TextField localisationTF;
    public TextField salaireTF;
    public CheckBox disponibleCB;
    public TextField imageTF;
    public TextField utilisateurTF;
    public Button gererContratBtn;
    public Button gererOffreBtn;
    @FXML
    private ImageView imagePreview;
    @FXML
    private Label imageNameLabel;

    // Mappage nom -> objet
    private Map<String, TypeContrat> mapTypeContrat = new HashMap<>();
    private Map<String, TypeOffre> mapTypeOffre = new HashMap<>();

    private File selectedImageFile;
    private final OffreService offreService = new OffreService();
    private final TypeContratService typeContratService = new TypeContratService();
    private final TypeOffreService typeOffreService = new TypeOffreService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Charger les types de contrat
        loadTypeContrats();

        // Charger les types d'offre
        loadTypeOffres();

        disponibleCB.setSelected(true); // Par défaut, l'offre est disponible
    }

    private void loadTypeContrats() {
        try {
            typeContratService.recuperer().forEach(typeContrat -> {
                typeContratCB.getItems().add(typeContrat.getNom());
                mapTypeContrat.put(typeContrat.getNom(), typeContrat);
            });
        } catch (SQLException e) {
            showError("Erreur de chargement des types de contrat", e.getMessage());
        }
    }

    private void loadTypeOffres() {
        try {
            typeOffreService.recuperer().forEach(typeOffre -> {
                typeOffreCB.getItems().add(typeOffre.getNom());
                mapTypeOffre.put(typeOffre.getNom(), typeOffre);
            });
        } catch (SQLException e) {
            showError("Erreur de chargement des types d'offre", e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void afficherTypeContrat(ActionEvent event) {
        loadNewScene("/AfficherTypeContrat.fxml");
    }

    public void afficherTypeOffres(ActionEvent event) {
        loadNewScene("/AfficherTypeOffre.fxml");
    }

    public void afficherOffres(ActionEvent actionEvent) {
        loadNewScene("/AfficherOffre.fxml");
    }

    private void loadNewScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            if (gererContratBtn.getScene() != null) {
                gererContratBtn.getScene().setRoot(root);
            }
        } catch (IOException e) {
            showError("Erreur de chargement de la scène", e.getMessage());
        }
    }

    public void ajouterOffre(ActionEvent actionEvent) {
        try {
            if (validateInputs()) {
                double salaire = parseSalaire();
                if (salaire <= 0) {
                    throw new IllegalArgumentException("Le salaire doit être positif.");
                }

                // Récupérer les objets à partir du ComboBox
                TypeContrat typeContrat = mapTypeContrat.get(typeContratCB.getValue());
                TypeOffre typeOffre = mapTypeOffre.get(typeOffreCB.getValue());

                if (typeContrat == null || typeOffre == null) {
                    throw new IllegalArgumentException("Type de contrat ou d'offre non valide.");
                }

                // Création et insertion
                Offre offre = new Offre(
                        typeContrat,
                        typeOffre,
                        posteTF.getText().trim(),
                        entrepriseTF.getText().trim(),
                        localisationTF.getText().trim(),
                        salaire,
                        disponibleCB.isSelected(),
                        imageTF.getText().trim(),
                        utilisateurTF.getText().trim()
                );

                // Gérer l'image
                if (selectedImageFile != null) {
                    // Ici, vous pouvez ajouter la logique pour sauvegarder l'image
                    // Par exemple, copier l'image dans un dossier de votre application
                    // et stocker le chemin relatif dans la base de données
                    String imagePath = saveImage(selectedImageFile);
                    offre.setImage(imagePath);
                }

                offreService.ajouter(offre);
                showSuccess("Offre ajoutée !", "L'offre a été ajoutée avec succès.");
                clearInputs();
            }
        } catch (SQLException | IllegalArgumentException e) {
            showError("Erreur lors de l'ajout de l'offre", e.getMessage());
        }
    }

    @FXML
    public void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        selectedImageFile = fileChooser.showOpenDialog(imagePreview.getScene().getWindow());
        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            imagePreview.setImage(image);
            imageNameLabel.setText(selectedImageFile.getName());
        }
    }

    private String saveImage(File imageFile) {
        // Créer le dossier images s'il n'existe pas
        File imagesDir = new File("src/main/resources/images");
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }

        // Générer un nom unique pour l'image
        String fileName = System.currentTimeMillis() + "_" + imageFile.getName();
        File destFile = new File(imagesDir, fileName);

        try {
            // Copier l'image
            java.nio.file.Files.copy(imageFile.toPath(), destFile.toPath());
            return "images/" + fileName;
        } catch (IOException e) {
            showError("Erreur", "Impossible de sauvegarder l'image : " + e.getMessage());
            return null;
        }
    }

    private boolean validateInputs() {
        if (posteTF.getText().isEmpty() || entrepriseTF.getText().isEmpty() ||
            localisationTF.getText().isEmpty() || salaireTF.getText().isEmpty() ||
            typeContratCB.getValue() == null || typeOffreCB.getValue() == null ||
            utilisateurTF.getText().isEmpty()) {
            showError("Erreur", "Veuillez remplir tous les champs obligatoires");
            return false;
        }
        try {
            Double.parseDouble(salaireTF.getText());
        } catch (NumberFormatException e) {
            showError("Erreur", "Le salaire doit être un nombre valide");
            return false;
        }
        return true;
    }

    private void clearInputs() {
        posteTF.clear();
        entrepriseTF.clear();
        localisationTF.clear();
        salaireTF.clear();
        disponibleCB.setSelected(false);
        typeContratCB.setValue(null);
        typeOffreCB.setValue(null);
        utilisateurTF.clear();
        imagePreview.setImage(null);
        imageNameLabel.setText("Aucune image sélectionnée");
        selectedImageFile = null;
    }

    @FXML
    private void handleOffreClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterOffre.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une Offre");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double parseSalaire() {
        try {
            return Double.parseDouble(salaireTF.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Le salaire doit être un nombre valide.");
        }
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
