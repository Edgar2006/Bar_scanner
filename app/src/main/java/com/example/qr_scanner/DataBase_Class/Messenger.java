package com.example.qr_scanner.DataBase_Class;

import java.util.ArrayList;
import java.util.List;

public class Messenger {
    private String email, name, comment, address, count, imageRef, userRef;
    private float ratingBarScore;
    private long time;

    public Messenger(String email, String name, String comment, String address, String count, String imageRef, String userRef,long time,float ratingBarScore) {
        this.email = email;
        this.name = name;
        this.comment = comment;
        this.address = address;
        this.count = count;
        this.imageRef = imageRef;
        this.userRef = userRef;
        this.time = time;
        this.ratingBarScore = ratingBarScore;
    }

    public Messenger() {
    }
    public Messenger(String a){
        this.email = a;
        this.name = a;
        this.comment = a;
        this.address = a;
        this.count = a;
        this.imageRef = a;
        this.userRef = a;
        this.time = 0;
        this.ratingBarScore = 0F;
    }
    public float getRatingBarScore() {
        return ratingBarScore;
    }

    public void setRatingBarScore(float ratingBarScore) {
        this.ratingBarScore = ratingBarScore;
    }

    public String getUserRef() {
        return userRef;
    }

    public void setUserRef(String userRef) {
        this.userRef = userRef;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Messenger{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", address='" + address + '\'' +
                ", count='" + count + '\'' +
                ", imageRef='" + imageRef + '\'' +
                ", userRef='" + userRef + '\'' +
                ", ratingBarScore=" + ratingBarScore +
                ", time=" + time +
                '}';
    }
}
