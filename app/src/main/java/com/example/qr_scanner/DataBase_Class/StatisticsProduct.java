package com.example.qr_scanner.DataBase_Class;

public class StatisticsProduct {
    private int count;
    private int countBuyPeople;
    public static int COUNT,COUNT_BUY_PEOPLE;

    public StatisticsProduct(int count, int countBuyPeople) {
        this.count = count;
        this.countBuyPeople = countBuyPeople;
    }

    public StatisticsProduct() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCountBuyPeople() {
        return countBuyPeople;
    }

    public void setCountBuyPeople(int countBuyPeople) {
        this.countBuyPeople = countBuyPeople;
    }

}
