package com.makemytrip.makemytrip.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelDto {
    private String _id;
    private String hotelName;
    private String description;
    private String country;
    private String state;
    private String location;
    private double rating;
    private List<RoomDto> rooms;
}