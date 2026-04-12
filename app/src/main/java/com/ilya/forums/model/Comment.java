package com.ilya.forums.model;

import java.io.Serializable;
import java.util.Date;

public class Comment  implements Serializable {
    String commentId, text, parentpostid;

    Date date;
    User author;
    public Comment() {
    }

    public Comment(String commentId, Date date, String text, String parentpostid, User author) {
        this.commentId = commentId;
        this.date = date;
        this.text = text;
        this.parentpostid = parentpostid;
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
                ", date='" + date + '\'' +
                ", text='" + text + '\'' +
                ", parentpostid='" + parentpostid + '\'' +
                ", author=" + author +
                '}';
    }
}
