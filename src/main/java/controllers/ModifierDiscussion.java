package controllers;

import entities.Discussion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.DiscussionService;

import java.io.IOException;

public class ModifierDiscussion {
    @FXML
    private TextField titleField;
    @FXML
    private TextArea contentField;

    private Discussion discussion;
    private final DiscussionService ds = new DiscussionService();

    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
        titleField.setText(discussion.getTitle());
        contentField.setText(discussion.getContent());
    }

    @FXML
    private void modifierDiscussion() {
        try {
            discussion.setTitle(titleField.getText());
            discussion.setContent(contentField.getText());
            ds.modifier(discussion);

            // Close the window after saving
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Discussions.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setTitle("Discussions");
            newStage.setScene(new Scene(root));
            newStage.show();

        } catch (Exception e) {
            System.out.println("Erreur lors de la modification: " + e.getMessage());
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
