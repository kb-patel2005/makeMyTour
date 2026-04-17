package com.makemytrip.makemytrip.controllers;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.makemytrip.makemytrip.models.Booking;
import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.FlightUpdateRequest;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.repositories.FlightRepository;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import com.makemytrip.makemytrip.services.BookingService;
import com.makemytrip.makemytrip.services.PreferenceService;

@RestController
@RequestMapping("/booking")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private PreferenceService preferenceService;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private FlightRepository flightRepository;

    @PostMapping("/flight")
    public Booking flightBooking(@RequestBody Booking booking) {
        preferenceService.updatePreference(
                booking.getUserId(),
                flightRepository.findById(booking.getBookingId()).orElse(null), 
                "BOOK");
        return bookingService.addBooking(booking);
    }

    @PutMapping("/updateflight")
    public Flight updateFlight(@RequestBody FlightUpdateRequest request) {
        return bookingService.updateFlight(request);
    }

    @PostMapping("/hotel")
    public Booking hotelBooking(@RequestBody Booking booking) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        Hotel hotel = hotelRepository.findById(booking.getBookingId())
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        Booking savedBooking = bookingService.addBooking(booking);
        preferenceService.updateHotelPreference(userId, hotel, "BOOK");
        return savedBooking;
    }

    @PostMapping("/cancel")
    public Booking cancelBooking(@RequestBody Map<String, String> request) {
        String bookingId = request.get("bookingId");
        String cancellationReason = request.getOrDefault("cancellationReason", "Customer request");
        return bookingService.cancelBooking(bookingId, cancellationReason);
    }

    @PutMapping("/refund/{bookingId}")
    public Booking changeRefundStatus(
            @PathVariable("bookingId") String bookingId,
            @RequestParam("status") String status) {
        return bookingService.updateRefundStatus(bookingId, status);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable("bookingId") String bookingId) {
        bookingService.deleteBooking(bookingId);
    }

    @GetMapping("/get")
    public List<Booking> booking() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getPrincipal();
        return bookingService.getBooking(userId);
    }

    @GetMapping("/getIdList")
    public List<String> getIdsList(){
        return bookingService.getAllBookingIds();
    }

}