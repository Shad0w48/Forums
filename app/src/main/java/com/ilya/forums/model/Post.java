package com.ilya.forums.model;

public class Post {
    String postId, title, content, userId, timestamp;


    int upVote, downVote;

    String forumId;



    public Post() {
    }

    public Post(String postId, String title, String content, String UserId, String timestamp, int upVote, int downVote, String ForumId) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.userId = UserId;
        this.timestamp = timestamp;
        this.upVote = upVote;
        this.downVote = downVote;
        this.forumId = ForumId;
    }
    public Post(String postId, String title, String content, String UserId, String timestamp, String ForumId) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.userId = UserId;
        this.timestamp = timestamp;
        this.upVote = 0;
        this.downVote = 0;
        this.forumId = ForumId;
    }



    public String getPostId() {
        return postId;
    }

    public void setPostId(String id) {
        this.postId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorid() {
        return userId;
    }

    public void setAuthorid(String authorid) {
        this.userId = authorid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getUpVote() {
        return upVote;
    }

    public void setUpVote(int upVote) {
        this.upVote = upVote;
    }

    public int getDownVote() {
        return downVote;
    }

    public void setDownVote(int downVote) {
        this.downVote = downVote;
    }

    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        forumId = forumId;
    }
}
