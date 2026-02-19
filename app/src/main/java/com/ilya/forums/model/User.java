package com.ilya.forums.model;

import java.io.Serializable;

public class User implements Serializable {
    String id, fname, lname, email, phone, password;
    Boolean isAdmin;



    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public void setIsAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public User(String id, String fname, String lname, String email, String phone, String password, Boolean isAdmin) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public User(String id, String fname, String lname, String email, String phone, Boolean isAdmin) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.phone = phone;

        this.isAdmin = isAdmin;
    }


    public User(String id, String fname, String lname) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;

    }

    public User() {
    }

    public User(String id, String fname, String lname, String email, String phone, String password) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.isAdmin = false;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
