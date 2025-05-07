package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // 🆕 Charge la page de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/index.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Connexion à Careera");
            primaryStage.show();

        } catch (Exception e) {
            System.out.println("Erreur de chargement FXML : " + e.getMessage());
            e.printStackTrace();
        }

    }

}
