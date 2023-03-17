package com.example.qr_scanner.DataBase_Class;

public class UserCommentSaveData {
    private String companyEmail, userEmail,barCode;

    public UserCommentSaveData(String companyName, String userName, String barCode) {
        this.companyEmail = companyName;
        this.userEmail = userName;
        this.barCode = barCode;
    }

    public UserCommentSaveData() {
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
