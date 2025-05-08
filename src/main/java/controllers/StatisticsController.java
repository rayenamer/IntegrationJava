package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import services.DiscussionService;
import services.ReplyService;

import java.net.URL;
import java.util.ResourceBundle;

public class StatisticsController implements Initializable {

    @FXML private Label lblNbrDiscussions;
    @FXML private Label lblNbrReplies;
    @FXML private Label lblTopDiscussionUser;
    @FXML private Label lblTopReplyUser;

    private final DiscussionService discussionService = new DiscussionService();
    private final ReplyService replyService = new ReplyService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int totalDiscussions = discussionService.getNumberOfDiscussions();
        int totalReplies = replyService.getNumberOfReplies();
        String topDiscussionUser = discussionService.getTopContributorUsername();
        String topReplyUser = replyService.getTopReplierUsername();

        lblNbrDiscussions.setText("📝 Total Discussions: " + totalDiscussions);
        lblNbrReplies.setText("💬 Total Replies: " + totalReplies);
        lblTopDiscussionUser.setText("🏆 Top Discussion Poster: " + topDiscussionUser);
        lblTopReplyUser.setText("🎯 Top Replier: " + topReplyUser);
    }
}
