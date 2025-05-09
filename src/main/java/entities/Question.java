package entities;

public class Question {
    private String question;
    private String[] options;
    private int correctAnswerIndex;
    private int score;

    public Question(String question, String[] options, int correctAnswerIndex) {
        this.question = question;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
    public int getScore() {
        return score;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
}
