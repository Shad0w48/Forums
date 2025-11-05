package com.ilya.forums.model;

public class Comment {
    String author, timestamp, text, parentpostid;

    public Comment() {
    }

    public Comment(String author, String timestamp, String text, String parentpostid) {
        this.author = author;
        this.timestamp = timestamp;
        this.text = text;
        this.parentpostid = parentpostid;
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
        return "Vote{" +
                "author='" + author + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", text='" + text + '\'' +
                ", parentpostid='" + parentpostid + '\'' +
                '}';
    }
}
