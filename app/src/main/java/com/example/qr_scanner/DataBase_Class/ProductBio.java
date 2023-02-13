package com.example.qr_scanner.DataBase_Class;

public class ProductBio {
    private String productName, imageRef, bioShort,bioLong;

    public ProductBio(String productName, String imageRef, String bioShort, String bioLong) {
        this.productName = productName;
        this.imageRef = imageRef;
        this.bioShort = bioShort;
        this.bioLong = bioLong;
    }

    public ProductBio() {
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }

    public String getBioShort() {
        return bioShort;
    }

    public void setBioShort(String bioShort) {
        this.bioShort = bioShort;
    }

    public String getBioLong() {
        return bioLong;
    }

    public void setBioLong(String bioLong) {
        this.bioLong = bioLong;
    }
}
