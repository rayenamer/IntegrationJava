package services;

import entities.Moderateur;
import utils.MyDatabase;
import java.sql.*;

public class ModerateurService {
    private Connection connection;

    public ModerateurService() {
        this.connection = MyDatabase.getInstance().getCnx();
    }

    public boolean ajouterModerateur(Moderateur moderateur) {
        String sqlUser = "INSERT INTO user (email, password, nom, prenom, domaine, sexe, tel, type, roles) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlModerateur = "INSERT INTO moderateur (user_id, societe) " +
                "VALUES (?, ?)";

        try {
            // Désactiver l'auto-commit pour gérer la transaction manuellement
            connection.setAutoCommit(false);

            // Étape 1: Insérer dans `user` et récupérer l'ID généré
            try (PreparedStatement psUser = connection.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, moderateur.getEmail());
                psUser.setString(2, moderateur.getPassword());
                psUser.setString(3, moderateur.getNom());
                psUser.setString(4, moderateur.getPrenom());
                psUser.setString(5, moderateur.getDomaine());
                psUser.setString(6, moderateur.getSexe());
                psUser.setString(7, moderateur.getTel());
                psUser.setString(8, "moderateur"); // Type forcé à "freelancer"
                psUser.setString(9, moderateur.getRoles());

                int affectedRows = psUser.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Échec de l'insertion dans `user`, aucune ligne affectée.");
                }

                // Récupérer l'ID généré
                try (ResultSet generatedKeys = psUser.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        moderateur.setId(userId); // Stocker l'ID pour une utilisation ultérieure

                        // Étape 2: Insérer dans `freelancer` avec la clé étrangère
                        try (PreparedStatement psModerateur = connection.prepareStatement(sqlModerateur)) {
                            psModerateur.setInt(1, userId); // Clé étrangère vers `user`
                            psModerateur.setString(2, moderateur.getSociete());


                            psModerateur.executeUpdate();
                            connection.commit(); // Valider la transaction
                            System.out.println("✅ moderateur  ajouté avec succès (ID utilisateur: " + userId + ")");
                            return true;
                        }
                    } else {
                        throw new SQLException("Échec de la récupération de l'ID utilisateur.");
                    }
                }
            }
        } catch (SQLException e) {
            try {
                connection.rollback(); // Annuler en cas d'erreur
            } catch (SQLException ex) {
                System.err.println("❌ Erreur lors du rollback: " + ex.getMessage());
            }
            System.err.println("❌ Erreur lors de l'ajout du moderateur: " + e.getMessage());
            return false;
        } finally {
            try {
                connection.setAutoCommit(true); // Rétablir l'auto-commit
            } catch (SQLException e) {
                System.err.println("❌ Erreur lors du rétablissement de l'auto-commit: " + e.getMessage());
            }
        }
    }

    // Méthodes supplémentaires (récupération, suppression, etc.) peuvent être ajoutées ici
}