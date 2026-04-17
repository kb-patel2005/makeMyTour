package com.makemytrip.makemytrip.models;

import java.time.LocalDateTime;
import java.util.List;

public class FlightDto {

    private String id;
    private String flightName;
    private String from;
    private String to;
    private String status;
    private String delayReason;
    private String departureTime;
    private String arrivalTime;
    private double price;
    private int availableSeats;
    private int totalReviews;
    private List<TimePrice> priceHistory;
    private boolean[][] economicseats;
    private boolean[][] bussinesseats;
    public boolean[][] getEconomicseats() {
        return economicseats;
    }
    public void setEconomicseats(boolean[][] economicseats) {
        this.economicseats = economicseats;
    }
    public boolean[][] getBussinesseats() {
        return bussinesseats;
    }
    public void setBussinesseats(boolean[][] bussinesseats) {
        this.bussinesseats = bussinesseats;
    }
    public List<TimePrice> getPriceHistory() {
        return priceHistory;
    }
    public void setPriceHistory(List<TimePrice> priceHistory) {
        this.priceHistory = priceHistory;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getDelayReason() {
        return delayReason;
    }
    public void setDelayReason(String delayReason) {
        this.delayReason = delayReason;
    }
    public FlightDto() {}
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFlightName() {
        return flightName;
    }
    public void setFlightName(String flightName) {
        this.flightName = flightName;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
    public int getTotalReviews() {
        return totalReviews;
    }
    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

}
