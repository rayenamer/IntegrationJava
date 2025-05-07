package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import services.UserService;

import java.io.IOException;
import java.util.Optional;
import java.util.Stack;
import java.util.HashMap;
import java.util.Map;

public class StatistiqueController {

    private Stack<String> viewHistory = new Stack<>();
    private UserService userService = new UserService();

    // Couleurs des rôles
    private Map<String, Color> userTypeColors = new HashMap<>() {{
        put("Moderateur", Color.valueOf("#FF6384"));
        put("Freelancer", Color.valueOf("#36A2EB"));
        put("Chercheur", Color.valueOf("#FFCE56"));
        // couleurs optionnelles pour d'autres types éventuels
        put("Admin", Color.valueOf("#8A2BE2"));
        put("Enseignant", Color.valueOf("#20B2AA"));
        put("Etudiant", Color.valueOf("#FFA07A"));
    }};

    @FXML
    private PieChart userPieChart;

    @FXML
    private VBox legendContainer;

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Button btnRetour;

    @FXML
    private Button btnFermer;

    @FXML
    public void initialize() {
        loadUserStatistics();
        pushToHistory("/statistique.fxml");
    }

    private void loadUserStatistics() {
        try {
            Map<String, Integer> userData = userService.getUserStatistics();
            int totalUsers = userData.values().stream().mapToInt(Integer::intValue).sum();

            totalUsersLabel.setText("Total utilisateurs: " + totalUsers);

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            userData.forEach((type, count) -> {
                pieChartData.add(new PieChart.Data(type + " (" + count + ")", count));
            });
            userPieChart.setData(pieChartData);

            createLegend(userData, totalUsers);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger les statistiques: " + e.getMessage());
        }
    }

    private void createLegend(Map<String, Integer> distribution, int totalUsers) {
        legendContainer.getChildren().clear();
        legendContainer.setSpacing(10);

        distribution.forEach((type, count) -> {
            HBox legendItem = new HBox(10);
            legendItem.setAlignment(Pos.CENTER_LEFT);

            Rectangle colorRect = new Rectangle(20, 20);
            colorRect.setFill(userTypeColors.getOrDefault(type, Color.GRAY));
            colorRect.setStroke(Color.BLACK);

            Label typeLabel = new Label(String.format("%s: %d (%.1f%%)",
                    type,
                    count,
                    (count * 100.0 / totalUsers)));
            typeLabel.setStyle("-fx-font-weight: bold;");

            legendItem.getChildren().addAll(colorRect, typeLabel);
            legendContainer.getChildren().add(legendItem);
        });
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        if (viewHistory.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Navigation",
                    "Vous êtes déjà sur l'écran principal");
            return;
        }

        String previousView = viewHistory.pop();
        loadView(previousView, event);
    }

    @FXML
    private void handleFerme(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Fermeture");
        confirmation.setHeaderText("Voulez-vous vraiment quitter l'application ?");
        confirmation.setContentText("Toutes les modifications non sauvegardées seront perdues.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            stage.close();
        }
    }

    private void loadView(String fxmlPath, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger la vue: " + fxmlPath);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void pushToHistory(String fxmlPath) {
        viewHistory.push(fxmlPath);
    }
}
