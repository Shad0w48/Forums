package com.ilya.forums.model;


import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.List;

public class Forum {


    private String forumId;
    private String name;
    private String description;
    private User creator;
    private Date createdAt;

    public Forum(String forumId, String name, String description, User creator, Date createdAt) {
        this.forumId = forumId;
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.createdAt = createdAt;
    }

    public Forum(Forum forum) {
        this.forumId = forum.getForumId();
        this.name = forum.getName();

    }

    public Forum() {
    }

    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }


    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Forum{" +
                "forumId='" + forumId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", creator=" + creator +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
