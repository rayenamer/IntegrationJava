package entities;

import java.time.LocalDateTime;

public class Reply {
    private Integer id;
    private String content;
    private LocalDateTime createdAt;
    private Integer userId;
    private Integer discussionId;
    private int likes = 0;
    private int dislikes = 0;
    private String userName;
    private String userPhoto;

    public Reply() {}

    public Reply(String content, LocalDateTime createdAt, Integer userId, Integer discussionId, String userName, String userPhoto) {
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.discussionId = discussionId;
        this.userName = userName;
        this.userPhoto = userPhoto;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getDiscussionId() { return discussionId; }
    public void setDiscussionId(Integer discussionId) { this.discussionId = discussionId; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
    public int getDislikes() { return dislikes; }
    public void setDislikes(int dislikes) { this.dislikes = dislikes; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserPhoto() { return userPhoto; }
    public void setUserPhoto(String userPhoto) { this.userPhoto = userPhoto; }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", createdAt, userName, content);
    }
} 