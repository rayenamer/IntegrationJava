package controllers;

import entities.Personne;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import services.PersonneService;

import java.io.IOException;
import java.sql.SQLException;

public class AjouterPersonneController {
    @javafx.fxml.FXML
    private TextField ageTF;
    @javafx.fxml.FXML
    private TextField nomTF;
    @javafx.fxml.FXML
    private TextField prenomTF;

    @javafx.fxml.FXML
    public void afficherPersonnes(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherPersonnes.fxml"));
        try {
            Parent root = loader.load();
            ageTF.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    @javafx.fxml.FXML
    public void ajouterPersonne(ActionEvent actionEvent) {
        PersonneService ps = new PersonneService();
        String nom = nomTF.getText();
        String prenom = prenomTF.getText();
        int age = Integer.parseInt(ageTF.getText());
        Personne p = new Personne(age, nom, prenom);
        try {
            ps.ajouter(p);
        } catch (SQLException e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Erreur");
            a.setContentText(e.getMessage());
            a.show();
        }
    }
}
