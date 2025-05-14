package services;

import entities.User;
import entities.Freelancer;
import entities.Moderateur;
import entities.Chercheur;
import utils.MyDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService implements Service<User> {
    private Connection cnx;

    public UserService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(User user) throws SQLException {
        ajouterUser(user);
    }

    public void ajouterUser(User user) {
        try {
            String sqlUser = "INSERT INTO user (email, password, nom, prenom, domaine, sexe, tel, type, roles) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            if (!isColumnExists("user", "roles")) {
                sqlUser = sqlUser.replace(", roles", "");
            }

            PreparedStatement pstUser = cnx.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            pstUser.setString(1, user.getEmail());
            pstUser.setString(2, user.getPassword());
            pstUser.setString(3, user.getNom());
            pstUser.setString(4, user.getPrenom());
            pstUser.setString(5, user.getDomaine());
            pstUser.setString(6, user.getSexe());
            pstUser.setString(7, user.getTel());
            pstUser.setString(8, user.getType());

            if (isColumnExists("user", "roles")) {
                pstUser.setString(9, user.getRoles());
            }

            pstUser.executeUpdate();

            ResultSet rs = pstUser.getGeneratedKeys();
            int userId = -1;
            if (rs.next()) {
                userId = rs.getInt(1);
            }

            user.setId(userId);

            switch (user.getType().toLowerCase()) {
                case "freelancer":
                    if (user instanceof Freelancer freelancer) {
                        String sqlFree = "INSERT INTO freelancer (id, photo, cv, adresse, annees_experience) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement pst = cnx.prepareStatement(sqlFree);
                        pst.setInt(1, userId);
                        pst.setString(2, freelancer.getPhoto());
                        pst.setString(3, freelancer.getCv());
                        pst.setString(4, freelancer.getAdresse());
                        pst.setInt(5, freelancer.getAnnees_Experience());
                        pst.executeUpdate();
                    }
                    break;

                case "moderateur":
                    if (user instanceof Moderateur moderateur) {
                        if (moderateur.getSociete() == null || moderateur.getSociete().isEmpty()) {
                            System.err.println("❌ Le champ 'societe' ne peut pas être vide pour un modérateur !");
                            return;
                        }

                        String sqlMod = "INSERT INTO moderateur (id, societe) VALUES (?, ?)";
                        PreparedStatement pst = cnx.prepareStatement(sqlMod);
                        pst.setInt(1, userId);
                        pst.setString(2, moderateur.getSociete());
                        pst.executeUpdate();
                    }
                    break;

                case "chercheur":
                    if (user instanceof Chercheur chercheur) {
                        String sqlChercheur = "INSERT INTO chercheur (id, photo, cv) VALUES (?, ?, ?)";
                        PreparedStatement pst = cnx.prepareStatement(sqlChercheur);
                        pst.setInt(1, userId);
                        pst.setString(2, chercheur.getPhoto());
                        pst.setString(3, chercheur.getCv());
                        pst.executeUpdate();
                    }
                    break;

                default:
                    System.err.println("❌ Type d'utilisateur inconnu !");
            }

            System.out.println("✅ Utilisateur de type " + user.getType() + " ajouté avec succès !");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    private boolean isColumnExists(String tableName, String columnName) {
        try {
            DatabaseMetaData metaData = cnx.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, tableName, columnName);
            return columns.next();
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification de la colonne : " + e.getMessage());
            return false;
        }
    }

    public boolean emailExists(String email) {
        try {
            String req = "SELECT * FROM user WHERE email = ?";
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void modifier(User user) throws SQLException {
        String sql = "UPDATE user SET email = ?, password = ?, nom = ?, prenom = ?, domaine = ?, sexe = ?, tel = ?, type = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, user.getEmail());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getNom());
        ps.setString(4, user.getPrenom());
        ps.setString(5, user.getDomaine());
        ps.setString(6, user.getSexe());
        ps.setString(7, user.getTel());
        ps.setString(8, user.getType());
        ps.setInt(9, user.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(User user) throws SQLException {
        String sql = "DELETE FROM user WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, user.getId());
        ps.executeUpdate();
    }

    @Override
    public List<User> recuperer() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            User user = new User(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("domaine"),
                    rs.getString("sexe"),
                    rs.getString("tel"),
                    rs.getString("type"),
                    rs.getString("roles")
            );
            users.add(user);
        }
        return users;
    }

    public boolean emailExiste(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification de l'email: " + e.getMessage());
        }
        return false;
    }

    public String getPasswordByEmail(String email) {
        String password = null;
        String req = "SELECT password FROM user WHERE email = ?";

        try {
            PreparedStatement pst = MyDatabase.getInstance().getCnx().prepareStatement(req);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                password = rs.getString("password");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return password;
    }

    // ✅ Méthode getUserStatistics()
    public Map<String, Integer> getUserStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT type, COUNT(*) as count FROM user GROUP BY type";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String type = rs.getString("type");
                int count = rs.getInt("count");
                stats.put(type, count);
            }

            // total général des utilisateurs
            String totalSql = "SELECT COUNT(*) as total FROM user";
            try (Statement totalStmt = cnx.createStatement();
                 ResultSet totalRs = totalStmt.executeQuery(totalSql)) {
                if (totalRs.next()) {
                    stats.put("total", totalRs.getInt("total"));
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des statistiques : " + e.getMessage());
        }

        return stats;
    }

    // Méthode pour récupérer l'utilisateur courant
    public User getCurrentUser() throws SQLException {
        // Pour l'instant, on récupère le premier utilisateur de la base
        // À adapter selon votre logique d'authentification
        String sql = "SELECT * FROM user LIMIT 1";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("domaine"),
                    rs.getString("sexe"),
                    rs.getString("tel"),
                    rs.getString("type"),
                    rs.getString("roles")
                );
            }
        }
        return null;
    }
}
