// src/main/java/services/ChercheurService.java
package services;

import entities.Chercheur;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChercheurService {
    private Connection connection;

    public ChercheurService() {
        this.connection = MyDatabase.getInstance().getCnx();
    }

    public boolean ajouterChercheur(Chercheur chercheur) {
        String sqlUser = "INSERT INTO user (email, password, nom, prenom, domaine, sexe, tel, type, roles) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlChercheur = "INSERT INTO chercheur (photo, cv, user_id) VALUES (?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement psUser = connection.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, chercheur.getEmail());
                psUser.setString(2, chercheur.getPassword());
                psUser.setString(3, chercheur.getNom());
                psUser.setString(4, chercheur.getPrenom());
                psUser.setString(5, chercheur.getDomaine());
                psUser.setString(6, chercheur.getSexe());
                psUser.setString(7, chercheur.getTel());
                psUser.setString(8, "chercheur");
                psUser.setString(9, chercheur.getRoles());

                int affectedRows = psUser.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Échec de l'insertion dans `user`.");
                }

                try (ResultSet generatedKeys = psUser.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        chercheur.setId(userId);

                        try (PreparedStatement psChercheur = connection.prepareStatement(sqlChercheur)) {
                            psChercheur.setString(1, chercheur.getPhoto());
                            psChercheur.setString(2, chercheur.getCv());
                            psChercheur.setInt(3, userId);
                            psChercheur.executeUpdate();
                        }
                    } else {
                        throw new SQLException("Échec de récupération de l'ID.");
                    }
                }

                connection.commit();
                System.out.println("✅ Chercheur ajouté avec succès (ID: " + chercheur.getId() + ")");
                return true;
            }

        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { /* ignore */ }
            System.err.println("❌ Erreur ajout chercheur : " + e.getMessage());
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { /* ignore */ }
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

        String sqlChercheur = "UPDATE chercheur SET photo = ?, cv = ? WHERE user_id = ?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(sqlUser.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                ps.executeUpdate();
            }

            try (PreparedStatement ps2 = connection.prepareStatement(sqlChercheur)) {
                ps2.setString(1, chercheur.getPhoto());
                ps2.setString(2, chercheur.getCv());
                ps2.setInt(3, chercheur.getId());
                ps2.executeUpdate();
            }

            connection.commit();
            System.out.println("✅ Chercheur modifié (ID: " + chercheur.getId() + ")");
            return true;

        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { /* ignore */ }
            System.err.println("❌ Erreur modifierChercheur : " + e.getMessage());
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException ex) { /* ignore */ }
        }
    }

    public List<Chercheur> afficherChercheurByEmail(String email) {
        List<Chercheur> chercheurs = new ArrayList<>();
        String sql = "SELECT u.id, u.email, u.password, u.nom, u.prenom, u.domaine, u.sexe, u.tel, u.roles, " +
                "c.photo, c.cv " +
                "FROM user u JOIN chercheur c ON u.id = c.user_id WHERE u.email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Chercheur c = new Chercheur();
                    c.setId(rs.getInt("id"));
                    c.setEmail(rs.getString("email"));
                    c.setPassword(rs.getString("password"));
                    c.setNom(rs.getString("nom"));
                    c.setPrenom(rs.getString("prenom"));
                    c.setDomaine(rs.getString("domaine"));
                    c.setSexe(rs.getString("sexe"));
                    c.setTel(rs.getString("tel"));
                    c.setRoles(rs.getString("roles"));
                    c.setPhoto(rs.getString("photo"));
                    c.setCv(rs.getString("cv"));
                    chercheurs.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération chercheur par email : " + e.getMessage());
        }

        return chercheurs;
    }
}
