package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controllers.MissionListController;
import utils.DatabaseConnection;

import java.sql.Connection;

public class Main extends Application {
    Connection conn = DatabaseConnection.getConnection();

    private MissionListController missionListController;
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Missionfreelencer.fxml"));
        Scene scene = new Scene(loader.load(), 600, 600);
        primaryStage.setTitle("Ajouter une Mission");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}