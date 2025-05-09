package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import services.WitAiService;

public class WitController {

    @FXML
    private TextField inputField;

    @FXML
    private TextArea responseArea;

    @FXML
    private Button sendButton;

    private final WitAiService witAiService = new WitAiService();

    @FXML
    private void initialize() {
        sendButton.setOnAction(e -> {
            String userMessage = inputField.getText().trim();

            if (!userMessage.isEmpty()) {
                responseArea.setText("Loading...");
                new Thread(() -> {
                    String jsonResponse = witAiService.sendMessageToGemini(userMessage);

                    String FiltrerResponse = filtredResponse(jsonResponse);

                    javafx.application.Platform.runLater(() -> responseArea.setText(FiltrerResponse));
                }).start();
            }
        });
    }

    public String filtredResponse(String response) {
        if (response == null || response.isEmpty()) {
            return "";
        }
        int newlineIndex = response.indexOf("\\n");
        return newlineIndex != -1 ? response.substring(0, newlineIndex) : response;
    }




}
