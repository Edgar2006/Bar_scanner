package com.example.qr_scanner.DataBase_Class;

public class Company {
    private String name,email,description,imageRef;


    public Company(String email, String name, String description, String imageRef) {
        this.name = name;
        this.email = email;
        this.description = description;
        this.imageRef = imageRef;
    }

    public Company() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }

    @Override
    public String toString() {
        return "Company{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", description='" + description + '\'' +
                ", imageRef='" + imageRef + '\'' +
                '}';
    }
}
