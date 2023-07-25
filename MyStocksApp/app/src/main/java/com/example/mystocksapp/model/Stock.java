package com.example.mystocksapp.model;

public class Stock {

    private String symbol;
    private double price;
    private double percentChange;

    public Stock(String symbol, double price, double percentChange){
        this.symbol = symbol;
        this.price = price;
        this.percentChange = percentChange;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public double getPercentChange() {
        return percentChange;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPercentChange(double percentChange) {
        this.percentChange = percentChange;
    }

}
