package entities;


public class QuizResult {
    private int score;
    private int totalQuestions;


    public QuizResult(int score, int totalQuestions) {
        this.score = score;
        this.totalQuestions = totalQuestions;

    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

}
