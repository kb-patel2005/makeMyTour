package com.makemytrip.makemytrip.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.makemytrip.makemytrip.models.Booking;

public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByUserId(String userId);

    List<Booking> findByBookingId(String bookingId);

    List<Booking> findByRefundDateBefore(LocalDateTime time);

    List<Booking> findByBookingDateBeforeAndStatusNot(
            LocalDateTime time,
            String status);
            
}
