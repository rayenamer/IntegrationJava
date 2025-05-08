package entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Discussion {
    private Integer id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Integer userId;
    private int likes = 0;
    private int dislikes = 0;
    private String userName;
    private String userPhoto;
    private List<Reply> replies = new ArrayList<>();

    public Discussion() {}

    public Discussion(String title, String content, LocalDateTime createdAt, Integer userId, String userName, String userPhoto) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.userName = userName;
        this.userPhoto = userPhoto;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
    public int getDislikes() { return dislikes; }
    public void setDislikes(int dislikes) { this.dislikes = dislikes; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserPhoto() { return userPhoto; }
    public void setUserPhoto(String userPhoto) { this.userPhoto = userPhoto; }
    public List<Reply> getReplies() { return replies; }
    public void setReplies(List<Reply> replies) { this.replies = replies; }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", createdAt, userName, title);
    }
}
