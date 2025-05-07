package wiemwebsite.demo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import wiemwebsite.demo.models.Missionfreelencer;
import wiemwebsite.demo.services.CloudinaryUploader;
import wiemwebsite.demo.services.MissionfreelencerService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.File;
import java.sql.Date;
import java.util.Map;

import static wiemwebsite.demo.services.CloudinaryUploader.cloudinary;

public class MissionfreelencerController {

    @FXML private TextField txtTitre;
    @FXML private TextField txtPrix;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker dpDateLimite;
    @FXML private TextField txtSpecialite;
    @FXML private DatePicker dpDate;
    @FXML private ImageView imgMission;
    @FXML private Button uploadImageBtn;

    private final MissionfreelencerService missionService = new MissionfreelencerService();
    private MissionListController missionListController;
    private String uploadedImageUrl = "";

    private final int currentUserId = 1;

    public void setMissionListController(MissionListController controller) {
        this.missionListController = controller;
    }

    @FXML

    private void ajouterMission() {
        try {
            // Vérification des champs obligatoires
            if (txtTitre.getText().isEmpty() || txtPrix.getText().isEmpty()
                    || dpDateLimite.getValue() == null || dpDate.getValue() == null) {
                showError("Tous les champs obligatoires doivent être remplis");
                return;
            }

            // Vérification du prix
            double prix;
            try {
                prix = Double.parseDouble(txtPrix.getText());
                if (prix <= 0) {  // Le prix doit être positif
                    showError("Le prix doit être un nombre positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Le prix doit être un nombre valide.");
                return;
            }

            // Vérification de la longueur de la description
            if (txtDescription.getText().length() < 10) {
                showError("La description doit contenir au moins 10 caractères.");
                return;
            }

            // Vérification de la date limite (la date limite doit être après la date actuelle)
            if (dpDateLimite.getValue().isBefore(dpDate.getValue())) {
                showError("La date limite ne peut pas être avant la date actuelle.");
                return;
            }

            // Vérification du format de l'image (si l'image est choisie, elle doit être valide)
            String imageUrl = uploadedImageUrl != null ? uploadedImageUrl : "";


            // Création de la mission
            Missionfreelencer newMission = new Missionfreelencer(
                    0,
                    txtTitre.getText(),
                    prix,
                    txtDescription.getText(),
                    Date.valueOf(dpDateLimite.getValue()),
                    txtSpecialite.getText(),
                    Date.valueOf(dpDate.getValue()),
                    currentUserId,
                    imageUrl
            );

            // Appel du service pour ajouter la mission
            missionService.ajouterMission(newMission);
            clearFields();
            showSuccess("Mission ajoutée avec succès !");
            redirectToMissionList();

        } catch (Exception e) {
            showError("Erreur lors de l'ajout de la mission : " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void redirectToMissionList() {
        try {
            String fxmlPath = "/wiemwebsite/demo/FXML/listmissionfreelencer.fxml";
            java.net.URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("Erreur : Fichier FXML non trouvé à l'emplacement : " + fxmlPath);
                throw new RuntimeException("Fichier FXML non trouvé : " + fxmlPath);
            }
            System.out.println("Chargement de listmissionfreelencer.fxml depuis : " + fxmlUrl);

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene listScene = new Scene(loader.load(), 600, 400);
            MissionListController listController = loader.getController();
            this.missionListController = listController;
            if (missionListController != null) {
                missionListController.refreshMissions();
            } else {
                System.err.println("Erreur : MissionListController est null");
            }
            Stage stage = (Stage) txtTitre.getScene().getWindow();
            stage.setScene(listScene);
            stage.setTitle("Liste des Missions");
            stage.show();
        } catch (Exception e) {
            showError("Erreur lors de la redirection vers la liste : " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void clearFields() {
        txtTitre.clear();
        txtPrix.clear();
        txtDescription.clear();
        txtSpecialite.clear();
        dpDateLimite.setValue(null);
        dpDate.setValue(null);
        imgMission.setImage(null);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(uploadImageBtn.getScene().getWindow());

        if (file != null) {
            try {
                imgMission.setImage(new Image(file.toURI().toString()));
                String imageUrl = CloudinaryUploader.uploadImage(file);
                this.uploadedImageUrl = imageUrl;

            } catch (Exception e) {
                showError("Échec de l'upload de l'image : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }



}