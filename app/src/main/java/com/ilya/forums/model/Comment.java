package com.ilya.forums.model;

import java.io.Serializable;

public class Comment  implements Serializable {
    String author, timestamp, text, parentpostid, authorId;

    public Comment() {
    }

    public Comment(String author, String timestamp, String text, String parentpostid,String authorId) {
        this.author = author;
        this.authorId=authorId;
        this.timestamp = timestamp;
        this.text = text;
        this.parentpostid = parentpostid;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
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


    @Override
    public String toString() {
        return "Comment{" +
                "author='" + author + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", text='" + text + '\'' +
                ", parentpostid='" + parentpostid + '\'' +
                ", authorId='" + authorId + '\'' +
                '}';
    }
}
