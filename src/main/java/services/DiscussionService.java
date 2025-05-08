package services;

import entities.Discussion;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscussionService implements Service<Discussion> {
    private Connection cnx;

    public DiscussionService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Discussion discussion) throws SQLException {
        String sql = "INSERT INTO discussion(title, content, createdAt, UserId, userName, userPhoto) " +
                "VALUES(?, ?, CURRENT_TIMESTAMP, ?, ?, ?)";

        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, discussion.getTitle());
        ps.setString(2, discussion.getContent());
        ps.setInt(3, discussion.getUserId());
        ps.setString(4, discussion.getUserName());
        ps.setString(5, discussion.getUserPhoto());
        ps.executeUpdate();
    }

    @Override
    public void modifier(Discussion discussion) throws SQLException {
        String sql = "UPDATE discussion SET title = ?, content = ?, likes = ?, dislikes = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, discussion.getTitle());
        ps.setString(2, discussion.getContent());
        ps.setInt(3, discussion.getLikes());
        ps.setInt(4, discussion.getDislikes());
        ps.setInt(5, discussion.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(Discussion discussion) throws SQLException {
        String sql = "DELETE FROM discussion WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, discussion.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Discussion> recuperer() throws SQLException {
        List<Discussion> discussions = new ArrayList<>();
        String sql = "SELECT * FROM discussion";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Discussion d = new Discussion();
            d.setId(rs.getInt("id"));
            d.setTitle(rs.getString("title"));
            d.setContent(rs.getString("content"));
            d.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
            d.setUserId(rs.getInt("UserId"));
            d.setLikes(rs.getInt("likes"));
            d.setDislikes(rs.getInt("dislikes"));
            d.setUserName(rs.getString("userName"));
            d.setUserPhoto(rs.getString("userPhoto"));
            discussions.add(d);
        }

        return discussions;
    }

    // Get total number of discussions
    public int getNumberOfDiscussions() {
        String sql = "SELECT COUNT(*) FROM discussion";
        try (PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get the username with the most discussions posted
    public String getTopContributorUsername() {
        String sql = "SELECT userName, COUNT(*) AS total FROM discussion GROUP BY userName ORDER BY total DESC LIMIT 1";
        try (PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("userName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

}
