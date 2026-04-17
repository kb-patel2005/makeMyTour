package com.makemytrip.makemytrip.models;

import java.time.LocalDateTime;

public class TimePrice {
    private LocalDateTime timestamp;
    private double price;

    public TimePrice(){}
    public TimePrice(LocalDateTime timestamp, double price) {
        this.timestamp = timestamp;
        this.price = price;
    }
    public TimePrice(double newPrice, String string) {
        this.price = newPrice;
        this.timestamp = LocalDateTime.now();
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
}
