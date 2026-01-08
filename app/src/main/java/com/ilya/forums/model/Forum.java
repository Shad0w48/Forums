package com.ilya.forums.model;


import com.google.firebase.Timestamp;

import java.util.List;

public class Forum {


    private String forumId;
    private String name;
    private String description;
    private User creator;
    List<Post> postList;
    private Timestamp createdAt;

    public Forum(String forumId, String name, String description, User creator, List<Post> postList, Timestamp createdAt) {
        this.forumId = forumId;
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.postList = postList;
        this.createdAt = createdAt;
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

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
