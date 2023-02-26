package com.example.qr_scanner.DataBase_Class;

public class ProductBio {
    private String companyName, productName, imageRef, companyRef, bioShort, bioLong, barCode;

    public ProductBio(String companyName, String productName, String imageRef, String companyRef, String bioShort, String bioLong, String barCode) {
        this.companyName = companyName;
        this.productName = productName;
        this.imageRef = imageRef;
        this.companyRef = companyRef;
        this.bioShort = bioShort;
        this.bioLong = bioLong;
        this.barCode = barCode;
    }

    public ProductBio() {
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getCompanyRef() {
        return companyRef;
    }

    public void setCompanyRef(String companyRef) {
        this.companyRef = companyRef;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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
