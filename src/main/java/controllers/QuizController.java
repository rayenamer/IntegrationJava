package controllers;

import entities.Question;
import entities.QuizResult;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import services.QuizResultService;

import java.util.ArrayList;
import java.util.List;

public class QuizController {

    @FXML private VBox questionsContainer;
    @FXML private Text timerText;
    @FXML private Button submitButton;
    @FXML private ProgressBar progressBar;
    @FXML private Text scoreText;
    @FXML private ComboBox<String> difficultyComboBox;

    private List<Question> questions;
    private List<ToggleGroup> answerGroups;
    private List<Boolean> questionResults;
    private int score = 0;
    private int timeLeft = 30;
    private Timeline timeline;
    private int currentQuestionIndex = 0;

    private QuizResultService quizResultService = new QuizResultService();

    @FXML
    public void initialize() {
        questions = new ArrayList<>();
        answerGroups = new ArrayList<>();
        questionResults = new ArrayList<>();

        // Initialize difficulty levels
        difficultyComboBox.getItems().addAll("Facile", "Moyen", "Difficile");
        difficultyComboBox.setValue("Moyen");

        // Initialize progress bar
        progressBar.setProgress(0);
    }

    @FXML private Button startQuizButton;

    @FXML
    private void startQuiz() {
        questions = loadQuestions();
        questionsContainer.setVisible(true);
        submitButton.setDisable(false);
        startQuizButton.setVisible(false);
        currentQuestionIndex = 0;
        score = 0;
        timeLeft = 30;
        questionResults.clear();
        answerGroups.clear();
        questionsContainer.getChildren().clear();

        // Start timer
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        displayQuestion(currentQuestionIndex);
    }

    private void displayQuestion(int index) {
        if (index >= questions.size()) {
            submitQuiz();
            return;
        }

        Question question = questions.get(index);
        VBox questionBox = new VBox(10);
        questionBox.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-background-radius: 10;");

        Text questionText = new Text((index + 1) + ". " + question.getQuestion());
        questionText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        questionBox.getChildren().add(questionText);

        ToggleGroup group = new ToggleGroup();
        answerGroups.add(group);

        for (String option : question.getOptions()) {
            RadioButton rb = new RadioButton(option);
            rb.setToggleGroup(group);
            rb.setStyle("-fx-font-size: 16px; -fx-padding: 5;");
            rb.setOnAction(e -> checkAnswer(index));
            questionBox.getChildren().add(rb);
        }

        questionsContainer.getChildren().clear();
        questionsContainer.getChildren().add(questionBox);

        // Update progress
        progressBar.setProgress((double) (index + 1) / questions.size());
        scoreText.setText("Score: " + score + "/" + questions.size());
    }

    private void checkAnswer(int questionIndex) {
        ToggleGroup group = answerGroups.get(questionIndex);
        Question question = questions.get(questionIndex);

        RadioButton selected = (RadioButton) group.getSelectedToggle();
        if (selected != null) {
            boolean isCorrect = selected.getText().equals(question.getOptions()[question.getCorrectAnswerIndex()]);
            questionResults.add(isCorrect);
            if (isCorrect) score++;

            // Show immediate feedback
            Alert feedback = new Alert(Alert.AlertType.INFORMATION);
            feedback.setTitle("Résultat");
            feedback.setHeaderText(isCorrect ? "Correct! 🎉" : "Incorrect 😕");
            feedback.setContentText(isCorrect ? "Bonne réponse!" : "La bonne réponse était: " + question.getOptions()[question.getCorrectAnswerIndex()]);
            feedback.showAndWait();

            // Move to next question after a short delay
            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                currentQuestionIndex++;
                displayQuestion(currentQuestionIndex);
            }));
            delay.play();
        }
    }

    private void updateTimer() {
        if (timeLeft > 0) {
            timeLeft--;
            timerText.setText("⏰ Temps restant: " + timeLeft);
        } else {
            timeline.stop();
            submitQuiz();
        }
    }

    @FXML
    private void submitQuiz() {
        if (timeline != null) timeline.stop();

        // Calculate final score
        score = 0;
        for (Boolean result : questionResults) {
            if (result) score++;
        }

        // Show detailed results
        StringBuilder resultMessage = new StringBuilder();
        resultMessage.append("Résumé du Quiz:\n\n");
        resultMessage.append("Score final: ").append(score).append("/").append(questions.size()).append("\n\n");
        resultMessage.append("Détails des réponses:\n");

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            resultMessage.append("\nQuestion ").append(i + 1).append(": ")
                    .append(questionResults.get(i) ? "✅" : "❌")
                    .append(" - ").append(q.getQuestion())
                    .append("\nRéponse correcte: ").append(q.getOptions()[q.getCorrectAnswerIndex()]);
        }

        Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
        resultAlert.setTitle("Résultats du Quiz");
        resultAlert.setHeaderText("Quiz terminé!");
        resultAlert.setContentText(resultMessage.toString());

        ButtonType retryButton = new ButtonType("Réessayer");
        ButtonType closeButton = new ButtonType("Fermer");
        resultAlert.getButtonTypes().setAll(retryButton, closeButton);

        resultAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == retryButton) {
                resetQuiz();
            }
        });

        // Save result
        QuizResult quizResult = new QuizResult(score, questions.size());
        quizResultService.saveQuizResult(quizResult);
    }

    private void resetQuiz() {
        questionsContainer.setVisible(false);
        submitButton.setDisable(true);
        startQuizButton.setVisible(true);
        progressBar.setProgress(0);
        scoreText.setText("Score: 0/0");
        currentQuestionIndex = 0;
        score = 0;
        timeLeft = 30;
        questionResults.clear();
        answerGroups.clear();
        questionsContainer.getChildren().clear();
    }

    private List<Question> loadQuestions() {
        List<Question> questionList = new ArrayList<>();
        String difficulty = difficultyComboBox.getValue();

        // Career-focused questions
        questionList.add(new Question("Quelle est la première étape pour rédiger un CV efficace ?",
            new String[]{"Liste de toutes vos expériences", "Analyse de l'offre d'emploi", "Choix de la police", "Ajout de photos"}, 1));

        questionList.add(new Question("Quelle est la durée idéale d'une lettre de motivation ?",
            new String[]{"1 page", "2 pages", "3 pages", "4 pages"}, 0));

        questionList.add(new Question("Quelle est la meilleure façon de se préparer pour un entretien d'embauche ?",
            new String[]{"Mémoriser des réponses", "Rechercher l'entreprise", "Porter une tenue formelle", "Tout ce qui précède"}, 3));

        questionList.add(new Question("Quelle est la règle d'or pour le networking professionnel ?",
            new String[]{"Donner avant de recevoir", "Collecter le plus de cartes", "Parler de soi", "Éviter les événements"}, 0));

        questionList.add(new Question("Quelle est la meilleure façon de négocier un salaire ?",
            new String[]{"Demander directement", "Présenter des arguments basés sur le marché", "Menacer de partir", "Accepter la première offre"}, 1));

        return questionList;
    }
}
