package controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import services.ChatbotService;

public class ChatbotController {

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private final ChatbotService chatbotService;
    private boolean isBotTyping = false;

    public ChatbotController() {
        this.chatbotService = new ChatbotService();
    }

    @FXML
    private void initialize() {
        chatArea.setWrapText(true);
        chatArea.setEditable(false);

        chatArea.appendText("🤖 Assistant: Bonjour ! Je suis votre assistant virtuel spécialisé dans l'orientation professionnelle.\n" +
                "Je peux vous aider avec :\n" +
                "- La rédaction de CV et lettres de motivation\n" +
                "- La préparation aux entretiens d'embauche\n" +
                "- La négociation salariale\n" +
                "- Le développement de réseau professionnel\n" +
                "- L'orientation de carrière\n\n" +
                "Comment puis-je vous aider aujourd'hui ?\n\n");

        sendButton.setOnAction(e -> handleSendMessage());

        userInput.textProperty().addListener((obs, oldVal, newVal) ->
                sendButton.setDisable(newVal.trim().isEmpty())
        );

        userInput.setOnKeyPressed(this::handleKeyPress);
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
            event.consume();
            if (!userInput.getText().trim().isEmpty()) {
                handleSendMessage();
            }
        }
    }

    @FXML
    private void handleSendMessage() {
        if (isBotTyping) return;

        String userMessage = userInput.getText().trim();
        if (userMessage.isEmpty()) return;

        chatArea.appendText("👤 Vous: " + userMessage + "\n\n");
        userInput.clear();

        isBotTyping = true;
        sendButton.setDisable(true);
        userInput.setDisable(true);

        chatArea.appendText("🤖 Assistant: ");
        PauseTransition typingDelay = new PauseTransition(Duration.seconds(1));
        typingDelay.setOnFinished(e -> {
            try {
                String botResponse = chatbotService.getResponse(userMessage);
                chatArea.appendText(botResponse + "\n\n");
            } catch (Exception ex) {
                chatArea.appendText("Désolé, une erreur est survenue lors de la réponse.\n\n");
            }

            chatArea.setScrollTop(Double.MAX_VALUE);
            isBotTyping = false;
            sendButton.setDisable(false);
            userInput.setDisable(false);
            userInput.requestFocus();
        });
        typingDelay.play();
    }
}
