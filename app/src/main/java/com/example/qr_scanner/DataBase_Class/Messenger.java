package com.example.qr_scanner.DataBase_Class;

import java.util.ArrayList;
import java.util.List;

public class Messenger {
    private String email,comment;
    private String address,count;

    public Messenger(String email, String comment, String address, String count) {
        this.email = email;
        this.comment = comment;
        this.address = address;
        this.count = count;
    }

    public Messenger() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
}
