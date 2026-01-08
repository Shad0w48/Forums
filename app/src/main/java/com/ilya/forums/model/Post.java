package com.ilya.forums.model;

public class Post {
    String postId, title, content,  timestamp;
    User user;


    int upVote, downVote;

    String forumId;
    String postPic;



    public Post() {
    }


    public Post(String postId, String title, String content, String timestamp, User user, int upVote, int downVote, String forumId, String postPic) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.user = user;
        this.upVote = upVote;
        this.downVote = downVote;
        this.forumId = forumId;
        this.postPic = postPic;
    }


    public Post(String postId, String title, String content, User user, String timestamp, String ForumId) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPostPic() {
        return postPic;
    }

    public void setPostPic(String postPic) {
        this.postPic = postPic;
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

    @Override
    public String toString() {
        return "Post{" +
                "postId='" + postId + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", user=" + user +
                ", upVote=" + upVote +
                ", downVote=" + downVote +
                ", forumId='" + forumId + '\'' +
                ", postPic='" + postPic + '\'' +
                '}';
    }
}

