package com.makemytrip.makemytrip.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.makemytrip.makemytrip.models.Booking;
import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.FlightUpdateRequest;
import com.makemytrip.makemytrip.repositories.BookingRepository;
import com.makemytrip.makemytrip.repositories.FlightRepository;

@RestController
public class FlightController {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @MessageMapping("/flight/update")
    @SendTo("/topic/flights")
    public FlightUpdateRequest bookRide(FlightUpdateRequest request) {
        // Flight flight = flightRepository.findById(request.getId())
        //         .orElseThrow(() -> new RuntimeException("Flight not found"));
        // flight.setStatus(request.getStatus());
        // flight.setArrivalTime(request.getArrivalTime());
        // flight.setDepartureTime(request.getDepartureTime());
        return request;
    }

    @GetMapping("/flight")
    public List<FlightUpdateRequest> getUserFlights() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getPrincipal();
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        if (bookings == null || bookings.isEmpty()) {
            return Collections.emptyList();
        }
        List<Booking> flightBookings = bookings.stream()
                .filter(b -> "FLIGHT".equalsIgnoreCase(b.getType()))
                .toList();
        if (flightBookings.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> flightIds = flightBookings.stream()
                .map(Booking::getBookingId)
                .filter(Objects::nonNull) // 🔥 avoid null IDs
                .toList();
        Map<String, Flight> flightMap = flightRepository.findAllById(flightIds)
                .stream()
                .collect(Collectors.toMap(Flight::getId, f -> f));
        return flightBookings.stream()
                .map(b -> {
                    try {
                        Flight f = flightMap.get(b.getBookingId());
                        if (f == null) {
                            System.out.println("⚠️ Flight not found for bookingId: " + b.getBookingId());
                            return null;
                        }
                        return new FlightUpdateRequest(
                                b.getBookingId(),
                                f.getFlightName(),
                                f.getTo(),
                                f.getFrom(),
                                b.getSeat(),
                                b.getSeats() != null
                                        ? b.getSeats()
                                        : List.of(),
                                f.getStatus(),
                                f.getDepartureTime(),
                                f.getArrivalTime(),
                                f.getDelayReason(),
                                b.getTotalPrice(),
                                null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

}
