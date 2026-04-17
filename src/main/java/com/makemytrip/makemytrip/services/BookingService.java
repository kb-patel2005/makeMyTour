package com.makemytrip.makemytrip.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.makemytrip.makemytrip.models.Bookedhotel;
import com.makemytrip.makemytrip.models.Booking;
import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.FlightUpdateRequest;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.Room;
import com.makemytrip.makemytrip.repositories.BookingRepository;
import com.makemytrip.makemytrip.repositories.FlightRepository;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Flight updateFlight(FlightUpdateRequest request) {
        Flight flight = flightRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        if ((request.getStatus() != "ON_TIME") && (request.getSeatType()).equals(null)) {
            flight.setStatus(request.getStatus());
            flight.setDepartureTime(request.getDepartureTime());
            flight.setArrivalTime(request.getArrivalTime());
            flight.setDelayReason(request.getDelayReason());
            flight.setPrice(request.getTotalPrice());
        }
        if (request.getSeats() != null && !request.getSeats().isEmpty()) {
            boolean[][] seatMatrix = request.getSeatType().startsWith("e") || request.getSeatType().startsWith("E")
                    ? flight.getEconomicseats()
                    : flight.getBussinesseats();
            if (seatMatrix == null) {
                throw new RuntimeException("Seat layout not found");
            }
            for (String seat : request.getSeats()) {
                int row = Integer.parseInt(seat.substring(0, seat.length() - 1)) - 1;
                int col = seat.charAt(seat.length() - 1) - 'A';
                if (seatMatrix[row][col]) {
                    throw new RuntimeException("Seat already booked: " + seat);
                }
            }
            for (String seat : request.getSeats()) {
                int row = Integer.parseInt(seat.substring(0, seat.length() - 1)) - 1;
                int col = seat.charAt(seat.length() - 1) - 'A';
                seatMatrix[row][col] = true;
            }
            flight.setAvailableSeats(
                    flight.getAvailableSeats() - request.getSeats().size());
            if (request.getSeatType().equalsIgnoreCase("economics")) {
                flight.setEconomicseats(seatMatrix);
            } else {
                flight.setBussinesseats(seatMatrix);
            }
        }
        return flightRepository.save(flight);
    }

    public double calculateRefund(Booking booking) {
        if (booking.getBookingDate() == null) {
            return 0;
        }
        LocalDateTime bookingTime = booking.getBookingDate();
        long hours = Duration.between(bookingTime, LocalDateTime.now()).toHours();
        if (hours <= 24) {
            return booking.getTotalPrice() * 0.5;
        } else if (hours <= 72) {
            return booking.getTotalPrice() * 0.25;
        }
        return 0;
    }

    public Booking addBooking(Booking booking) {
        if ("FLIGHT".equalsIgnoreCase(booking.getType())) {
            Optional<Flight> optionalFlight = flightRepository.findById(booking.getBookingId());
            if (optionalFlight.isPresent()) {
                Flight flight = optionalFlight.get();
                    flightRepository.save(flight);
            }
        } else if ("HOTEL".equalsIgnoreCase(booking.getType())) {
            Optional<Hotel> optionalHotel = hotelRepository.findById(booking.getBookingId());
            if (optionalHotel.isPresent()) {
                Hotel hotel = optionalHotel.get();
                Bookedhotel bookedHotel = booking.getRooms().get(0);
                for (Room room : hotel.getRooms()) {
                    if (room.getType().equalsIgnoreCase(bookedHotel.getType())) {
                        bookedHotel.getRooms().forEach(e -> {
                            int index = Integer.parseInt(e);
                            Boolean isAvailable = room.getAvailability().get(index);
                            if (isAvailable) {
                                throw new RuntimeException("Room not available");
                            }
                        });
                        List<Boolean> availibilty = room.getAvailability();
                        bookedHotel.getRooms().forEach(e -> {
                            int index = Integer.parseInt(e);
                            availibilty.set(index, true);
                        });
                        room.setAvailability(availibilty);
                    }
                }
            }
        }
        return bookingRepository.save(booking);
    }

    public void deleteBook(Booking booking) {
        if ("flight".equalsIgnoreCase(booking.getType())) {
            Flight flight = flightRepository.findById(booking.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Flight not found"));
            boolean[][] seats;
            if (booking.getSeat().toLowerCase().contains("econom")) {
                seats = flight.getEconomicseats();
            } else {
                seats = flight.getBussinesseats();
            }
            for (String seat : booking.getSeats()) {
                int row = Integer.parseInt(seat.substring(0, seat.length() - 1)) - 1;
                char colChar = seat.charAt(seat.length() - 1);
                int col = colChar - 'A';
                seats[row][col] = false;
            }
            if (booking.getSeat().toLowerCase().contains("econom")) {
                flight.setEconomicseats(seats);
            } else {
                flight.setBussinesseats(seats);
            }
            Flight updatedFlight = flightRepository.save(flight);
            messagingTemplate.convertAndSend("/topic/flights", updatedFlight);
        }
        if ("hotel".equalsIgnoreCase(booking.getType())) {
            Hotel hotel = hotelRepository.findById(booking.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));
            List<Room> hotelRooms = hotel.getRooms();
            for (Bookedhotel booked : booking.getRooms()) {
                String bookedType = booked.getType(); // e.g. "DELUXE"
                for (Room room : hotelRooms) {
                    if (room.getType().equalsIgnoreCase(bookedType)) {
                        List<Boolean> availability = room.getAvailability();
                        for (String roomNumber : booked.getRooms()) {
                            int index = Integer.parseInt(roomNumber) - 1;
                            if (index >= 0 && index < availability.size()) {
                                availability.set(index, false); // make available again
                            }
                        }
                    }
                }
            }
            Hotel updatedHotel = hotelRepository.save(hotel);
            messagingTemplate.convertAndSend("/topic/hotels", updatedHotel);
        }
    }

    public Booking cancelBooking(String bookingId, String reason) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty())
            return null;
        Booking booking = optionalBooking.get();
        deleteBook(booking);
        booking.setStatus("CANCELLED");
        booking.setCancellationReason(reason);
        double refund = calculateRefund(booking);
        booking.setRefundAmount(refund);
        booking.setRefundStatus("PENDING");
        booking.setRefundDate(LocalDateTime.now().plusDays(1));
        return bookingRepository.save(booking);
    }

    public Booking updateRefundStatus(String bookingId, String status) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setRefundStatus(status);
            if ("PROCESSED".equalsIgnoreCase(status)) {
                booking.setRefundDate(LocalDateTime.now());
            } else if ("COMPLETED".equalsIgnoreCase(status)) {
                booking.setRefundDate(LocalDateTime.now().plusDays(1));
            }
            return bookingRepository.save(booking);
        }
        return null;
    }

    public List<Booking> getBooking(String userId) {
        return bookingRepository.findByUserId(userId);
    }

    public void deleteBooking(String id) {
        bookingRepository.deleteById(id);
    }

    public List<String> getAllBookingIds() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) auth.getPrincipal();
        List<Booking> booking = bookingRepository.findByUserId(userId);
        Set<String> uniqueIds = booking.stream()
                .map(Booking::getBookingId)
                .collect(Collectors.toSet());
        List<String> idList = new ArrayList<>(uniqueIds);
        return idList;

    }

    public List<Booking> booksaftercomplete(){
        return bookingRepository.findByBookingDateBeforeAndStatusNot(LocalDateTime.now(),"CANCELLED");
    }
}