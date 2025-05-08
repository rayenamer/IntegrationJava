package test;

import controllers.EvenementController;
import controllers.EventListController;
import controllers.TypeEventController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Event Management System");

        // Button to manage events
        Button eventButton = new Button("Manage Events");
        eventButton.setOnAction(e -> new EvenementController().show(primaryStage));

        // Button to manage event types
        Button typeButton = new Button("Manage Event Types");
        typeButton.setOnAction(e -> new TypeEventController().show(primaryStage));

        // Button to view event list
        Button eventListButton = new Button("View Event List");
        eventListButton.setOnAction(e -> new EventListController().show(primaryStage));

        // Layout
        VBox layout = new VBox(20, eventButton, typeButton, eventListButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}