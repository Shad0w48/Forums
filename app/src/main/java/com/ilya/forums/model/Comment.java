package com.ilya.forums.model;

import java.io.Serializable;

public class Comment  implements Serializable {
    String commentId, timestamp, text, parentpostid;

    User author;
    public Comment() {
    }

    public Comment(String commentId, String timestamp, String text, String parentpostid, User author) {
        this.commentId = commentId;
        this.timestamp = timestamp;
        this.text = text;
        this.parentpostid = parentpostid;
        this.author = author;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getParentpostid() {
        return parentpostid;
    }

    public void setParentpostid(String parentpostid) {
        this.parentpostid = parentpostid;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId='" + commentId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", text='" + text + '\'' +
                ", parentpostid='" + parentpostid + '\'' +
                ", author=" + author +
                '}';
    }
}
