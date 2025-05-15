package controllers;

import entities.Moderateur;
import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ModerateurService;
import utils.MyDatabase;
import utils.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModifierProfileModerateurController {
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTel;
    @FXML private TextField txtDomaine;
    @FXML private TextField txtSociete;
    @FXML private Button btnAnnuler;
    @FXML private Button btnEnregistrer;

    private Moderateur moderateur;

    @FXML
    public void initialize() {
        // Charger les données du modérateur depuis la base à partir de l'ID de la session
        User sessionUser = Session.getCurrentUser();
        if (sessionUser != null) {
            moderateur = fetchModerateurFromDB(sessionUser.getId());
        } else {
            moderateur = new Moderateur();
        }
        remplirChamps();
    }

    private Moderateur fetchModerateurFromDB(int id) {
        Moderateur m = new Moderateur();
        try {
            Connection cnx = MyDatabase.getInstance().getCnx();
            PreparedStatement ps = cnx.prepareStatement(
                "SELECT u.nom, u.prenom, u.email, u.tel, u.domaine, m.societe " +
                "FROM user u JOIN moderateur m ON u.id = m.id WHERE u.id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                m.setId(id);
                m.setNom(rs.getString("nom"));
                m.setPrenom(rs.getString("prenom"));
                m.setEmail(rs.getString("email"));
                m.setTel(rs.getString("tel"));
                m.setDomaine(rs.getString("domaine"));
                m.setSociete(rs.getString("societe"));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du modérateur : " + e.getMessage());
        }
        return m;
    }

    private void remplirChamps() {
        if (moderateur != null) {
            txtNom.setText(moderateur.getNom());
            txtPrenom.setText(moderateur.getPrenom());
            txtEmail.setText(moderateur.getEmail());
            txtTel.setText(moderateur.getTel());
            txtDomaine.setText(moderateur.getDomaine());
            txtSociete.setText(moderateur.getSociete());
        }
    }

    @FXML
    private void handleAnnuler() {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleEnregistrer() {
        if (!validateFields()) return;
        updateModerateur();
    }

    private void updateModerateur() {
        Connection connection = null;
        PreparedStatement stmtUser = null;
        PreparedStatement stmtModerateur = null;
        try {
            connection = MyDatabase.getInstance().getCnx();
            connection.setAutoCommit(false); // Démarrer une transaction

            // Mise à jour de la table user (sans mot de passe)
            String queryUser = "UPDATE user SET nom = ?, prenom = ?, email = ?, tel = ?, domaine = ? WHERE id = ?";

            stmtUser = connection.prepareStatement(queryUser);
            stmtUser.setString(1, txtNom.getText());
            stmtUser.setString(2, txtPrenom.getText());
            stmtUser.setString(3, txtEmail.getText());
            stmtUser.setString(4, txtTel.getText());
            stmtUser.setString(5, txtDomaine.getText());
            stmtUser.setInt(6, moderateur.getId());

            int rowsAffectedUser = stmtUser.executeUpdate();

            // Mise à jour de la table moderateur (uniquement societe)
            String queryModerateur = "UPDATE moderateur SET societe = ? WHERE id = ?";
            stmtModerateur = connection.prepareStatement(queryModerateur);
            stmtModerateur.setString(1, txtSociete.getText());
            stmtModerateur.setInt(2, moderateur.getId());

            int rowsAffectedModerateur = stmtModerateur.executeUpdate();

            // Valider la transaction
            connection.commit();

            if (rowsAffectedUser > 0 || rowsAffectedModerateur > 0) {
                // Mettre à jour l'objet Moderateur en session
                moderateur.setNom(txtNom.getText());
                moderateur.setPrenom(txtPrenom.getText());
                moderateur.setEmail(txtEmail.getText());
                moderateur.setTel(txtTel.getText());
                moderateur.setDomaine(txtDomaine.getText());
                moderateur.setSociete(txtSociete.getText());
                Session.setCurrentUser(moderateur);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Profil modifié avec succès.");
                closeWindow();
            } else {
                connection.rollback();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour le profil.");
            }
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            System.err.println("Erreur SQL lors de la mise à jour: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la mise à jour du profil: " + e.getMessage());
        }
        try {
            if (stmtUser != null) stmtUser.close();
            if (stmtModerateur != null) stmtModerateur.close();
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        if (txtNom == null || txtPrenom == null || txtEmail == null || txtTel == null || txtDomaine == null || txtSociete == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur interne", "Un ou plusieurs champs sont manquants dans le formulaire.");
            return false;
        }
        if (txtNom.getText() == null || txtNom.getText().trim().isEmpty() ||
            txtPrenom.getText() == null || txtPrenom.getText().trim().isEmpty() ||
            txtEmail.getText() == null || txtEmail.getText().trim().isEmpty() ||
            txtTel.getText() == null || txtTel.getText().trim().isEmpty() ||
            txtDomaine.getText() == null || txtDomaine.getText().trim().isEmpty() ||
            txtSociete.getText() == null || txtSociete.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs obligatoires", "Veuillez remplir tous les champs.");
            return false;
        }
        // Email simple check
        if (!txtEmail.getText().trim().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            showAlert(Alert.AlertType.ERROR, "Email invalide", "Veuillez saisir un email valide.");
            return false;
        }
        // Téléphone simple check
        if (!txtTel.getText().trim().matches("^[0-9]{8,}$")) {
            showAlert(Alert.AlertType.ERROR, "Téléphone invalide", "Veuillez saisir un numéro de téléphone valide (8 chiffres ou plus).");
            return false;
        }
        return true;
    }

    private void closeWindow() {
        Stage stage = (Stage) btnEnregistrer.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 