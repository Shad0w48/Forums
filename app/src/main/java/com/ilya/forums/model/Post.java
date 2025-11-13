package com.ilya.forums.model;

public class Post {
    String postId, title, content, authorid, timestamp, upVote, downVote, communityid;

    public Post() {
    }

    public Post(String postId, String title, String content, String authorid, String timestamp, String upVote, String downVote, String communityid) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.authorid = authorid;
        this.timestamp = timestamp;
        this.upVote = upVote;
        this.downVote = downVote;
        this.communityid = communityid;
    }

    public Post(String title, String content, String authorid, String timestamp, String upVote, String downVote, String communityid) {
        this.title = title;
        this.content = content;
        this.authorid = authorid;
        this.timestamp = timestamp;
        this.upVote = upVote;
        this.downVote = downVote;
        this.communityid = communityid;
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
        return authorid;
    }

    public void setAuthorid(String authorid) {
        this.authorid = authorid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUpVote() {
        return upVote;
    }

    public void setUpVote(String upVote) {
        this.upVote = upVote;
    }

    public String getDownVote() {
        return downVote;
    }

    public void setDownVote(String downVote) {
        this.downVote = downVote;
    }

    public String getCommunityid() {
        return communityid;
    }

    public void setCommunityid(String communityid) {
        this.communityid = communityid;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + postId + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", authorid='" + authorid + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", upVote='" + upVote + '\'' +
                ", downVote='" + downVote + '\'' +
                ", communityid='" + communityid + '\'' +
                '}';
    }
}
