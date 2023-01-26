package com.example.qr_scanner.Class;

import java.util.ArrayList;
import java.util.List;

public class Messenger {
    private String comment,email;
    private String address;
    public Messenger(String address,String comment, String email) {
        this.address = address;
        this.comment = comment;
        this.email = email;
    }

    public Messenger() {
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
}
