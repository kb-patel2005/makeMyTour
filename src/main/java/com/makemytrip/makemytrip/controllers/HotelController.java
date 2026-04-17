package com.makemytrip.makemytrip.controllers;

import com.makemytrip.makemytrip.config.Jwtutils;
import com.makemytrip.makemytrip.models.Booking;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.Room;
import com.makemytrip.makemytrip.repositories.BookingRepository;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import com.makemytrip.makemytrip.services.PreferenceService;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotel")
public class HotelController {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PreferenceService preferenceService;

    @Autowired
    private Jwtutils jwtutils;

    @MessageMapping("/hotel/update")
    @SendTo("/topic/hotels")
    public Hotel bookHotel(Hotel hotel) {
        hotelRepository.save(hotel);
        return hotel;
    }

    public HotelController(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @PutMapping("/{id}")
    public Hotel updateHotel(
            @PathVariable("id") String id, 
            @RequestParam("type") String type,
            @RequestParam("roomIndex") String roomIndex,
            @RequestBody Hotel updatedHotel,
            HttpServletRequest req) throws AuthException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String header = req.getHeader("Authorization");
        if (header == null && !header.startsWith("Bearer ")) {
            throw new AuthException("you can't upadate this one");
        }
        String token = header.substring(7);
        String userId = (String) jwtutils.extractUserId(token);
        if ((authentication.getPrincipal()).equals(userId)) {
            Hotel hotel = hotelRepository.findById(id).orElseThrow();
            hotel.setHotelName(updatedHotel.getHotelName());
            hotel.setDescription(updatedHotel.getDescription());
            hotel.setLocation(updatedHotel.getLocation());
            hotel.setRating(updatedHotel.getRating());
            for (int i = 0; i < hotel.getRooms().size(); i++) {
                if (hotel.getRooms().get(i).getType().equalsIgnoreCase(type)) {
                    Room r = hotel.getRooms().get(i);
                    for (String index : roomIndex.split(",")) {
                        int idx = Integer.parseInt(index);
                        if (idx >= 0 && idx < r.getAvailability().size()) {
                            if (r.getAvailability().get(idx)) {
                                throw new RuntimeException("Room already booked");
                            } else {
                                r.getAvailability().set(idx, true);
                            }
                        }
                    }

                }
            }
            return hotelRepository.save(hotel);
        }
        throw new AuthException("not  able  for  this  update..........................");
    }

    @PostMapping("/book")
    public Booking bookHotel(@RequestBody Booking booking) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        preferenceService.updateHotelPreference((String)authentication.getPrincipal(), hotelRepository.findById(booking.getBookingId()).orElse(null), "BOOK");
        return bookingRepository.save(booking);
    }

}