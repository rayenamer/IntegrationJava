package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;
import javafx.stage.Stage;

public class AcceuilController {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private MenuItem profilMenuItem;

    @FXML
    private MenuItem deconnexionMenuItem;

    public void initialize() {
        profilMenuItem.setOnAction(event -> {
            System.out.println(profilMenuItem.getText());
            // logiques supplémentaires ici si nécessaire
        });

        deconnexionMenuItem.setOnAction(event -> {
            System.out.println(deconnexionMenuItem.getText());
            // logiques supplémentaires ici si nécessaire
        });
    }

    public void loadAjouterOffre() {
        loadUI("/AjouterOffre.fxml");
    }

    public void loadFreelance() {
        loadUI("/Freelance.fxml");
    }

    public void loadEvenement() {
        loadUI("/Evenement.fxml");
    }

    public void loadDiscussion() {
        loadUI("/Discussion.fxml");
    }

    public void loadForum() {
        loadUI("/Forum.fxml");
    }

    private void loadUI(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
