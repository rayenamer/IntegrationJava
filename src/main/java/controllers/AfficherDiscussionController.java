package controllers;

import entities.Discussion;
import entities.Reply;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.DiscussionService;
import services.ReplyService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AfficherDiscussionController implements Initializable {
    @FXML
    private ListView<Discussion> discussionListView;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnModify;
    @FXML
    private Button btnStatistics;
    @FXML
    private TextField searchField;
    @FXML
    private Button btnSortMostLiked;
    @FXML
    private Button btnSortNewest;
    @FXML
    private Button btnSortMostCommented;

    @FXML
    private Button btnWit;

    @FXML
    private Button btnJoke;


    private DiscussionService discussionService;
    private ReplyService replyService;
    private String currentUserName = "Current User"; // This should be replaced with actual logged-in user

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        discussionService = new DiscussionService();
        replyService = new ReplyService();
        setupListView();
        setupButtons();
        loadDiscussions();

        // Set up search field listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterDiscussions(newValue));

    }
    private void sortDiscussionsByMostLiked() {
        try {
            List<Discussion> discussions = discussionService.recuperer();
            discussions.sort((d1, d2) -> Integer.compare(d2.getLikes(), d1.getLikes())); // Sort by likes in descending order
            discussionListView.getItems().setAll(discussions);
        } catch (SQLException e) {
            showAlert("Error", "Failed to sort discussions by likes: " + e.getMessage());
        }
    }

    private void sortDiscussionsByNewest() {
        try {
            List<Discussion> discussions = discussionService.recuperer();
            discussions.sort((d1, d2) -> d2.getCreatedAt().compareTo(d1.getCreatedAt())); // Sort by date in descending order
            discussionListView.getItems().setAll(discussions);
        } catch (SQLException e) {
            showAlert("Error", "Failed to sort discussions by date: " + e.getMessage());
        }
    }

    private void sortDiscussionsByMostCommented() {
        try {
            List<Discussion> discussions = discussionService.recuperer();
            discussions.sort((d1, d2) -> Integer.compare(getCommentCount(d2), getCommentCount(d1))); // Sort by comment count in descending order
            discussionListView.getItems().setAll(discussions);
        } catch (SQLException e) {
            showAlert("Error", "Failed to sort discussions by comments: " + e.getMessage());
        }
    }

    private int getCommentCount(Discussion discussion) {
        try {
            return replyService.getRepliesByDiscussion(discussion.getId()).size();
        } catch (SQLException e) {
            showAlert("Error", "Failed to count comments: " + e.getMessage());
            return 0;
        }
    }



    private void filterDiscussions(String searchText) {
        try {
            // If searchText is empty, load all discussions
            if (searchText == null || searchText.isEmpty()) {
                discussionListView.getItems().setAll(discussionService.recuperer());
            } else {
                // Otherwise, filter the discussions based on title or content
                List<Discussion> filteredDiscussions = discussionService.recuperer().stream()
                        .filter(discussion -> discussion.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                                discussion.getContent().toLowerCase().contains(searchText.toLowerCase()))
                        .collect(Collectors.toList());

                discussionListView.getItems().setAll(filteredDiscussions);
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to filter discussions: " + e.getMessage());
        }
    }


    private void setupListView() {
        discussionListView.setCellFactory(param -> new ListCell<Discussion>() {
            private VBox repliesBox;
            private TextField replyInput;
            private VBox postCard;

            @Override
            protected void updateItem(Discussion discussion, boolean empty) {
                super.updateItem(discussion, empty);
                if (empty || discussion == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Main container
                    postCard = new VBox(10);
                    postCard.getStyleClass().addAll("post-card");
                    postCard.prefWidthProperty().bind(discussionListView.widthProperty().subtract(32));
                    postCard.setMaxWidth(Control.USE_PREF_SIZE);

                    // Header with avatar and user info
                    HBox postHeader = new HBox(10);
                    postHeader.getStyleClass().add("post-header");
                    postHeader.prefWidthProperty().bind(postCard.prefWidthProperty());

                    // Avatar
                    Label avatar = new Label("👤");
                    avatar.getStyleClass().add("avatar");

                    // User info
                    VBox userInfo = new VBox(2);
                    Label userName = new Label(discussion.getUserName());
                    userName.getStyleClass().add("user-name");
                    Label timestamp = new Label(discussion.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
                    timestamp.getStyleClass().add("timestamp");
                    userInfo.getChildren().addAll(userName, timestamp);

                    postHeader.getChildren().addAll(avatar, userInfo);

                    // Content
                    VBox contentBox = new VBox(5);
                    contentBox.prefWidthProperty().bind(postCard.prefWidthProperty());
                    
                    Label title = new Label(discussion.getTitle());
                    title.getStyleClass().add("post-title");
                    title.setWrapText(true);
                    title.prefWidthProperty().bind(contentBox.prefWidthProperty());
                    
                    Text content = new Text(discussion.getContent());
                    content.getStyleClass().add("post-content");
                    content.wrappingWidthProperty().bind(contentBox.prefWidthProperty().subtract(16));
                    
                    contentBox.getChildren().addAll(title, content);

                    // Action buttons
                    HBox actionButtons = new HBox(15);
                    actionButtons.getStyleClass().add("post-actions");
                    actionButtons.prefWidthProperty().bind(postCard.prefWidthProperty());

                    // Like button with counter
                    HBox likeContainer = new HBox(8);
                    likeContainer.setAlignment(Pos.CENTER_LEFT);
                    Button likeBtn = new Button("👍 Like");
                    likeBtn.getStyleClass().add("action-icon-button");
                    Label likeCount = new Label(formatCount(discussion.getLikes()));
                    likeCount.getStyleClass().addAll("timestamp", "like-count");
                    likeContainer.getChildren().addAll(likeBtn, likeCount);

                    // Comment button
                    Button commentBtn = new Button("💬 Comment");
                    commentBtn.getStyleClass().add("action-icon-button");

                    // Share button
                    Button shareBtn = new Button("↗ Share");
                    shareBtn.getStyleClass().add("action-icon-button");

                    actionButtons.getChildren().addAll(likeContainer, commentBtn, shareBtn);

                    // Like button functionality
                    likeBtn.setOnAction(event -> {
                        try {
                            int currentLikes = discussion.getLikes();
                            discussion.setLikes(currentLikes + 1);
                            discussionService.modifier(discussion);
                            likeCount.setText(formatCount(currentLikes + 1));
                        } catch (SQLException e) {
                            showAlert("Error", "Failed to update likes: " + e.getMessage());
                        }
                    });

                    // Replies section
                    repliesBox = new VBox(5);
                    repliesBox.getStyleClass().add("replies-section");
                    repliesBox.prefWidthProperty().bind(postCard.prefWidthProperty());
                    
                    // Reply input section
                    HBox replyInputBox = new HBox(10);
                    replyInputBox.getStyleClass().add("reply-input-box");
                    replyInputBox.setAlignment(Pos.CENTER_LEFT);
                    replyInputBox.prefWidthProperty().bind(postCard.prefWidthProperty());
                    
                    Label replyAvatar = new Label("👤");
                    replyAvatar.getStyleClass().add("avatar");
                    
                    replyInput = new TextField();
                    replyInput.setPromptText("Write a comment...");
                    replyInput.getStyleClass().add("reply-input");
                    HBox.setHgrow(replyInput, Priority.ALWAYS);
                    
                    Button postReplyBtn = new Button("Post");
                    postReplyBtn.getStyleClass().addAll("action-button", "create-post-button");
                    
                    replyInputBox.getChildren().addAll(replyAvatar, replyInput, postReplyBtn);

                    // Post reply functionality
                    postReplyBtn.setOnAction(event -> {
                        String replyContent = replyInput.getText().trim();
                        if (!replyContent.isEmpty()) {
                            try {
                                Reply newReply = new Reply(
                                    replyContent,
                                    LocalDateTime.now(),
                                    Integer.valueOf(1), // Default user ID as Integer
                                    discussion.getId(), // Already an Integer from Discussion entity
                                    currentUserName,
                                    ""
                                );
                                replyService.ajouter(newReply);
                                replyInput.clear();
                                loadReplies(discussion.getId());
                            } catch (SQLException e) {
                                showAlert("Error", "Failed to post reply: " + e.getMessage());
                            }
                        }
                    });
                    btnStatistics.setOnAction(event -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Statistics.fxml"));
                            Scene scene = new Scene(loader.load());
                            Stage stage = new Stage();
                            stage.setTitle("Statistics");
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException e) {
                            showAlert("Error", "Failed to open statistics window: " + e.getMessage());
                        }
                    });

                    btnWit.setOnAction(event -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/WitInterface.fxml"));
                            Scene scene = new Scene(loader.load());
                            Stage stage = new Stage();
                            stage.setTitle("Ai");
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException e) {
                            showAlert("Error", "Failed to open statistics window: " + e.getMessage());
                        }
                    });

                    btnJoke.setOnAction(event -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/joke_view.fxml"));
                            Scene scene = new Scene(loader.load());
                            Stage stage = new Stage();
                            stage.setTitle("Ai");
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException e) {
                            showAlert("Error", "Failed to open statistics window: " + e.getMessage());
                        }
                    });


                    // Load existing replies
                    loadReplies(discussion.getId());

                    // Add all components to the post card
                    postCard.getChildren().addAll(
                        postHeader, 
                        contentBox, 
                        actionButtons,
                        new Separator(),
                        repliesBox,
                        replyInputBox
                    );
                    setGraphic(postCard);
                }
            }

            private String formatCount(int count) {
                if (count < 1000) return Integer.toString(count);
                if (count < 1000000) return String.format("%.1fK", count/1000.0);
                return String.format("%.1fM", count/1000000.0);
            }

            private void loadReplies(Integer discussionId) {
                try {
                    List<Reply> replies = replyService.getRepliesByDiscussion(discussionId);
                    repliesBox.getChildren().clear();
                    
                    for (Reply reply : replies) {
                        HBox replyBox = createReplyBox(reply);
                        repliesBox.getChildren().add(replyBox);
                    }
                } catch (SQLException e) {
                    showAlert("Error", "Failed to load replies: " + e.getMessage());
                }
            }

            private HBox createReplyBox(Reply reply) {
                HBox replyBox = new HBox(10);
                replyBox.getStyleClass().add("reply-box");
                replyBox.prefWidthProperty().bind(repliesBox.prefWidthProperty().subtract(16));
                
                // Avatar
                Label avatar = new Label("👤");
                avatar.getStyleClass().add("avatar");
                
                // Reply content
                VBox contentBox = new VBox(2);
                contentBox.prefWidthProperty().bind(replyBox.prefWidthProperty().subtract(50));
                
                Label username = new Label(reply.getUserName());
                username.getStyleClass().add("user-name");
                
                Text content = new Text(reply.getContent());
                content.getStyleClass().add("reply-content");
                content.wrappingWidthProperty().bind(contentBox.prefWidthProperty());
                
                Label timestamp = new Label(reply.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
                timestamp.getStyleClass().add("timestamp");
                
                contentBox.getChildren().addAll(username, content, timestamp);
                replyBox.getChildren().addAll(avatar, contentBox);
                
                return replyBox;
            }
        });

        // Listen for width changes and update cell sizes
        discussionListView.widthProperty().addListener((obs, oldVal, newVal) -> {
            discussionListView.refresh();
        });
    }

    private void setupButtons() {
        btnAdd.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterDiscussion.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                showAlert("Error", "Failed to open add discussion window: " + e.getMessage());
            }
        });

        btnDelete.setOnAction(event -> {
            Discussion selected = discussionListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    discussionService.supprimer(selected);
                    loadDiscussions();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to delete discussion: " + e.getMessage());
                }
            }
        });

        btnModify.setOnAction(event -> {
            Discussion selected = discussionListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierDiscussion.fxml"));
                    Scene scene = new Scene(loader.load());
                    ModifierDiscussionController controller = loader.getController();
                    controller.setDiscussion(selected);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    showAlert("Error", "Failed to open edit window: " + e.getMessage());
                }
            }
        });

        // Sorting buttons functionality
        btnSortMostLiked.setOnAction(event -> sortDiscussionsByMostLiked());
        btnSortNewest.setOnAction(event -> sortDiscussionsByNewest());
        btnSortMostCommented.setOnAction(event -> sortDiscussionsByMostCommented());
    }

    private void loadDiscussions() {
        try {
            discussionListView.getItems().setAll(discussionService.recuperer());
        } catch (SQLException e) {
            showAlert("Error", "Failed to load discussions: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void home(ActionEvent actionEvent) {
        try {
            String userType = getCurrentUserType();  // Replace this with your method to get the current user type

            FXMLLoader loader;
            if ("Freelancer".equals(userType) || "Chercheur".equals(userType)) {
                loader = new FXMLLoader(getClass().getResource("/Acceuilfc.fxml"));
            } else if ("Moderateur".equals(userType)) {
                loader = new FXMLLoader(getClass().getResource("/Acceuil.fxml"));
            } else {
                // Handle case for unknown user type or error
                System.out.println("Unknown user type: " + userType);
                return;
            }

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Accueil");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            // Handle the exception properly, for example show an error message
            System.err.println("Error loading the scene: " + e.getMessage());
        }
    }

    // This method should return the current user type, replace with your actual logic to get user type
    private String getCurrentUserType() {
        // Logic to get current user type (e.g., Freelancer, Chercheur, Moderateur)
        // This could be based on a session, user role, or any other logic you are using
        return "Freelancer";  // Example return, change as per your logic
    }

}
