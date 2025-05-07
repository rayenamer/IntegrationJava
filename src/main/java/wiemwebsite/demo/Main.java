package wiemwebsite.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import wiemwebsite.demo.controllers.MissionListController;

import java.sql.Connection;

public class Main extends Application {
    Connection conn = DatabaseConnection.getConnection();

    private MissionListController missionListController;
    @Override
    public void start(Stage primaryStage) throws Exception {
        String fxmlPath = "/wiemwebsite/demo/FXML/Missionfreelencer.fxml";
        java.net.URL fxmlUrl = getClass().getResource(fxmlPath);
        if (fxmlUrl == null) {
            throw new RuntimeException("Fichier FXML non trouvé : " + fxmlPath);
        }
        System.out.println("Chargement de : " + fxmlUrl);

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), 600, 600);
        primaryStage.setTitle("Ajouter une Mission");
        primaryStage.setScene(scene);
        primaryStage.show();
}}