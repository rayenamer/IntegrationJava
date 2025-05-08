package controllers;

import entities.Discussion;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.DiscussionService;
import utils.Session;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Random;


import utils.Session;

public class AjouterDiscussion {

    @javafx.fxml.FXML
    private TextField titleTF;
    @javafx.fxml.FXML
    private TextField contentTA;
    @javafx.fxml.FXML
    private TextField userNameTF;
    @javafx.fxml.FXML
    private TextField userPhotoTF;
    @javafx.fxml.FXML
    private TextField userIdTF;
    @javafx.fxml.FXML
    private TextField likesTF;
    @javafx.fxml.FXML
    private TextField dislikesTF;
    @javafx.fxml.FXML
    private Label errorLabel;

    private User currentUser;


    private boolean isInputValid() {
        boolean valid = true;
        StringBuilder errorMessage = new StringBuilder();

        String title = titleTF.getText().trim();
        String content = contentTA.getText().trim();


        // Reset styles and error message
        titleTF.setStyle(null);
        contentTA.setStyle(null);
        errorLabel.setText("");

        // Title validation
        if (title.isEmpty()) {
            titleTF.setStyle("-fx-border-color: red;");
            errorMessage.append("Le titre ne doit pas être vide.\n");
            valid = false;
        } else if (title.length() < 5 || title.length() > 100 || !title.matches("[a-zA-Z0-9 ]+")) {
            titleTF.setStyle("-fx-border-color: red;");
            errorMessage.append("Le titre doit contenir entre 5 et 100 caractères, sans caractères spéciaux.\n");
            valid = false;
        } else if (title.toLowerCase().contains("badword")) {
            titleTF.setStyle("-fx-border-color: red;");
            errorMessage.append("Le titre ne doit pas contenir de mots inappropriés.\n");
            valid = false;
        }

        // Content validation
        if (content.isEmpty()) {
            contentTA.setStyle("-fx-border-color: red;");
            errorMessage.append("Le contenu ne doit pas être vide.\n");
            valid = false;
        } else if (content.length() < 5 || content.length() > 100 || !content.matches("[a-zA-Z0-9 ]+")) {
            contentTA.setStyle("-fx-border-color: red;");
            errorMessage.append("Le contenu doit contenir entre 5 et 100 caractères, sans caractères spéciaux.\n");
            valid = false;
        } else if (content.toLowerCase().contains("badword")) {
            contentTA.setStyle("-fx-border-color: red;");
            errorMessage.append("Le contenu ne doit pas contenir de mots inappropriés.\n");
            valid = false;
        }



        if (!valid) {
            errorLabel.setText(errorMessage.toString());
        }

        return valid;
    }





    @javafx.fxml.FXML
    private void addDiscussion(ActionEvent event) {
        currentUser = Session.getCurrentUser();
        if (!isInputValid()) {
            return; // Cancel adding if validation fails
        }
        DiscussionService ds = new DiscussionService();

        String title = titleTF.getText().trim();
        String content = contentTA.getText().trim();
        String userName = currentUser.getPrenom();
        String userPhoto = " ";
        int userId = currentUser.getId();
        int likes = 0;
        int dislikes = 0;
        LocalDateTime createdAt = LocalDateTime.now();
        //Random rand = new Random();
        //int id = rand.nextInt(1000) + 1;
        Discussion d = new Discussion(title, content, createdAt, userId, userName, userPhoto);
        try {
            ds.ajouter(d);
            this.showDiscussions(event);
        }catch (SQLException e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Erreur");
            a.setContentText(e.getMessage());
            a.show();
        }
    }

    @javafx.fxml.FXML
    private void showDiscussions(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Discussions.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close(); // Close the current window

            Stage newStage = new Stage();
            newStage.setTitle("Discussions");
            newStage.setScene(new Scene(root));
            newStage.show();

        } catch (IOException e) {
            System.out.println("Failed to load Discussions.fxml: " + e.getMessage());
        }
    }

}
