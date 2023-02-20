package com.example.qr_scanner.DataBase_Class;

public class User {
    private String name,email,imageRef;
    private boolean access;
    public static String EMAIL, NAME, EMAIL_CONVERT, URL;

    public User(String name, String email,String imageRef,boolean access) {
        this.name = name;
        this.email = email;
        this.imageRef = imageRef;
        this.access = access;
    }

    public User() {
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
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
}
