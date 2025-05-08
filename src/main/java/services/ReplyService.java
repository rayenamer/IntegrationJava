package services;

import entities.Reply;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReplyService implements Service<Reply> {
    private Connection cnx;

    public ReplyService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Reply reply) throws SQLException {
        String sql = "INSERT INTO reply(content, created_at, user_id, discussion_id, user_name, user_photo) " +
                "VALUES(?, CURRENT_TIMESTAMP, ?, ?, ?, ?)";
        
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, reply.getContent());
        ps.setInt(2, reply.getUserId());
        ps.setInt(3, reply.getDiscussionId());
        ps.setString(4, reply.getUserName());
        ps.setString(5, reply.getUserPhoto());
        ps.executeUpdate();
    }

    @Override
    public void modifier(Reply reply) throws SQLException {
        String sql = "UPDATE reply SET content = ?, likes = ?, dislikes = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, reply.getContent());
        ps.setInt(2, reply.getLikes());
        ps.setInt(3, reply.getDislikes());
        ps.setInt(4, reply.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(Reply reply) throws SQLException {
        String sql = "DELETE FROM reply WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, reply.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Reply> recuperer() throws SQLException {
        List<Reply> replies = new ArrayList<>();
        String sql = "SELECT * FROM reply";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Reply r = new Reply();
            r.setId(rs.getInt("id"));
            r.setContent(rs.getString("content"));
            r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            r.setUserId(rs.getInt("user_id"));
            r.setDiscussionId(rs.getInt("discussion_id"));
            r.setLikes(rs.getInt("likes"));
            r.setDislikes(rs.getInt("dislikes"));
            r.setUserName(rs.getString("user_name"));
            r.setUserPhoto(rs.getString("user_photo"));
            replies.add(r);
        }

        return replies;
    }

    public List<Reply> getRepliesByDiscussion(int discussionId) throws SQLException {
        List<Reply> replies = new ArrayList<>();
        String sql = "SELECT * FROM reply WHERE discussion_id = ? ORDER BY created_at DESC";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, discussionId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Reply r = new Reply();
            r.setId(rs.getInt("id"));
            r.setContent(rs.getString("content"));
            r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            r.setUserId(rs.getInt("user_id"));
            r.setDiscussionId(rs.getInt("discussion_id"));
            r.setLikes(rs.getInt("likes"));
            r.setDislikes(rs.getInt("dislikes"));
            r.setUserName(rs.getString("user_name"));
            r.setUserPhoto(rs.getString("user_photo"));
            replies.add(r);
        }

        return replies;
    }

    // Get total number of replies
    public int getNumberOfReplies() {
        String sql = "SELECT COUNT(*) FROM reply";
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

    // Get the username with the most replies
    public String getTopReplierUsername() {
        String sql = "SELECT user_name, COUNT(*) AS total FROM reply GROUP BY user_name ORDER BY total DESC LIMIT 1";
        try (PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("user_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

} 