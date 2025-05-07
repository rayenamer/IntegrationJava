package services;

import entities.Question;
import java.util.ArrayList;
import java.util.List;

public class QuestionService {

    private List<Question> questionList;

    public QuestionService() {
        // Initialize with some dummy data for the sake of example
        questionList = new ArrayList<>();
        questionList.add(new Question("Quel est le capital de la France ?", new String[]{"Paris", "Londres", "Berlin", "Madrid"}, 0));
        questionList.add(new Question("Quelle est la couleur du ciel ?", new String[]{"Bleu", "Rouge", "Vert", "Jaune"}, 0));
        questionList.add(new Question("Quel est l'élément chimique dont le symbole est 'O' ?", new String[]{"Oxygène", "Or", "Ozone", "Osmium"}, 0));
        questionList.add(new Question("Combien de continents y a-t-il sur Terre ?", new String[]{"5", "6", "7", "8"}, 2));
        questionList.add(new Question("Quelle est la vitesse de la lumière ?", new String[]{"300 000 km/s", "150 000 km/s", "1 000 000 km/s", "500 000 km/s"}, 0));
    }

    // Add a new question
    public void addQuestion(Question question) {
        questionList.add(question);
    }

    // Retrieve all questions
    public List<Question> getAllQuestions() {
        return questionList;
    }

    // Get a specific question by index
    public Question getQuestion(int index) {
        if (index >= 0 && index < questionList.size()) {
            return questionList.get(index);
        } else {
            return null; // Return null if the index is out of bounds
        }
    }

    public int calculateTotalScore() {
        int score = 0;
        for (Question question : questionList) {
            score += question.getScore(); // Sum all question scores
        }
        return score;
    }

    // Remove a question by index
    public void removeQuestion(int index) {
        if (index >= 0 && index < questionList.size()) {
            questionList.remove(index);
        }
    }
}
