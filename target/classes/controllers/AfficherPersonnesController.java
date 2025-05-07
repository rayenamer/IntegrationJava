package controllers;

import entities.Personne;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import services.PersonneService;

import java.sql.SQLException;
import java.util.List;

public class AfficherPersonnesController {
    @javafx.fxml.FXML
    private ListView<Personne> listView;

    ObservableList<Personne> obs;

    PersonneService ps = new PersonneService();
    @FXML
    void initialize(){
        try {
            List<Personne> personnes = ps.recuperer();
            obs = FXCollections.observableArrayList(personnes);
            listView.setItems(obs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void handleOffreClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterOffre.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une Offre");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void supprimerPersonne(ActionEvent actionEvent) {
        Personne p = listView.getSelectionModel().getSelectedItem();
        try {
            ps.supprimer(p);
            obs.remove(p);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
