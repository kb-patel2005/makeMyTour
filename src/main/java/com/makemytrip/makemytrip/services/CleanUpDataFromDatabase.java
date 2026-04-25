package com.makemytrip.makemytrip.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.makemytrip.makemytrip.models.Booking;
import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.Room;
import com.makemytrip.makemytrip.repositories.BookingRepository;
import com.makemytrip.makemytrip.repositories.FlightRepository;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import com.makemytrip.makemytrip.repositories.NotificationRepository;

@Service
public class CleanUpDataFromDatabase {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Scheduled(fixedRate = 3600000)
    public void deleteExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findByRefundDateBefore(now);
        bookingRepository.deleteAll(bookings);
    }

    @Scheduled(fixedRate = 3600000) 
    public void deleteOldActiveBookings() {
        List<Booking> bookings = bookingService.booksaftercomplete();
        bookings.forEach(booking -> {
            bookingService.deleteBook(booking);
        });
        bookingRepository.deleteAll(bookings);
    }

    @Scheduled(fixedRate = 3600000)
    public void resetSeatsAfterArrival() {
        List<Flight> flights = flightRepository.findAll();
        for (Flight flight : flights) {
            if (flight.getArrivalTime() == null)
                continue;

            LocalDateTime arrival = LocalDateTime.parse(flight.getArrivalTime());

            if (arrival.isBefore(LocalDateTime.now())) {
                // Reset seats
                boolean[][] eco = new boolean[10][4];
                boolean[][] bus = new boolean[10][4];
                flight.setEconomicseats(eco);
                flight.setBussinesseats(bus);
                flight.setAvailableSeats(80);
                flight.setStatus("ON_TIME");
                flight.setDelayReason(null);
                flight.setPrice(flight.getBasePrice());
                if (flight.getPriceHistory() != null) {
                    flight.getPriceHistory().add(new TimePrice(LocalDateTime.now(), flight.getBasePrice()));
                }
                LocalDateTime newArrival = arrival.plusDays(1);
                LocalDateTime newDeparture = LocalDateTime.parse(flight.getDepartureTime()).plusDays(1);
                flight.setArrivalTime(newArrival.toString());
                flight.setDepartureTime(newDeparture.toString());
                flightRepository.save(flight);
                notificationRepository.deleteByEntityId(flight.getId());
            }
        }
    }


    @Scheduled(cron = "0 0 13 * * ?")
    public void resetHotelRooms() {
        List<Hotel> hotels = hotelRepository.findAll();
        for (Hotel hotel : hotels) {
            for (Room room : hotel.getRooms()) {
                List<Boolean> availability = room.getAvailability();
                for (int i = 0; i < availability.size(); i++) {
                    availability.set(i, false);
                }
            }
            hotelRepository.save(hotel);
        }
    }

}
