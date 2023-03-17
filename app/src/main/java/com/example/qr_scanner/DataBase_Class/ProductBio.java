package com.example.qr_scanner.DataBase_Class;

public class ProductBio {

    private String companyEmail, companyName, productName, imageRef, companyRef, bioShort, bioLong, barCode;
    private boolean access;
    private Long time;


    public ProductBio(String companyEmail ,String companyName, String productName, String imageRef, String companyRef, String bioShort, String bioLong, String barCode, boolean access, Long time) {
        this.companyEmail = companyEmail;
        this.companyName = companyName;
        this.productName = productName;
        this.imageRef = imageRef;
        this.companyRef = companyRef;
        this.bioShort = bioShort;
        this.bioLong = bioLong;
        this.barCode = barCode;
        this.access = access;
        this.time = time;
    }

    public ProductBio() {
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
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

    public boolean getAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }
}
