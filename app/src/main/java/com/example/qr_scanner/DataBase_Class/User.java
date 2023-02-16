package com.example.qr_scanner.DataBase_Class;

public class User {
    private String name,email,imageRef;
    public static String EMAIL, NAME, EMAIL_CONVERT, URL;

    public User(String name, String email,String imageRef) {
        this.name = name;
        this.email = email;
        this.imageRef = imageRef;
    }

    public User() {
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
