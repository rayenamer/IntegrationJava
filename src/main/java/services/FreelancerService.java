package services;

import entities.Freelancer;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FreelancerService {
    private Connection connection;

    public FreelancerService() {
        this.connection = MyDatabase.getInstance().getCnx();
    }

    // ➕ Ajouter un freelancer (insère dans `user` puis `freelancer`)
    public boolean ajouterFreelancer(Freelancer freelancer) {
        String sqlUser = "INSERT INTO user (email, password, nom, prenom, domaine, sexe, tel, type, roles) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlFreelancer = "INSERT INTO freelancer (user_id, photo, cv, adresse, annees_experience) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement psUser = connection.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, freelancer.getEmail());
                psUser.setString(2, freelancer.getPassword());
                psUser.setString(3, freelancer.getNom());
                psUser.setString(4, freelancer.getPrenom());
                psUser.setString(5, freelancer.getDomaine());
                psUser.setString(6, freelancer.getSexe());
                psUser.setString(7, freelancer.getTel());
                psUser.setString(8, "freelancer");
                psUser.setString(9, freelancer.getRoles());

                int affectedRows = psUser.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Échec de l'insertion dans `user`, aucune ligne affectée.");
                }

                try (ResultSet generatedKeys = psUser.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        freelancer.setId(userId);

                        try (PreparedStatement psFreelancer = connection.prepareStatement(sqlFreelancer)) {
                            psFreelancer.setInt(1, userId);
                            psFreelancer.setString(2, freelancer.getPhoto());
                            psFreelancer.setString(3, freelancer.getCv());
                            psFreelancer.setString(4, freelancer.getAdresse());
                            psFreelancer.setInt(5, freelancer.getAnnees_Experience());

                            psFreelancer.executeUpdate();
                            connection.commit();
                            System.out.println("✅ Freelancer ajouté (ID: " + userId + ")");
                            return true;
                        }
                    } else {
                        throw new SQLException("Échec de la récupération de l'ID utilisateur.");
                    }
                }
            }
        } catch (SQLException e) {
            try { connection.rollback(); }
            catch (SQLException ex) { System.err.println("❌ Rollback failed: " + ex.getMessage()); }
            System.err.println("❌ Erreur ajout freelancer: " + e.getMessage());
            return false;
        } finally {
            try { connection.setAutoCommit(true); }
            catch (SQLException e) { System.err.println("❌ Auto-commit failed: " + e.getMessage()); }
        }
    }

    // 🔎 Récupérer tous les freelancers

    /**
     * Récupère un unique Freelancer correspondant à l'email passé en paramètre.
     */
    // Affiche tous les freelancers en fonction de l'email
    public List<Freelancer> afficherFreelancersByEmail(String email) {
        List<Freelancer> freelancers = new ArrayList<>();
        String sql = "SELECT u.id, u.email, u.password, u.nom, u.prenom, u.domaine, u.sexe, u.tel, u.roles, "
                + "f.photo, f.cv, f.adresse, f.annees_experience "
                + "FROM user u JOIN freelancer f ON u.id = f.user_id WHERE u.email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Freelancer freelancer = new Freelancer();
                    freelancer.setId(rs.getInt("id"));
                    freelancer.setEmail(rs.getString("email"));
                    freelancer.setPassword(rs.getString("password"));
                    freelancer.setNom(rs.getString("nom"));
                    freelancer.setPrenom(rs.getString("prenom"));
                    freelancer.setDomaine(rs.getString("domaine"));
                    freelancer.setSexe(rs.getString("sexe"));
                    freelancer.setTel(rs.getString("tel"));
                    freelancer.setRoles(rs.getString("roles"));
                    freelancer.setPhoto(rs.getString("photo"));
                    freelancer.setCv(rs.getString("cv"));
                    freelancer.setAdresse(rs.getString("adresse"));
                    freelancer.setAnnees_Experience(rs.getInt("annees_experience"));
                    freelancers.add(freelancer);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des freelancers par email : " + e.getMessage());
        }
        return freelancers;
    }

    public Freelancer getFreelancerByEmail(String email) {
        Freelancer freelancer = null;
        String sql = "SELECT u.id, u.email, u.password, u.nom, u.prenom, u.domaine, u.sexe, u.tel, u.roles, "
                + "f.photo, f.cv, f.adresse, f.annees_experience "
                + "FROM user u JOIN freelancer f ON u.id = f.user_id WHERE u.email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    freelancer = new Freelancer();
                    freelancer.setId(rs.getInt("id"));
                    freelancer.setEmail(rs.getString("email"));
                    freelancer.setPassword(rs.getString("password"));
                    freelancer.setNom(rs.getString("nom"));
                    freelancer.setPrenom(rs.getString("prenom"));
                    freelancer.setDomaine(rs.getString("domaine"));
                    freelancer.setSexe(rs.getString("sexe"));
                    freelancer.setTel(rs.getString("tel"));
                    freelancer.setRoles(rs.getString("roles"));
                    freelancer.setPhoto(rs.getString("photo"));
                    freelancer.setCv(rs.getString("cv"));
                    freelancer.setAdresse(rs.getString("adresse"));
                    freelancer.setAnnees_Experience(rs.getInt("annees_experience"));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur getFreelancerByEmail: " + e.getMessage());
        }
        return freelancer;
    }

    // 🔎 Récupérer un freelancer par email
    public List<Freelancer> getFreelancersByEmail(String email) {
        List<Freelancer> list = new ArrayList<>();
        String sql = "SELECT u.id, u.email, u.password, u.nom, u.prenom, u.domaine, u.sexe, u.tel, u.roles, " +
                "f.photo, f.cv, f.adresse, f.annees_experience " +
                "FROM user u JOIN freelancer f ON u.id = f.user_id WHERE u.email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToFreelancer(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur getFreelancersByEmail: " + e.getMessage());
        }
        return list;
    }

    // 🔎 Récupérer un freelancer par ID
    public Freelancer getFreelancerById(int id) {
        String sql = "SELECT u.id, u.email, u.password, u.nom, u.prenom, u.domaine, u.sexe, u.tel, u.roles, " +
                "f.photo, f.cv, f.adresse, f.annees_experience " +
                "FROM user u JOIN freelancer f ON u.id = f.user_id WHERE u.id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToFreelancer(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur getFreelancerById: " + e.getMessage());
        }
        return null;
    }

    // ✏️ Mettre à jour un freelancer
    public boolean ModifierProfilController(Freelancer freelancer) {
        StringBuilder sqlUser = new StringBuilder("UPDATE user SET email = ?");
        List<Object> params = new ArrayList<>();
        params.add(freelancer.getEmail());

        if (freelancer.getPassword() != null && !freelancer.getPassword().isEmpty()) {
            sqlUser.append(", password = ?");
            params.add(freelancer.getPassword());
        }
        sqlUser.append(", nom = ?, prenom = ?, domaine = ?, sexe = ?, tel = ?, roles = ? WHERE id = ?");
        params.add(freelancer.getNom());
        params.add(freelancer.getPrenom());
        params.add(freelancer.getDomaine());
        params.add(freelancer.getSexe());
        params.add(freelancer.getTel());
        params.add(freelancer.getRoles());
        params.add(freelancer.getId());

        String sqlFreelancer = "UPDATE freelancer SET photo = ?, cv = ?, adresse = ?, annees_experience = ? WHERE user_id = ?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(sqlUser.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                ps.executeUpdate();
            }

            try (PreparedStatement ps2 = connection.prepareStatement(sqlFreelancer)) {
                ps2.setString(1, freelancer.getPhoto());
                ps2.setString(2, freelancer.getCv());
                ps2.setString(3, freelancer.getAdresse());
                ps2.setInt(4, freelancer.getAnnees_Experience());
                ps2.setInt(5, freelancer.getId());
                ps2.executeUpdate();
            }

            connection.commit();
            System.out.println("✅ Freelancer modifié (ID: " + freelancer.getId() + ")");
            return true;
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { /* ignore */ }
            System.err.println("❌ Erreur ModifierProfilController: " + e.getMessage());
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { /* ignore */ }
        }
    }

    // 🗑️ Supprimer un freelancer par ID (facultatif)
    public boolean supprimerFreelancer(int id) {
        String sql1 = "DELETE FROM freelancer WHERE user_id = ?";
        String sql2 = "DELETE FROM user WHERE id = ?";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement ps1 = connection.prepareStatement(sql1)) {
                ps1.setInt(1, id);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = connection.prepareStatement(sql2)) {
                ps2.setInt(1, id);
                ps2.executeUpdate();
            }
            connection.commit();
            return true;
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { /* ignore */ }
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException ex) { /* ignore */ }
        }
    }

    // 🔧 Méthode utilitaire pour mapper un ResultSet en Freelancer
    private Freelancer mapRowToFreelancer(ResultSet rs) throws SQLException {
        Freelancer f = new Freelancer();
        f.setId(rs.getInt("id"));
        f.setEmail(rs.getString("email"));
        f.setPassword(rs.getString("password"));
        f.setNom(rs.getString("nom"));
        f.setPrenom(rs.getString("prenom"));
        f.setDomaine(rs.getString("domaine"));
        f.setSexe(rs.getString("sexe"));
        f.setTel(rs.getString("tel"));
        f.setRoles(rs.getString("roles"));
        f.setPhoto(rs.getString("photo"));
        f.setCv(rs.getString("cv"));
        f.setAdresse(rs.getString("adresse"));
        f.setAnnees_Experience(rs.getInt("annees_experience"));
        return f;
    }
}
