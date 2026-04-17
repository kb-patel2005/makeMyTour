package com.makemytrip.makemytrip.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.UserPreference;
import com.makemytrip.makemytrip.repositories.UserPreferenceRepository;

@Service
public class PreferenceService {

    @Autowired
    private UserPreferenceRepository repo;

    public void updatePreference(String userId, Flight flight, String actionType) {
        int weight = switch (actionType) {
            case "BOOK" -> 5;
            case "SEARCH" -> 2;
            default -> 1;
        };
        UserPreference pref = repo.findByUserId(userId)
                .orElseGet(() -> {
                    UserPreference p = new UserPreference();
                    p.setUserId(userId);
                    return p;
                });
        String destination = flight.getTo();
        pref.getDestinationScore().put(
                destination,
                pref.getDestinationScore().getOrDefault(destination, 0) + weight);
        String route = flight.getFrom() + "-" + flight.getTo();
        pref.getRouteScore().put(
                route,
                pref.getRouteScore().getOrDefault(route, 0) + weight);
        pref.setLastUpdated(LocalDateTime.now());
        repo.save(pref);
    }

    public void feedback(String userId, String destination, boolean liked) {
        UserPreference pref = repo.findByUserId(userId).orElseThrow();
        int change = liked ? 3 : -2;
        pref.getDestinationScore().put(
                destination,
                pref.getDestinationScore().getOrDefault(destination, 0) + change);

        repo.save(pref);
    }

    public void updateHotelPreference(String userId, Hotel hotel, String actionType) {
        int weight = switch (actionType) {
            case "BOOK" -> 5;
            case "SEARCH" -> 2;
            case "VIEW" -> 1;
            default -> 1;
        };
        if (hotel != null) {
            UserPreference pref = repo.findByUserId(userId)
                    .orElseGet(() -> {
                        UserPreference p = new UserPreference();
                        p.setUserId(userId);
                        return p;
                    });

            String location = hotel.getLocation();
            pref.getDestinationScore().put(
                    location,
                    pref.getDestinationScore().getOrDefault(location, 0) + weight);
            pref.getHotelScore().put(
                    hotel.get_id(),
                    pref.getHotelScore().getOrDefault(hotel.get_id(), 0) + weight);
            pref.setLastUpdated(LocalDateTime.now());
            repo.save(pref);
        } else {

        }
    }

    public void hotelFeedback(String userId, String location, boolean liked) {
        UserPreference pref = repo.findByUserId(userId).orElseThrow();
        int change = liked ? 3 : -2;
        pref.getDestinationScore().put(
                location,
                pref.getDestinationScore().getOrDefault(location, 0) + change);
        repo.save(pref);
    }

    

}
