package services;

import entities.Moderateur;
import utils.MyDatabase;
import java.sql.*;

public class ModerateurService {
    private final MyDatabase database;

    public ModerateurService() {
        this.database = MyDatabase.getInstance();
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (Connection conn = database.getCnx();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification de l'email: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la vérification de l'email", e);
        }
        return false;
    }

    public boolean ajouterModerateur(Moderateur moderateur) {
        // Vérifier si l'email existe déjà
        if (emailExists(moderateur.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà.");
        }

        String sqlUser = "INSERT INTO user (email, password, nom, prenom, domaine, sexe, tel, type, roles) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlModerateur = "INSERT INTO moderateur (id, societe) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = database.getCnx();
            conn.setAutoCommit(false);

            // Étape 1: Insérer dans `user`
            try (PreparedStatement psUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, moderateur.getEmail());
                psUser.setString(2, moderateur.getPassword());
                psUser.setString(3, moderateur.getNom());
                psUser.setString(4, moderateur.getPrenom());
                psUser.setString(5, moderateur.getDomaine());
                psUser.setString(6, moderateur.getSexe());
                psUser.setString(7, moderateur.getTel());
                psUser.setString(8, "moderateur");
                psUser.setString(9, moderateur.getRoles());

                int affectedRows = psUser.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Échec de l'insertion dans `user`, aucune ligne affectée.");
                }

                // Récupérer l'ID généré
                try (ResultSet generatedKeys = psUser.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);

                        // Étape 2: Insérer dans `moderateur`
                        try (PreparedStatement psModerateur = conn.prepareStatement(sqlModerateur)) {
                            psModerateur.setInt(1, userId);
                            psModerateur.setString(2, moderateur.getSociete());

                            int affectedRowsModerateur = psModerateur.executeUpdate();
                            if (affectedRowsModerateur == 0) {
                                throw new SQLException("Échec de l'insertion dans `moderateur`, aucune ligne affectée.");
                            }
                        }
                    } else {
                        throw new SQLException("Échec de l'insertion dans `user`, aucun ID généré.");
                    }
                }
            }

            conn.commit();
            System.out.println("✅ Modérateur ajouté avec succès");
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("❌ Transaction annulée");
                } catch (SQLException ex) {
                    System.err.println("❌ Erreur lors de l'annulation de la transaction: " + ex.getMessage());
                }
            }
            System.err.println("❌ Erreur lors de l'ajout du modérateur: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'ajout du modérateur", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("❌ Erreur lors de la réactivation de l'auto-commit: " + e.getMessage());
                }
            }
        }
    }

    // Méthodes supplémentaires (récupération, suppression, etc.) peuvent être ajoutées ici
}