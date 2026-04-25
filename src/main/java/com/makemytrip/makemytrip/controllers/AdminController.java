package com.makemytrip.makemytrip.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import com.makemytrip.makemytrip.models.Users;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.Room;
import com.makemytrip.makemytrip.models.TimePrice;
import com.makemytrip.makemytrip.repositories.UserRepository;
import com.makemytrip.makemytrip.repositories.FlightRepository;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import jakarta.security.auth.message.AuthException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private FlightRepository flightRepository;

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getallusers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
        if (roles.contains("ADMIN")) {
            List<Users> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        }
        throw new AccessDeniedException("it only admin access.......");
    }

    @PostMapping("/flight")
    public Flight addflight(@RequestBody Flight flight) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ADMIN"));
        if (isAdmin) {
            boolean[][] economicSeats = new boolean[10][4];
            boolean[][] businessSeats = new boolean[10][4];
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 4; j++) {
                    economicSeats[i][j] = false;
                    businessSeats[i][j] = false;
                }
            }
            flight.setEconomicseats(economicSeats);
            flight.setBussinesseats(businessSeats);
            flight.setBasePrice(flight.getPrice());
            if (flight.getPriceHistory() == null) {
                flight.setPriceHistory(new ArrayList<>());
            }
            flight.getPriceHistory().add(
                    new TimePrice(flight.getPrice(), Instant.now().toString()));
            return flightRepository.save(flight);
        }

        throw new AccessDeniedException("Only admin can add flight");
    }

    @PostMapping("/hotel")
    public Hotel createHotel(
            @RequestPart("hotel") String hotelJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    
        if (!isAdmin) {
            throw new AuthException("Only admin can do this activity");
        }
    
        ObjectMapper mapper = new ObjectMapper();
        Hotel hotel = mapper.readValue(hotelJson, Hotel.class);
    
        if (hotel.getRooms() == null) {
            hotel.setRooms(new ArrayList<>());
        }
    
        List<Room> rooms = hotel.getRooms();
        int fileIndex = 0;
    
        for (Room room : rooms) {
            List<byte[]> images = new ArrayList<>();
            if (files != null) {
                for (MultipartFile f : files) {
                    images.add(f.getBytes());
                }
            }
            room.setImages(images);
            room.setAvailability(
                    new ArrayList<>(Collections.nCopies(Integer.parseInt(room.getTotalRooms()), false))
            );
        }
    
        return hotelRepository.save(hotel);
    }

    @PutMapping("/flight/{id}")
    public ResponseEntity<Flight> editflight(@PathVariable("id") String id,
            @RequestParam(value = "type", required = false) String type,
            @RequestBody Flight updatedFlight) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ADMIN"));
        if (isAdmin) {
            Optional<Flight> flightOptional = flightRepository.findById(id);
            if (flightOptional.isPresent()) {
                Flight flight = flightOptional.get();
                flight.setId(id);
                flight.setDepartureTime(updatedFlight.getDepartureTime());
                flight.setArrivalTime(updatedFlight.getArrivalTime());
                flight.setPrice(updatedFlight.getPrice());
                flight.setStatus(updatedFlight.getStatus());
                flight.setDelayReason(updatedFlight.getDelayReason());  
                flightRepository.save(flight);
                return ResponseEntity.ok(flight);
            }
            return ResponseEntity.notFound().build();
        }

        throw new AccessDeniedException("it only admin access.......");
    }

    @PutMapping("/hotel/{id}")
    public ResponseEntity<Hotel> editHotel(@PathVariable("id") String id, @RequestBody Hotel updatedHotel) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
        if (roles.contains("ADMIN")) {
            Optional<Hotel> hotelOptional = hotelRepository.findById(id);
            if (hotelOptional.isPresent()) {
                Hotel hotel = hotelOptional.get();
                hotel.setHotelName(updatedHotel.getHotelName());
                hotelRepository.save(hotel);
                return ResponseEntity.ok(hotel);
            }
            return ResponseEntity.notFound().build();
        }
        throw new AccessDeniedException("it only admin access.......");
    }

}
