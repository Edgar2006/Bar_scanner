package com.example.qr_scanner.DataBase_Class;

public class Rating {
    public float rating;
    public int countView,countRating;

    public Rating(float rating, int countView, int countRating) {
        this.rating = rating;
        this.countView = countView;
        this.countRating = countRating;
    }

    public Rating() {
    }

}
