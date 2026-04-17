package com.makemytrip.makemytrip.models;

import lombok.Data;

@Data
public class CancelBookingdat {
    private String id;
    private String bookingId;
    private String type; 
    private String qty; 
    private String reason;
}
