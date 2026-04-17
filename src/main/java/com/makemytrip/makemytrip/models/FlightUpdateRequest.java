package com.makemytrip.makemytrip.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightUpdateRequest {
    
    private String id;
    private String flightName;
    private String to;
    private String from; 
    private String seatType;
    private List<String> seats;
    private String status;
    private String departureTime;
    private String arrivalTime;
    private String delayReason;
    private double totalPrice;
    private boolean[][] seatsMatrix;
}
