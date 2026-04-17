package com.makemytrip.makemytrip.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.makemytrip.makemytrip.models.Review;
import com.makemytrip.makemytrip.repositories.FlightRepository;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import com.makemytrip.makemytrip.repositories.ReviewRepository;

@RestController
@RequestMapping("/api")
public class ReviewController {

    @Autowired
    HotelRepository hotelRepository;
    @Autowired
    FlightRepository flightRepository;
    @Autowired
    ReviewRepository reviewRepository;

    @GetMapping("/get/hotels/{hotelId}/reviews")
    public ResponseEntity<List<Review>> getHotelReviews(@PathVariable("hotelId") String hotelId,
            @RequestParam(value = "orderBy", required = false) String orderBy) {
        if (orderBy == "DESC") {
            return ResponseEntity.ok(reviewRepository.findByEntityIdAndEntityTypeOrderByRatingDesc(hotelId, "hotel"));
        } else {
            return ResponseEntity
                    .ok(reviewRepository.findByEntityIdAndEntityTypeOrderByCreatedAtDesc(hotelId, "hotel"));
        }
    }

    @GetMapping("/get/flight/{flightId}/reviews")
    public ResponseEntity<List<Review>> getFlightReviews(@PathVariable("flightId") String flightId,
            @RequestParam(value = "orderBy", required = false) String orderBy) {
        if (orderBy == "DESC") {
            return ResponseEntity.ok(reviewRepository.findByEntityIdAndEntityTypeOrderByRatingDesc(flightId, "flight"));
        } else {
            return ResponseEntity
                    .ok(reviewRepository.findByEntityIdAndEntityTypeOrderByCreatedAtDesc(flightId, "flight"));
        }
    }

    @GetMapping("/image/{reviewId}")
    public List<String> getImages(@PathVariable String reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        List<String> images = new ArrayList<>();
        for (Binary img : review.getPhotos()) {
            String base64 = Base64.getEncoder().encodeToString(img.getData());
            images.add(base64);
        }
        return images;
    }

    @GetMapping("/reviews/flagged")
    public List<Review> getFlaggedReviews() {
        return reviewRepository.findByFlagged(true);
    }

    

}