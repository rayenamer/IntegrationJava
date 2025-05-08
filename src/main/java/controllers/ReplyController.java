package controllers;

import entities.Discussion;
import entities.Reply;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import services.ReplyService;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ReplyController implements Initializable {
    @FXML
    private VBox repliesContainer;
    @FXML
    private TextField replyInput;
    @FXML
    private Button replyButton;

    private ReplyService replyService;
    private Discussion currentDiscussion;
    private String currentUserName;
    private String currentUserPhoto;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        replyService = new ReplyService();
        setupReplyButton();
    }

    public void setDiscussion(Discussion discussion) {
        this.currentDiscussion = discussion;
        loadReplies();
    }

    public void setUserInfo(String userName, String userPhoto) {
        this.currentUserName = userName;
        this.currentUserPhoto = userPhoto;
    }

    private void setupReplyButton() {
        replyButton.setOnAction(event -> {
            String replyContent = replyInput.getText().trim();
            if (!replyContent.isEmpty()) {
                try {
                    Reply newReply = new Reply(
                        replyContent,
                        LocalDateTime.now(),
                        currentDiscussion.getUserId(),
                        currentDiscussion.getId(),
                        currentUserName,
                        currentUserPhoto
                    );
                    replyService.ajouter(newReply);
                    replyInput.clear();
                    loadReplies();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to post reply: " + e.getMessage());
                }
            }
        });
    }

    private void loadReplies() {
        if (currentDiscussion == null) return;

        repliesContainer.getChildren().clear();
        try {
            List<Reply> replies = replyService.getRepliesByDiscussion(currentDiscussion.getId());
            for (Reply reply : replies) {
                HBox replyBox = createReplyBox(reply);
                repliesContainer.getChildren().add(replyBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load replies: " + e.getMessage());
        }
    }

    private HBox createReplyBox(Reply reply) {
        HBox replyBox = new HBox(10);
        replyBox.setStyle("-fx-padding: 10px; -fx-background-color: #f0f2f5; -fx-background-radius: 8px;");
        
        // Avatar
        Label avatar = new Label("👤");
        avatar.setStyle("-fx-font-size: 24px; -fx-padding: 8px; -fx-background-color: #e4e6eb; -fx-background-radius: 20px;");
        avatar.setPrefSize(40, 40);
        
        // Reply content
        VBox contentBox = new VBox(5);
        Label username = new Label(reply.getUserName());
        username.setStyle("-fx-font-weight: bold;");
        Text content = new Text(reply.getContent());
        content.setWrappingWidth(300);
        Label timestamp = new Label(reply.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        timestamp.setStyle("-fx-text-fill: #65676b; -fx-font-size: 12px;");
        
        contentBox.getChildren().addAll(username, content, timestamp);
        replyBox.getChildren().addAll(avatar, contentBox);
        
        return replyBox;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 