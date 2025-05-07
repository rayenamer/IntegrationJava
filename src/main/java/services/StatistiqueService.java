package services;

import utils.MyDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class StatistiqueService {

    public Map<String, Integer> getUserDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        String query = "SELECT role, COUNT(*) as count FROM user GROUP BY role";

        try (Connection conn = MyDatabase.getInstance().getCnx();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String role = rs.getString("role");
                int count = rs.getInt("count");
                distribution.put(role, count);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return distribution;
    }
}
