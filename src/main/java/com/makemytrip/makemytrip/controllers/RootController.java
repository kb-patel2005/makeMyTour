package com.makemytrip.makemytrip.controllers;

import com.makemytrip.makemytrip.config.Jwtutils;
import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.FlightDto;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.HotelDto;
import com.makemytrip.makemytrip.models.Room;
import com.makemytrip.makemytrip.models.RoomDto;
import com.makemytrip.makemytrip.models.TimePrice;
import com.makemytrip.makemytrip.repositories.FlightRepository;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import com.makemytrip.makemytrip.services.PreferenceService;

import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/public")
public class RootController {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private PreferenceService preferenceService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private Jwtutils jwtutils;

    @GetMapping("/hotels")
    public List<HotelDto> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        List<HotelDto> result = new ArrayList<>();
        for (Hotel h : hotels) {
            HotelDto dto = new HotelDto();
            dto.set_id(h.get_id());
            dto.setHotelName(h.getHotelName());
            dto.setDescription(h.getDescription());
            dto.setLocation(h.getLocation());
            dto.setRating(h.getRating());
            result.add(dto);
        }

        return result;
    }

    @GetMapping("/flights")
    public ResponseEntity<List<FlightDto>> getAllFlights() {

        List<Flight> flights = flightRepository.findAll();

        List<FlightDto> dtos = flights.stream().map(f -> {
            FlightDto dto = new FlightDto();
            dto.setId(f.getId());
            dto.setFlightName(f.getFlightName());
            dto.setFrom(f.getFrom());
            dto.setTo(f.getTo());
            dto.setBussinesseats(f.getBussinesseats());
            dto.setEconomicseats(f.getEconomicseats());
            dto.setPrice(f.getPrice());
            dto.setArrivalTime(f.getArrivalTime());
            dto.setDepartureTime(f.getDepartureTime());
            dto.setAvailableSeats(f.getAvailableSeats());
            dto.setTotalReviews(f.getTotalReviews());
            dto.setStatus(f.getStatus());
            dto.setDelayReason(f.getDelayReason());

            return dto;
        }).toList();

        return ResponseEntity.ok(dtos);
    }

    private double getDemandRatio(Flight flight) {
        int booked = 0;
        int total = 0;

        for (boolean[] row : flight.getEconomicseats()) {
            for (boolean seat : row) {
                if (seat)
                    booked++;
                total++;
            }
        }

        for (boolean[] row : flight.getBussinesseats()) {
            for (boolean seat : row) {
                if (seat)
                    booked++;
                total++;
            }
        }

        return total == 0 ? 0 : (double) booked / total;
    }

    private double calculatePrice(Flight flight) {
        double basePrice = flight.getBasePrice(); 
        double finalPrice = basePrice;

        double demand = getDemandRatio(flight);

        if (demand >= 0.8) {
            finalPrice *= 1.10;
        } else if (demand >= 0.6) {
            finalPrice *= 1.08;
        } else if (demand >= 0.4) {
            finalPrice *= 1.06;
        } else if (demand >= 0.2) {
            finalPrice *= 1.05;
        }
        LocalDate departure = LocalDate.parse(flight.getDepartureTime().substring(0, 10));
        Month month = departure.getMonth();
        double seasonalFactor = switch (month) {
            case DECEMBER -> 1.10;
            case JANUARY, MAY -> 1.08;
            case JUNE -> 1.05;
            default -> 1.0;
        };

        finalPrice *= seasonalFactor;

        return Math.round(finalPrice);
    }

    private void updatePriceHistory(Flight flight, double newPrice) {
        if (flight.getPriceHistory() == null) {
            flight.setPriceHistory(new ArrayList<>());
        }
        List<TimePrice> history = flight.getPriceHistory();
        if (!history.isEmpty()) {
            double lastPrice = history.get(history.size() - 1).getPrice();
            if (lastPrice == newPrice)
                return;
        }

        history.add(new TimePrice(newPrice, Instant.now().toString()));
    }

    private String extractUserIdFromRequest(HttpServletRequest request) {
        try {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                String user = jwtutils.extractUserId(token);
                if (user != null) {
                    return user;
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid token, continuing as guest");
        }

        return null;
    }

    @GetMapping("/flights/id/{id}")
    public Flight getFlight(@PathVariable("id") String id, HttpServletRequest request) {
        Flight flight = flightRepository.findById(id).orElseThrow();
        String userId = extractUserIdFromRequest(request);
        if (userId != null) {
            preferenceService.updatePreference(userId, flight, "VIEW");
        }
        double newPrice = calculatePrice(flight);
        if (Math.abs(flight.getPrice() - newPrice) > 0.01) {
            flight.setPrice(newPrice);
            updatePriceHistory(flight, newPrice);
            flightRepository.save(flight);
            messagingTemplate.convertAndSend("/topic/flights", flight);
        }
        return flight;
    }

    @GetMapping("/hotel/id/{id}")
    public ResponseEntity<HotelDto> getHotel(@PathVariable("id") String id,
            HttpServletRequest request) throws AuthException {
        Hotel hotel = hotelRepository.findById(id).orElse(null);
        HotelDto dto = new HotelDto();
        dto.set_id(hotel.get_id());
        dto.setHotelName(hotel.getHotelName());
        dto.setDescription(hotel.getDescription());
        dto.setLocation(hotel.getLocation());
        dto.setRating(hotel.getRating());
        List<RoomDto> roomDtos = new ArrayList<>();
        if (hotel.getRooms() != null) {
            for (Room room : hotel.getRooms()) {
                RoomDto r = new RoomDto();
                r.setType(room.getType());
                r.setPrice(room.getPrice());
                r.setTotalRooms(room.getTotalRooms());
                r.setAmenities(room.getAmenities());
                r.setAvailability(room.getAvailability());
                List<String> imgs = new ArrayList<>();
                if (room.getImages() != null) {
                    for (byte[] img : room.getImages()) {
                        imgs.add(
                                "data:image/jpeg;base64," +
                                        Base64.getEncoder().encodeToString(img));
                    }
                }
                r.setPhotos(imgs);
                roomDtos.add(r);
            }
        }
        dto.setRooms(roomDtos);
        String userId = extractUserIdFromRequest(request);
        preferenceService.updateHotelPreference(userId, hotel, "VIEW");
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/flights/search")
    public List<Flight> getFlights(
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            HttpServletRequest request) throws AuthException {
        List<Flight> f = flightRepository.findByFromIgnoreCaseAndToIgnoreCase(from, to);
        String userId = extractUserIdFromRequest(request);
        f.forEach(flight -> {
            preferenceService.updatePreference(userId, flight, "SEARCH");
        });
        return f;
    }

    @PostMapping("/flight/flighthistory/{flightId}")
    public List<TimePrice> fetchFlightHistory(@PathVariable("flightId") String flightId) {
        Optional<Flight> flight = flightRepository.findById(flightId);
        if (flight.isPresent()) {
            return flight.get().getPriceHistory();
        }
        return null;
    }

    @GetMapping("/flight/status/{flightId}")
    public Map<String, Object> getFlightStatus(@PathVariable("flightId") String flightId) {
        Optional<Flight> optionalFlight = flightRepository.findById(flightId);
        if (optionalFlight.isEmpty()) {
            Map<String, Object> missing = new HashMap<>();
            missing.put("error", "Flight not found");
            missing.put("flightId", flightId);
            return missing;
        }
        Flight flight = optionalFlight.get();
        String[] runtimeStates = new String[] { "On Time", "Delayed", "Boarding", "Departed" };
        Random random = new Random(System.currentTimeMillis());
        String status = flight.getStatus() != null && !flight.getStatus().isBlank() ? flight.getStatus()
                : runtimeStates[random.nextInt(runtimeStates.length)];
        LocalDateTime departure = LocalDateTime.parse(flight.getDepartureTime());
        LocalDateTime arrival = LocalDateTime.parse(flight.getArrivalTime());
        String delayReason = flight.getDelayReason() != null ? flight.getDelayReason() : "No delay";
        if (status.equalsIgnoreCase("Delayed")) {
            int delayMins = 10 + random.nextInt(50);
            departure = departure.plusMinutes(delayMins);
            arrival = arrival.plusMinutes(delayMins);
            delayReason = "Weather or air-traffic congestion";
        }
        Map<String, Object> dto = new HashMap<>();
        dto.put("flightId", flightId);
        dto.put("flightName", flight.getFlightName());
        dto.put("status", status);
        dto.put("delayReason", delayReason);
        dto.put("departureTime", departure.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.put("arrivalTime", arrival.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.put("estimatedArrival", arrival.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.put("updatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return dto;
    }

    @GetMapping("/{id}/images")
    public List<List<String>> getImages(@PathVariable("id") String id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow();
        List<List<String>> result = new ArrayList<>();
        if (hotel.getRooms() != null) {
            for (Room room : hotel.getRooms()) {
                List<String> imgs = new ArrayList<>();
                if (room.getImages() != null) {
                    for (byte[] img : room.getImages()) {
                        imgs.add("data:image/jpeg;base64," +
                                Base64.getEncoder().encodeToString(img));
                    }
                }
                result.add(imgs);
            }
        }
        return result;
    }

    @GetMapping("/hotel/{place}")
    public List<HotelDto> gethotelByUsingPlace(@PathVariable("place") String place,
            HttpServletRequest request)
            throws AuthException {
        List<Hotel> hotels = hotelRepository.findByLocationIgnoreCase(place);
        List<HotelDto> hoteldata = new ArrayList<>();
        String userId = extractUserIdFromRequest(request);
        for (Hotel hotel : hotels) {
            HotelDto dto = new HotelDto();
            dto.set_id(hotel.get_id());
            dto.setHotelName(hotel.getHotelName());
            dto.setDescription(hotel.getDescription());
            dto.setLocation(hotel.getLocation());
            dto.setRating(hotel.getRating());
            List<RoomDto> roomDtos = new ArrayList<>();
            if (hotel.getRooms() != null) {
                for (Room room : hotel.getRooms()) {
                    RoomDto r = new RoomDto();
                    r.setType(room.getType());
                    r.setPrice(room.getPrice());
                    r.setTotalRooms(room.getTotalRooms());
                    r.setAmenities(room.getAmenities());
                    r.setAvailability(room.getAvailability());
                    List<String> imgs = new ArrayList<>();
                    if (room.getImages() != null) {
                        for (byte[] img : room.getImages()) {
                            imgs.add(
                                    "data:image/jpeg;base64," +
                                            Base64.getEncoder().encodeToString(img));
                        }
                    }
                    r.setPhotos(imgs);
                    roomDtos.add(r);
                }
            }

            dto.setRooms(roomDtos);

            hoteldata.add(dto);

            if (userId != null) {
                preferenceService.updateHotelPreference(userId, hotel, "VIEW");
            }
        }

        // ✅ RETURN CORRECT DATA
        return hoteldata;
    }

}