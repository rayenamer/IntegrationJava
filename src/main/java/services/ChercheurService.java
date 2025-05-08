// src/main/java/services/ChercheurService.java
package services;

import entities.Chercheur;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChercheurService {
    private final MyDatabase database;

    public ChercheurService() {
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

    public boolean ajouterChercheur(Chercheur chercheur) {
        String sqlUser = "INSERT INTO user (email, password, nom, prenom, domaine, sexe, tel, type, roles) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlChercheur = "INSERT INTO chercheur (id_user, photo, cv, specialite) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = database.getCnx();
            conn.setAutoCommit(false);

            // Vérifier si l'email existe déjà
            if (emailExists(chercheur.getEmail())) {
                throw new SQLException("Un utilisateur avec cet email existe déjà");
            }

            // Étape 1: Insérer dans `user`
            try (PreparedStatement psUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, chercheur.getEmail());
                psUser.setString(2, chercheur.getPassword());
                psUser.setString(3, chercheur.getNom());
                psUser.setString(4, chercheur.getPrenom());
                psUser.setString(5, chercheur.getDomaine());
                psUser.setString(6, chercheur.getSexe());
                psUser.setString(7, chercheur.getTel());
                psUser.setString(8, "chercheur");
                psUser.setString(9, "ROLE_CHERCHEUR");

                int affectedRows = psUser.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Échec de l'insertion dans `user`, aucune ligne affectée.");
                }

                // Récupérer l'ID généré
                try (ResultSet generatedKeys = psUser.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);

                        // Étape 2: Insérer dans `chercheur`
                        try (PreparedStatement psChercheur = conn.prepareStatement(sqlChercheur)) {
                            psChercheur.setInt(1, userId);
                            psChercheur.setString(2, chercheur.getPhoto());
                            psChercheur.setString(3, chercheur.getCv());
                            psChercheur.setString(4, chercheur.getSpecialite());

                            int affectedRowsChercheur = psChercheur.executeUpdate();
                            if (affectedRowsChercheur == 0) {
                                throw new SQLException("Échec de l'insertion dans `chercheur`, aucune ligne affectée.");
                            }
                        }
                    } else {
                        throw new SQLException("Échec de l'insertion dans `user`, aucun ID généré.");
                    }
                }
            }

            conn.commit();
            System.out.println("✅ Chercheur ajouté avec succès");
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("❌ Erreur lors du rollback : " + ex.getMessage());
                }
            }
            System.err.println("❌ Erreur lors de l'ajout du chercheur : " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'ajout du chercheur", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("❌ Erreur lors de la réactivation de l'auto-commit : " + e.getMessage());
                }
            }
        }
    }

    public boolean modifierChercheur(Chercheur chercheur) {
        StringBuilder sqlUser = new StringBuilder("UPDATE user SET email = ?");
        List<Object> params = new ArrayList<>();
        params.add(chercheur.getEmail());

        if (chercheur.getPassword() != null && !chercheur.getPassword().isEmpty()) {
            sqlUser.append(", password = ?");
            params.add(chercheur.getPassword());
        }
        sqlUser.append(", nom = ?, prenom = ?, domaine = ?, sexe = ?, tel = ?, roles = ? WHERE id = ?");
        params.add(chercheur.getNom());
        params.add(chercheur.getPrenom());
        params.add(chercheur.getDomaine());
        params.add(chercheur.getSexe());
        params.add(chercheur.getTel());
        params.add(chercheur.getRoles());
        params.add(chercheur.getId());

        String sqlChercheur = "UPDATE chercheur SET photo = ?, cv = ?, specialite = ? WHERE id_user = ?";

        Connection conn = null;
        try {
            conn = database.getCnx();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlUser.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                ps.executeUpdate();
            }

            try (PreparedStatement ps2 = conn.prepareStatement(sqlChercheur)) {
                ps2.setString(1, chercheur.getPhoto());
                ps2.setString(2, chercheur.getCv());
                ps2.setString(3, chercheur.getSpecialite());
                ps2.setInt(4, chercheur.getId());
                ps2.executeUpdate();
            }

            conn.commit();
            System.out.println("✅ Chercheur modifié (ID: " + chercheur.getId() + ")");
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("❌ Erreur lors du rollback : " + ex.getMessage());
                }
            }
            System.err.println("❌ Erreur modifierChercheur : " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("❌ Erreur lors de la réactivation de l'auto-commit : " + e.getMessage());
                }
            }
        }
    }

    public List<Chercheur> afficherChercheurByEmail(String email) {
        List<Chercheur> chercheurs = new ArrayList<>();
        String sql = "SELECT u.id, u.email, u.password, u.nom, u.prenom, u.domaine, u.sexe, u.tel, u.roles, " +
                "c.photo, c.cv, c.specialite " +
                "FROM user u JOIN chercheur c ON u.id = c.id_user WHERE u.email = ?";

        try (Connection conn = database.getCnx();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    chercheurs.add(mapRowToChercheur(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération chercheur par email : " + e.getMessage());
        }

        return chercheurs;
    }

    public Chercheur getChercheurById(int id) {
        String sql = "SELECT u.id, u.email, u.password, u.nom, u.prenom, u.domaine, u.sexe, u.tel, u.roles, " +
                "c.photo, c.cv, c.specialite " +
                "FROM user u JOIN chercheur c ON u.id = c.id_user WHERE u.id = ?";

        try (Connection conn = database.getCnx();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToChercheur(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération chercheur par ID : " + e.getMessage());
        }
        return null;
    }

    public boolean supprimerChercheur(int id) {
        String sqlChercheur = "DELETE FROM chercheur WHERE id_user = ?";
        String sqlUser = "DELETE FROM user WHERE id = ?";

        Connection conn = null;
        try {
            conn = database.getCnx();
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sqlChercheur)) {
                ps1.setInt(1, id);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = conn.prepareStatement(sqlUser)) {
                ps2.setInt(1, id);
                ps2.executeUpdate();
            }

            conn.commit();
            System.out.println("✅ Chercheur supprimé (ID: " + id + ")");
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("❌ Erreur lors du rollback : " + ex.getMessage());
                }
            }
            System.err.println("❌ Erreur suppression chercheur : " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("❌ Erreur lors de la réactivation de l'auto-commit : " + e.getMessage());
                }
            }
        }
    }

    private Chercheur mapRowToChercheur(ResultSet rs) throws SQLException {
        Chercheur chercheur = new Chercheur();
        chercheur.setId(rs.getInt("id"));
        chercheur.setEmail(rs.getString("email"));
        chercheur.setPassword(rs.getString("password"));
        chercheur.setNom(rs.getString("nom"));
        chercheur.setPrenom(rs.getString("prenom"));
        chercheur.setDomaine(rs.getString("domaine"));
        chercheur.setSexe(rs.getString("sexe"));
        chercheur.setTel(rs.getString("tel"));
        chercheur.setRoles(rs.getString("roles"));
        chercheur.setPhoto(rs.getString("photo"));
        chercheur.setCv(rs.getString("cv"));
        chercheur.setSpecialite(rs.getString("specialite"));
        return chercheur;
    }
}
