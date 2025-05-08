package services;

import entities.TypeEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeEventService {
    public List<TypeEvent> findAll() {
        List<TypeEvent> types = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM type_event")) {
            while (rs.next()) {
                types.add(new TypeEvent(rs.getInt("id"), rs.getString("nom")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching event types: " + e.getMessage());
        }
        return types;
    }

    public void save(TypeEvent type) {
        String sql = type.getId() == 0 ?
                "INSERT INTO type_event (nom) VALUES (?)" :
                "UPDATE type_event SET nom=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type.getNom());
            if (type.getId() != 0) {
                pstmt.setInt(2, type.getId());
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving event type: " + e.getMessage());
        }
    }

    public void delete(int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM type_event WHERE id=?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting event type: " + e.getMessage());
        }
    }
}