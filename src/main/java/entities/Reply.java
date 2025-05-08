package entities;

import java.time.LocalDateTime;

public class Reply {
    private Integer id;
    private Integer discussion_id;
    private String content;
    private LocalDateTime created_at;
    private Integer user_id;
    private int likes = 0;
    private int dislikes = 0;
    private String user_name;
    private String user_photo;

    public Reply() {}

    public Reply(String content, LocalDateTime created_at, Integer user_id, Integer discussion_id, String user_name, String user_photo) {
        this.content = content;
        this.created_at = created_at;
        this.user_id = user_id;
        this.discussion_id = discussion_id;
        this.user_name = user_name;
        this.user_photo = user_photo;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getDiscussionId() { return discussion_id; }
    public void setDiscussionId(Integer discussion_id) { this.discussion_id = discussion_id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return created_at; }
    public void setCreatedAt(LocalDateTime created_at) { this.created_at = created_at; }
    public Integer getUserId() { return user_id; }
    public void setUserId(Integer user_id) { this.user_id = user_id; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
    public int getDislikes() { return dislikes; }
    public void setDislikes(int dislikes) { this.dislikes = dislikes; }
    public String getUserName() { return user_name; }
    public void setUserName(String user_name) { this.user_name = user_name; }
    public String getUserPhoto() { return user_photo; }
    public void setUserPhoto(String user_photo) { this.user_photo = user_photo; }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", created_at, user_name, content);
    }
} 