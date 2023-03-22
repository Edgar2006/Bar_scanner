package com.example.qr_scanner.Class;

public class UserLike {
    int like;
    String user;

    public UserLike(int like, String user) {
        this.like = like;
        this.user = user;
    }

    public UserLike() {
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
