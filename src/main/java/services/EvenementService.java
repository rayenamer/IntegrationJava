package services;

import entities.Evenement;
import entities.TypeEvent;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EvenementService {
    private final MyDatabase database;
    public EvenementService() {
        this.database = MyDatabase.getInstance();
    }

    public List<Evenement> findAll() {
        List<Evenement> events = new ArrayList<>();
        String sql = "SELECT e.*, t.nom as type_nom, t.id as type_id FROM evenement e LEFT JOIN type_event t ON e.type_event_id = t.id";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                TypeEvent type = rs.getInt("type_id") != 0 ? new TypeEvent(rs.getInt("type_id"), rs.getString("type_nom")) : null;
                // Use setParticipantsIdsFromString to parse [1.2.3] format
                Evenement event = new Evenement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getString("lieu"),
                        rs.getBoolean("disponibilite"),
                        type,
                        rs.getString("image_url"),
                        rs.getInt("user_id") != 0 ? rs.getInt("user_id") : null,
                        null // Temporarily set participantsIds to null
                );
                // Parse [1.2.3] string from database
                event.setParticipantsIdsFromString(rs.getString("participants_ids"));
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching events: " + e.getMessage());
        }
        return events;
    }

    public void save(Evenement event) {
        String sql = event.getId() == 0 ?
                "INSERT INTO evenement (nom, description, date, lieu, disponibilite, type_event_id, image_url, user_id, participants_ids) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)" :
                "UPDATE evenement SET nom=?, description=?, date=?, lieu=?, disponibilite=?, type_event_id=?, image_url=?, user_id=?, participants_ids=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, event.getNom());
            pstmt.setString(2, event.getDescription());
            pstmt.setTimestamp(3, Timestamp.valueOf(event.getDate()));
            pstmt.setString(4, event.getLieu());
            pstmt.setBoolean(5, event.isDisponibilite());
            pstmt.setObject(6, event.getTypeEvent() != null ? event.getTypeEvent().getId() : null);
            pstmt.setString(7, event.getImageUrl());
            pstmt.setObject(8, event.getUserId());
            // Convert List<Integer> to [1.2.3] string using getParticipantsIdsAsString
            String participantsIdsStr = event.getParticipantsIdsAsString();
            pstmt.setString(9, participantsIdsStr);
            if (event.getId() != 0) {
                pstmt.setInt(10, event.getId());
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving event: " + e.getMessage());
        }
    }

    public void delete(int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM evenement WHERE id=?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting event: " + e.getMessage());
        }
    }
}