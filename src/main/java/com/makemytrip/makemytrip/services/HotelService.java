package com.makemytrip.makemytrip.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.makemytrip.makemytrip.repositories.HotelRepository;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

//     public List<String> getAllHotelImages(String hotelId) {
//     Hotel hotel = hotelRepository.findById(hotelId)
//         .orElseThrow(() -> new RuntimeException("Hotel not found"));

//     List<String> images = new ArrayList<>();

//     for (Room room : hotel.getRooms()) {
//         if (room.getImages() != null) {
//             images.addAll(room.getImages());
//         }
//     }

//     return images;
// }
    
}
