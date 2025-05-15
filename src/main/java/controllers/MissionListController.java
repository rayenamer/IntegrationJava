package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import entities.Missionfreelencer;
import services.MissionfreelencerService;
import javafx.fxml.Initializable;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MissionListController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(MissionListController.class.getName());
    private static final String DEFAULT_IMAGE_URL = "https://via.placeholder.com/120";

    @FXML private VBox missionListVBox;
    @FXML private ScrollPane scrollPane;
    @FXML private TextField titleFilter;
    @FXML private TextField minPriceFilter;
    @FXML private TextField maxPriceFilter;
    @FXML private Button searchButton;
    @FXML private Button addMissionButton; // Added for "Ajouter Nouvelle Mission"

    private final MissionfreelencerService missionService = new MissionfreelencerService();
    private Missionfreelencer selectedMission; // Added to resolve 'selectedMission' symbol

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.info("Initialisation de MissionListController...");
        missionService.testConnection();

        // Vérification des injections FXML
        if (missionListVBox == null || scrollPane == null || titleFilter == null ||
                minPriceFilter == null || maxPriceFilter == null || searchButton == null ||
                addMissionButton == null) {
            LOGGER.severe("Injection FXML échouée : missionListVBox=" + missionListVBox +
                    ", scrollPane=" + scrollPane + ", titleFilter=" + titleFilter +
                    ", minPriceFilter=" + minPriceFilter + ", maxPriceFilter=" + maxPriceFilter +
                    ", searchButton=" + searchButton + ", addMissionButton=" + addMissionButton);
            showError("Erreur d'initialisation de l'interface. Veuillez vérifier le fichier FXML.");
            return;
        }

        // Charger le fichier CSS
        URL cssURL = getClass().getResource("/style/missionform.css");
        if (cssURL == null) {
            LOGGER.severe("Fichier CSS missionform.css introuvable à /wiemwebsite/demo/style/missionform.css");
        } else {
            LOGGER.info("Fichier CSS trouvé : " + cssURL.toExternalForm());
            scrollPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.getStylesheets().add(cssURL.toExternalForm());
                }
            });
        }

        refreshMissions();
    }

    public void refreshMissions() {
        try {
            LOGGER.info("Chargement des missions...");
            ObservableList<Missionfreelencer> missions = missionService.getAllMissions();
            LOGGER.info("Nombre de missions chargées : " + missions.size());

            Platform.runLater(() -> {
                missionListVBox.getChildren().clear();
                if (missions.isEmpty()) {
                    Label noMissionsLabel = createStyledLabel("Aucune mission disponible.", "no-missions-label");
                    missionListVBox.getChildren().add(noMissionsLabel);
                } else {
                    missions.forEach(mission -> missionListVBox.getChildren().add(createMissionCard(mission)));
                }
                missionListVBox.requestLayout();
                scrollPane.requestLayout();
            });
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors du chargement des missions : " + e.getMessage(), e);
            showError("Erreur lors du chargement des missions : " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue : " + e.getMessage(), e);
            showError("Erreur inattendue : " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        try {
            // Récupérer les filtres
            String title = titleFilter.getText();
            Double minPrice = null;
            Double maxPrice = null;

            // Valider et convertir les prix
            try {
                if (!minPriceFilter.getText().isEmpty()) {
                    minPrice = Double.parseDouble(minPriceFilter.getText());
                }
                if (!maxPriceFilter.getText().isEmpty()) {
                    maxPrice = Double.parseDouble(maxPriceFilter.getText());
                }
            } catch (NumberFormatException e) {
                showError("Les prix doivent être des nombres valides.");
                return;
            }

            // Effectuer la recherche
            ObservableList<Missionfreelencer> missions = missionService.searchMissionsByTitleAndPrice(title, minPrice, maxPrice);
            Platform.runLater(() -> {
                missionListVBox.getChildren().clear();
                if (missions.isEmpty()) {
                    Label noMissionsLabel = createStyledLabel("Aucune mission trouvée.", "no-missions-label");
                    missionListVBox.getChildren().add(noMissionsLabel);
                } else {
                    missions.forEach(mission -> missionListVBox.getChildren().add(createMissionCard(mission)));
                }
                missionListVBox.requestLayout();
                scrollPane.requestLayout();
            });
            LOGGER.info("Recherche effectuée avec titre=" + title +
                    ", minPrice=" + minPrice + ", maxPrice=" + maxPrice +
                    ", résultats=" + missions.size());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche : " + e.getMessage(), e);
            showError("Erreur lors de la recherche : " + e.getMessage());
        }
    }

    @FXML
    private void handleEditSelectedMission() {
        if (selectedMission == null) {
            showError("Veuillez sélectionner une mission à modifier.");
            return;
        }
        handleEditMission(selectedMission);
    }

    private void handleEditMission(Missionfreelencer mission) {
        try {
            URL fxmlUrl = getClass().getResource("/Modifiermission.fxml");
            if (fxmlUrl == null) {
                LOGGER.severe("Fichier missionfreelancer.fxml introuvable");
                showError("Erreur : Formulaire de modification de mission introuvable.");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            MissionfreelencerController controller = loader.getController();
            if (controller == null) {
                LOGGER.severe("Contrôleur MissionfreelancerController non trouvé");
                showError("Erreur : Contrôleur du formulaire non chargé.");
                return;
            }

            controller.setMissionListController(this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Modifier Mission - " + (mission.getTitre() != null ? mission.getTitre() : "Mission #" + mission.getId()));
            stage.setScene(scene);

            URL cssURL = getClass().getResource("/style/missionform.css");
            if (cssURL != null) {
                scene.getStylesheets().add(cssURL.toExternalForm());
            }

            stage.show();
            LOGGER.info("Fenêtre de modification de mission ouverte pour mission ID=" + mission.getId());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de missionfreelancer.fxml : " + e.getMessage(), e);
            showError("Erreur lors de l'ouverture du formulaire de modification : " + e.getMessage());
        }
    }

    @FXML
    private void handleAddMission() {
        try {
            URL fxmlUrl = getClass().getResource("/Missionfreelencer.fxml");
            if (fxmlUrl == null) {
                LOGGER.severe("Fichier missionfreelancer.fxml introuvable à /wiemwebsite/demo/FXML/Missionfreelencer.fxml");
                showError("Erreur : Formulaire de création de mission introuvable.");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            MissionfreelencerController controller = loader.getController();
            if (controller == null) {
                LOGGER.severe("Contrôleur MissionfreelancerController non trouvé");
                showError("Erreur : Contrôleur du formulaire non chargé.");
                return;
            }

            // Passer une référence à ce MissionListController
            controller.setMissionListController(this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Ajouter une Nouvelle Mission");
            stage.setScene(scene);

            URL cssURL = getClass().getResource("/style/missionform.css");
            if (cssURL != null) {
                scene.getStylesheets().add(cssURL.toExternalForm());
            }

            stage.show();
            LOGGER.info("Fenêtre de création de mission ouverte.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de missionfreelancer.fxml : " + e.getMessage(), e);
            showError("Erreur lors de l'ouverture du formulaire : " + e.getMessage());
        }
    }

    private HBox createMissionCard(Missionfreelencer mission) {
        HBox card = new HBox(20);
        card.getStyleClass().add("mission-card");
        card.setPadding(new javafx.geometry.Insets(15));

        VBox infoBox = new VBox(8);
        infoBox.setPrefWidth(400);

        Label titleLabel = createStyledLabel(
                mission.getTitre() != null ? mission.getTitre() : "Titre non spécifié", "card-title");
        Label budgetLabel = createStyledLabel("Budget : " + mission.getPrix() + " DT", "card-info");
        Label competencesLabel = createStyledLabel(
                "Compétence : " + (mission.getSpecialite() != null ? mission.getSpecialite() : "Non spécifiées"), "card-info");
        Label dateLimiteLabel = createStyledLabel(
                "Date limite : " + (mission.getDatelimite() != null ? mission.getDatelimite().toString() : "Non spécifiée"), "card-info");
        Label dateDebutLabel = createStyledLabel(
                "Date de début : " + (mission.getDate() != null ? mission.getDate().toString() : "Non spécifiée"), "card-info");
        Label descriptionLabel = createStyledLabel(
                mission.getDescription() != null ? mission.getDescription() : "Aucune description disponible.", "card-description");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(380);

        infoBox.getChildren().addAll(titleLabel, budgetLabel, competencesLabel, dateLimiteLabel, dateDebutLabel, descriptionLabel);

        VBox rightBox = new VBox(10);
        rightBox.setAlignment(javafx.geometry.Pos.CENTER);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("card-image");
        loadImage(imageView, mission.getImage_url());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        Button applyButton = new Button("Postuler");
        applyButton.getStyleClass().add("apply-button");
        applyButton.setOnAction(e -> handleApply(mission));

        Button editButton = new Button("Modifier");
        editButton.getStyleClass().add("edit-button");
        editButton.setOnAction(e -> handleEditMission(mission));

        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> confirmDeleteMission(mission));

        Button consulterButton = new Button("Consulter les candidatures");
        consulterButton.getStyleClass().add("consulter-button");
        consulterButton.setOnAction(e -> handleConsulterCandidatures(mission));

        buttonBox.getChildren().addAll(applyButton, editButton, deleteButton, consulterButton);
        rightBox.getChildren().addAll(imageView, buttonBox);

        card.getChildren().addAll(infoBox, rightBox);
        return card;
    }

    private Label createStyledLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private void loadImage(ImageView imageView, String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                imageView.setImage(new Image(imageUrl, true));
            } else {
                imageView.setImage(new Image(DEFAULT_IMAGE_URL, true));
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur lors du chargement de l'image : " + imageUrl, e);
            imageView.setImage(new Image(DEFAULT_IMAGE_URL, true));
        }
    }

    private void confirmDeleteMission(Missionfreelencer mission) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Voulez-vous vraiment supprimer cette mission ?");
        confirm.setContentText("Mission : " + (mission.getTitre() != null ? mission.getTitre() : "Mission #" + mission.getId()));

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    missionService.supprimerMission(mission.getId());
                    refreshMissions();
                    showSuccess("Mission supprimée avec succès !");
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de la mission ID=" + mission.getId() + " : " + e.getMessage(), e);
                    showError("Erreur lors de la suppression : " + e.getMessage());
                }
            }
        });
    }

    private void handleApply(Missionfreelencer mission) {
        LOGGER.info("Clic sur Postuler pour la mission ID=" + mission.getId() + ", Titre=" + mission.getTitre());
        try {
            URL fxmlUrl = getClass().getResource("/postulemission.fxml");
            if (fxmlUrl == null) {
                LOGGER.severe("Fichier postulermission.fxml introuvable à /wiemwebsite/demo/FXML/postulemission.fxml");
                showError("Erreur : Formulaire introuvable.");
                return;
            }
            LOGGER.info("Chargement de postulermission.fxml : " + fxmlUrl.toString());

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            PostulerMissionController controller = loader.getController();
            if (controller == null) {
                LOGGER.severe("Contrôleur PostulerMissionController non trouvé");
                showError("Erreur : Contrôleur du formulaire non chargé.");
                return;
            }

            controller.setMission(mission);
            LOGGER.info("Mission transmise au contrôleur : ID=" + mission.getId());

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Formulaire de Candidature - " + (mission.getTitre() != null ? mission.getTitre() : "Mission #" + mission.getId()));
            stage.setScene(scene);

            URL cssURL = getClass().getResource("/style/missionform.css");
            if (cssURL != null) {
                scene.getStylesheets().add(cssURL.toExternalForm());
            }

            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de postulermission.fxml : " + e.getMessage(), e);
            showError("Erreur lors de l'ouverture du formulaire : " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue dans handleApply : " + e.getMessage(), e);
            showError("Erreur inattendue : " + e.getMessage());
        }
    }

    private void handleConsulterCandidatures(Missionfreelencer mission) {
        LOGGER.info("Clic sur Consulter les candidatures pour la mission ID=" + mission.getId() + ", Titre=" + mission.getTitre());
        try {
            URL fxmlUrl = getClass().getResource("/demande_mission.fxml");
            if (fxmlUrl == null) {
                LOGGER.severe("Fichier demande_mission.fxml introuvable à /wiemwebsite/demo/FXML/demande_mission.fxml");
                showError("Erreur : Interface des candidatures introuvable.");
                return;
            }
            LOGGER.info("Chargement de demande_mission.fxml : " + fxmlUrl.toString());

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            DemandeMissionController controller = loader.getController();
            if (controller == null) {
                LOGGER.severe("Contrôleur DemandeMissionController non trouvé");
                showError("Erreur : Contrôleur des candidatures non chargé.");
                return;
            }

            controller.setMissionId(mission.getId());
            LOGGER.info("Mission ID=" + mission.getId() + " transmise au contrôleur");

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Candidatures pour Mission - " + (mission.getTitre() != null ? mission.getTitre() : "Mission #" + mission.getId()));
            stage.setScene(scene);

            URL cssURL = getClass().getResource("/style/missionform.css");
            if (cssURL != null) {
                scene.getStylesheets().add(cssURL.toExternalForm());
            }

            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de demande_mission.fxml : " + e.getMessage(), e);
            showError("Erreur lors de l'ouverture de l'interface des candidatures : " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue dans handleConsulterCandidatures : " + e.getMessage(), e);
            showError("Erreur inattendue : " + e.getMessage());
        }
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
    public void home(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Acceuilfc.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une Candidature");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError(e.getMessage());
        }
    }
}