
package controllers;

import entities.Candidature;
import entities.Offre;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import services.CandidatureService;
import services.OffreService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatistiquesController {

    @FXML
    private HBox chartContainer;

    private final CandidatureService candidatureService = new CandidatureService();
    private final OffreService offreService = new OffreService();

    private int score;
    private int total;

    public void setScore(int score, int total) {
        this.score = score;
        this.total = total;
        afficherStatistiques();
    }

    private void afficherStatistiques() {
        // Create the chart to display the number of people with score > 3 and <= 3
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Score");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of users");

        BarChart<String, Number> scoreChart = new BarChart<>(xAxis, yAxis);
        scoreChart.setTitle("Statistique par Score des quiz");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("User Scores");

        // Count the users based on the score condition (greater than 3 and less than or equal to 3)
        int scoreGreaterThan3 = 0;
        int scoreLessThanOrEqual3 = 0;

        // Retrieve all quiz results (assuming you have a service to fetch quiz results)
        List<Integer> userScores = getUserScores(); // Get the list of scores of all users

        for (int userScore : userScores) {
            if (userScore > 3) {
                scoreGreaterThan3++;
            } else {
                scoreLessThanOrEqual3++;
            }
        }

        // Add the data for the chart
        series.getData().add(new XYChart.Data<>("Score > 3", scoreGreaterThan3));
        series.getData().add(new XYChart.Data<>("Score <= 3", scoreLessThanOrEqual3));

        scoreChart.getData().add(series);

        // Dynamically add the score chart
        chartContainer.getChildren().add(scoreChart);
    }

    @FXML
    public void initialize() {
        chartContainer.setAlignment(Pos.CENTER);
        chartContainer.setSpacing(20);

        // Add the other charts (Candidature status and Offre contract type)
        PieChart pieChart = createCandidatureStatutPieChart();
        BarChart<String, Number> barChart = createOffreContratBarChart();

        // Add the score statistics chart
        afficherStatistiques(); // Add the score chart

        // Add all charts to the container
        chartContainer.getChildren().addAll(pieChart, barChart);
    }

    private List<Integer> getUserScores() {
        // This should be a method that retrieves the scores of users who completed the quiz.
        // For now, we'll return a dummy list of scores.
        return List.of(5, 2, 4, 3, 6, 1, 4, 5, 2); // Replace with actual database call
    }

    private PieChart createCandidatureStatutPieChart() {
        int enAttente = 0, acceptee = 0, refusee = 0;

        for (Candidature c : candidatureService.recupstat()) {
            switch (c.getStatut()) {
                case EN_ATTENTE -> enAttente++;
                case ACCEPTEE -> acceptee++;
                case REFUSEE -> refusee++;
            }
        }

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
                new PieChart.Data("En attente", enAttente),
                new PieChart.Data("Acceptées", acceptee),
                new PieChart.Data("Refusées", refusee)
        );

        PieChart pieChart = new PieChart(data);
        pieChart.setTitle("Répartition des candidatures");

        return pieChart;
    }

    private BarChart<String, Number> createOffreContratBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Type de contrat");
        yAxis.setLabel("Nombre d'offres");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Offres par type de contrat");

        Map<String, Integer> typeContratCounts = new HashMap<>();
        for (Offre offre : offreService.recupstat()) {
            String typeNom = offre.getTypeContrat().getNom();
            typeContratCounts.put(typeNom, typeContratCounts.getOrDefault(typeNom, 0) + 1);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Contrats");

        for (Map.Entry<String, Integer> entry : typeContratCounts.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);
        return barChart;
    }
}
