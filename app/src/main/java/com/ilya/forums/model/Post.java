package com.ilya.forums.model;

public class Post {
    String postId, title, content, UserId, timestamp;


    int upVote, downVote;

    String ForumId;



    public Post() {
    }

    public Post(String postId, String title, String content, String UserId, String timestamp, int upVote, int downVote, String ForumId) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.UserId = UserId;
        this.timestamp = timestamp;
        this.upVote = upVote;
        this.downVote = downVote;
        this.ForumId = ForumId;
    }
    public Post(String postId, String title, String content, String UserId, String timestamp, String ForumId) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.UserId = UserId;
        this.timestamp = timestamp;
        this.upVote = 0;
        this.downVote = 0;
        this.ForumId = ForumId;
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
        return UserId;
    }

    public void setAuthorid(String authorid) {
        this.UserId = authorid;
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
        return ForumId;
    }

    public void setForumId(String forumId) {
        ForumId = forumId;
    }
}
