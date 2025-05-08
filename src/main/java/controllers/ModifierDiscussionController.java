package controllers;

import entities.Discussion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import services.DiscussionService;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModifierDiscussionController implements Initializable {
    @FXML
    private TextField titleField;
    @FXML
    private TextArea contentArea;

    private Discussion discussion;
    private DiscussionService discussionService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        discussionService = new DiscussionService();
    }

    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
        titleField.setText(discussion.getTitle());
        contentArea.setText(discussion.getContent());
    }

    @FXML
    private void handleSave() {
        if (discussion != null) {
            discussion.setTitle(titleField.getText());
            discussion.setContent(contentArea.getText());
            try {
                discussionService.modifier(discussion);
                closeWindow();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        titleField.getScene().getWindow().hide();
    }
} 