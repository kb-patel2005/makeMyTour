package com.makemytrip.makemytrip.services;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.HotelDto;
import com.makemytrip.makemytrip.models.RecommendationDTO;
import com.makemytrip.makemytrip.models.Room;
import com.makemytrip.makemytrip.models.RoomDto;
import com.makemytrip.makemytrip.models.UserPreference;
import com.makemytrip.makemytrip.repositories.FlightRepository;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import com.makemytrip.makemytrip.repositories.UserPreferenceRepository;

@Service
public class RecommandationService {

    @Autowired
    private UserPreferenceRepository prefRepo;
    @Autowired
    private FlightRepository flightRepo;
    @Autowired
    private HotelRepository hotelRepo;

    public List<RecommendationDTO> recommendFlights(String userId) {

        UserPreference pref = prefRepo.findByUserId(userId).orElse(null);
        if (pref == null)
            return List.of();

        List<String> topDestinations = pref.getDestinationScore()
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(e -> e.getKey().toLowerCase().trim())
                .limit(3)
                .toList();

        if (topDestinations.isEmpty())
            return List.of();

        List<Flight> flights = new ArrayList<>();

        for (String dest : topDestinations) {
            System.out.println("Top destination for user " + userId + ": " + dest);
            flights.addAll(flightRepo.findByToIgnoreCase(dest));
        }

        return flights.stream().map(f -> {
            RecommendationDTO dto = new RecommendationDTO();
            dto.setItem(f);
            dto.setReason("Based on your frequent destinations");
            return dto;
        }).toList();
    }

    public void feedback(String userId, String destination, boolean liked) {
        UserPreference pref = prefRepo.findByUserId(userId).orElseThrow();

        int change = liked ? 3 : -2;

        pref.getDestinationScore().put(
                destination,
                pref.getDestinationScore().getOrDefault(destination, 0) + change);

        prefRepo.save(pref);
    }

    public List<RecommendationDTO> recommendHotels(String userId) {

        UserPreference pref = prefRepo.findByUserId(userId).orElse(null);
        if (pref == null)
            return List.of();

        String topLocation = pref.getDestinationScore()
                .entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (topLocation == null)
            return List.of();

        List<Hotel> hotels = hotelRepo.findByLocationIgnoreCase(topLocation);

        List<RecommendationDTO> hotelDtos = new ArrayList<>();

        for (Hotel hotel : hotels) {

            RecommendationDTO dto = new RecommendationDTO();
            HotelDto hotelDto = new HotelDto();

            hotelDto.set_id(hotel.get_id());
            hotelDto.setHotelName(hotel.getHotelName());
            hotelDto.setDescription(hotel.getDescription());
            hotelDto.setLocation(hotel.getLocation());
            hotelDto.setRating(hotel.getRating());

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
                            imgs.add("data:image/jpeg;base64," +
                                    Base64.getEncoder().encodeToString(img));
                        }
                    }

                    r.setPhotos(imgs);
                    roomDtos.add(r);
                }
            }

            hotelDto.setRooms(roomDtos);

            dto.setItem(hotelDto);
            dto.setReason("You often travel to " + topLocation);

            hotelDtos.add(dto);
        }

        return hotelDtos;
    }

}
