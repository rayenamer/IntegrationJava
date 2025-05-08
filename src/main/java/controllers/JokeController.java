package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import services.JokeService;

public class JokeController {

    @FXML
    private TextArea jokeArea;

    @FXML
    private Button jokeButton;

    private final JokeService jokeService = new JokeService();

    @FXML
    private void initialize() {
        jokeButton.setOnAction(e -> {
            jokeArea.setText("Fetching joke...");
            new Thread(() -> {
                String response = jokeService.fetchRawJokeResponse();
                javafx.application.Platform.runLater(() -> jokeArea.setText(response));
            }).start();
        });
    }
}
