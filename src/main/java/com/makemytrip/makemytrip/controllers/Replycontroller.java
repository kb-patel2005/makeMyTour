package com.makemytrip.makemytrip.controllers;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.Reply;
import com.makemytrip.makemytrip.models.Review;
import com.makemytrip.makemytrip.repositories.FlightRepository;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import com.makemytrip.makemytrip.repositories.ReviewRepository;

import io.jsonwebtoken.io.IOException;

@RestController
@RequestMapping("/reply")
public class Replycontroller {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @PostMapping("/flights/{bookingId}/reviews")
    public Review addflightReview(@PathVariable("bookingId") String flightId,
            @RequestParam("userId") String userId,
            @RequestParam("feedback") String feedback,
            @RequestParam("rating") String rating,
            @RequestParam(value = "photos", required = false) List<MultipartFile> photos)
            throws IOException, java.io.IOException {
        Optional<Flight> flightOpt = flightRepository.findById(flightId);
        if (flightOpt.isPresent()) {
            flightOpt.get().setTotalReviews(flightOpt.get().getTotalReviews() + 1);
            flightRepository.save(flightOpt.get());
            Review review = new Review();
            review.setCreatedAt(LocalTime.now().toString());
            review.setEntityType("flight");
            review.setRating(Integer.parseInt(rating));
            review.setEntityId(flightId);
            review.setFeedback(feedback);
            review.setFlagged(true);
            if (photos != null && !photos.isEmpty()) {
                List<Binary> photoList = new ArrayList<>();

                for (MultipartFile file : photos) {
                    Binary binary = new Binary(file.getBytes());
                    photoList.add(binary);
                }

                review.setPhotos(photoList);
                reviewRepository.save(review);
            }
            return review;
        } else {
            return null;
        }
    }

    @PostMapping("/hotels/{hotelId}/reviews")
    public Review addhotelReview(@PathVariable("hotelId") String hotelId,
            @RequestParam("userId") String userId,
            @RequestParam("feedback") String feedback,
            @RequestParam("rating") int rating,
            @RequestParam(value = "photos", required = false) List<MultipartFile> photos)
            throws IOException, java.io.IOException {
        Optional<Hotel> hotelOpt = hotelRepository.findById(hotelId);
        if (hotelOpt.isPresent()) {
            hotelRepository.save(hotelOpt.get());
            Review review = new Review();
            review.setCreatedAt(LocalTime.now().toString());
            review.setUserId(userId);
            review.setEntityType("hotel");
            review.setRating(rating);
            review.setEntityId(hotelId);
            review.setFeedback(feedback);
            review.setFlagged(true);
            if (photos != null && !photos.isEmpty()) {
                List<Binary> photoList = new ArrayList<>();
                for (MultipartFile file : photos) {
                    Binary binary = new Binary(file.getBytes());
                    photoList.add(binary);
                }
                review.setPhotos(photoList);
            }
            reviewRepository.save(review);
            return review;
        } else {
            return null;
        }
    }

    @PutMapping("/reviews/{id}/flag")
    public Review flagReview(@PathVariable String id, @RequestParam("flag") boolean flag) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setFlagged(flag);
            return reviewRepository.save(review);
        }
        return null;
    }

    @PostMapping("/reviews/{id}/reply")
    public Review addReply(@PathVariable("id") String id, @RequestBody Reply reply) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            List<Reply> replies = review.getReplies();
            if (replies == null) {
                replies = new ArrayList<>();
            }
            reply.setCreatedAt(LocalTime.now().toString());
            replies.add(reply);
            review.setReplies(replies);
            return reviewRepository.save(review);
        }
        return null;
    }

    @PutMapping("/addreply/{id}")
    public Review addreplytoreview(@RequestBody List<Reply> replies, @PathVariable("id") String id) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isPresent()) {
            review.get().setReplies(replies);
            reviewRepository.save(review.get());
            return review.get();
        }
        return null;
    }

}
