package com.makemytrip.makemytrip.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "booking")
public class Booking {

    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed
    private String bookingId;
    private String type;
    private LocalDateTime bookingDate;
    private int quantity;
    private String seat;
    private List<String> seats;
    private List<Bookedhotel> rooms;
    private double totalPrice;
    private String status; 
    private String reason;
    private String cancellationReason;
    private double refundAmount;
    private String refundStatus;
    private LocalDateTime refundDate;

    public List<String> getSeats() {
        return seats;
    }
    public void setSeats(List<String> seats) {
        this.seats = seats;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public String getId() {
        return id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public LocalDateTime getBookingDate() {
        return bookingDate;
    }
    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getCancellationReason() {
        return cancellationReason;
    }
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    public double getRefundAmount() {
        return refundAmount;
    }
    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }
    public String getRefundStatus() {
        return refundStatus;
    }
    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }
    public LocalDateTime getRefundDate() {
        return refundDate;
    }
    public void setRefundDate(LocalDateTime refundDate) {
        this.refundDate = refundDate;
    }
    @Override
    public String toString() {
        return "Booking [id=" + id + ", userId=" + userId + ", type=" + type + ", bookingDate=" + bookingDate
                + ", quantity=" + quantity + ", totalPrice=" + totalPrice + ", status=" + status + ", reason=" + reason
                + ", cancellationReason=" + cancellationReason + ", refundAmount=" + refundAmount + ", refundStatus="
                + refundStatus + ", refundDate=" + refundDate + "]";
    }
    public String getBookingId() {
        return bookingId;
    }
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
    public String getSeat() {
        return seat;
    }
    public void setSeat(String seat) {
        this.seat = seat;
    }
    public List<Bookedhotel> getRooms() {
        return rooms;
    }
    public void setRooms(List<Bookedhotel> rooms) {
        this.rooms = rooms;
    }

}
