package wiemwebsite.demo.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import wiemwebsite.demo.models.Missionfreelencer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class MissionfreelencerService {

    private static final Logger LOGGER = Logger.getLogger(MissionfreelencerService.class.getName());

    private static final String URL = "jdbc:mysql://localhost:3306/careera?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public void ajouterMission(Missionfreelencer mission) throws SQLException {
        String sql = "INSERT INTO missionfreelencer (titre, prix, description, datelimite, specialite, date, userid, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, mission.getTitre());
            stmt.setDouble(2, mission.getPrix());
            stmt.setString(3, mission.getDescription());
            stmt.setDate(4, mission.getDatelimite());
            stmt.setString(5, mission.getSpecialite());
            stmt.setDate(6, mission.getDate());
            stmt.setInt(7, mission.getUserid());
            stmt.setString(8, mission.getImage_url());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("L'insertion a échoué, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    mission.setId(generatedKeys.getInt(1));
                }
            }

            LOGGER.info("Mission ajoutée : " + mission);
        }
    }

    public ObservableList<Missionfreelencer> searchMissionsByTitleAndPrice(String titleFilter, Double minPrice, Double maxPrice) throws SQLException {
        ObservableList<Missionfreelencer> missions = FXCollections.observableArrayList();
        StringBuilder query = new StringBuilder("SELECT id, titre, prix, description, datelimite, specialite, date, userid, image_url FROM missionfreelencer WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (titleFilter != null && !titleFilter.trim().isEmpty()) {
            query.append(" AND titre LIKE ?");
            params.add("%" + titleFilter.trim() + "%");
        }
        if (minPrice != null) {
            query.append(" AND prix >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            query.append(" AND prix <= ?");
            params.add(maxPrice);
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    missions.add(new Missionfreelencer(
                            rs.getInt("id"),
                            rs.getString("titre"),
                            rs.getDouble("prix"),
                            rs.getString("description"),
                            rs.getDate("datelimite"),
                            rs.getString("specialite"),
                            rs.getDate("date"),
                            rs.getInt("userid"),
                            rs.getString("image_url")
                    ));
                }
            }
            LOGGER.info("Missions trouvées avec filtres (titre=" + titleFilter +
                    ", minPrice=" + minPrice + ", maxPrice=" + maxPrice + ") : " + missions.size());
        }
        return missions;
    }

    public void updateMission(Missionfreelencer mission) throws SQLException {
        String sql = "UPDATE missionfreelencer SET titre = ?, prix = ?, description = ?, datelimite = ?, specialite = ?, date = ?, userid = ?, image_url = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mission.getTitre());
            stmt.setDouble(2, mission.getPrix());
            stmt.setString(3, mission.getDescription());
            stmt.setDate(4, mission.getDatelimite());
            stmt.setString(5, mission.getSpecialite());
            stmt.setDate(6, mission.getDate());
            stmt.setInt(7, mission.getUserid());
            stmt.setString(8, mission.getImage_url());
            stmt.setInt(9, mission.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("La mise à jour a échoué, aucune mission trouvée avec l'ID : " + mission.getId());
            }

            LOGGER.info("Mission mise à jour : " + mission);
        }
    }

    public ObservableList<Missionfreelencer> getAllMissions() throws SQLException {
        List<Missionfreelencer> missions = new ArrayList<>();
        String sql = "SELECT * FROM missionfreelencer";
        LOGGER.info("Tentative de récupération des missions...");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            LOGGER.info("Connexion à la base de données réussie.");
            int count = 0;

            while (rs.next()) {
                try {
                    Date datelimite = null;
                    Date date = null;

                    try {
                        datelimite = rs.getDate("datelimite");
                    } catch (SQLException e) {
                        LOGGER.warning("Erreur lors de la lecture de datelimite pour l'ID " + rs.getInt("id") + " : " + e.getMessage());
                    }

                    try {
                        date = rs.getDate("date");
                    } catch (SQLException e) {
                        LOGGER.warning("Erreur lors de la lecture de date pour l'ID " + rs.getInt("id") + " : " + e.getMessage());
                    }

                    double prix;
                    try {
                        prix = rs.getDouble("prix");
                    } catch (SQLException | NumberFormatException e) {
                        LOGGER.warning("Erreur de conversion pour le champ 'prix' de l'ID " + rs.getInt("id") + " : " + e.getMessage());
                        continue; // Ignore la ligne invalide
                    }

                    Missionfreelencer mission = new Missionfreelencer(
                            rs.getInt("id"),
                            rs.getString("titre"),
                            prix,
                            rs.getString("description"),
                            datelimite,
                            rs.getString("specialite"),
                            date,
                            rs.getInt("userid"),
                            rs.getString("image_url")
                    );

                    missions.add(mission);
                    count++;
                    LOGGER.info("Mission ajoutée à la liste : " + mission.getTitre() + " (ID: " + mission.getId() + ")");

                } catch (SQLException e) {
                    LOGGER.warning("Erreur lors de la lecture d'une mission (ID: " + rs.getInt("id") + "), ligne ignorée : " + e.getMessage());
                }
            }

            LOGGER.info("Total des missions récupérées : " + count);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des missions : " + e.getMessage(), e);
            throw e;
        }

        return FXCollections.observableArrayList(missions);
    }

    public void supprimerMission(int id) throws SQLException {
        String sql = "DELETE FROM missionfreelencer WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Mission supprimée avec succès ! ID: " + id);
            } else {
                LOGGER.info("Aucune mission trouvée avec l'ID: " + id);
            }
        }
    }

    public void testConnection() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            LOGGER.info("Connexion réussie à la base de données careera !");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Échec de la connexion : " + e.getMessage(), e);
        }
    }
}