package services;

import entities.QuizResult;

public class  QuizResultService {

    // This method should interact with the database (e.g., via JDBC or an ORM framework like Hibernate)
    public void saveQuizResult(QuizResult quizResult) {
        // Your database logic to save the result
        System.out.println("Saving quiz result: " + quizResult.getScore() + "/" + quizResult.getTotalQuestions());
        // Example: Using an ORM framework, save the quizResult to the database
    }
}

