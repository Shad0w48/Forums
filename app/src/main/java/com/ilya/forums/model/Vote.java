package com.ilya.forums.model;

import java.io.Serializable;

public class Vote implements Serializable {
    String userid, postid;
    boolean typeVote;

    public Vote() {
    }

    public Vote(String userid, String postid, boolean typeVote) {
        this.userid = userid;
        this.postid = postid;
        this.typeVote = typeVote;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public boolean isTypeVote() {
        return typeVote;
    }

    public void setTypeVote(boolean typeVote) {
        this.typeVote = typeVote;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "userid='" + userid + '\'' +
                ", postid='" + postid + '\'' +
                ", typeVote=" + typeVote +
                '}';
    }
}
