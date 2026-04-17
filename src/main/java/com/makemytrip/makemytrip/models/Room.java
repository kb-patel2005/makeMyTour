package com.makemytrip.makemytrip.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    private String type;
    private String price;
    private String totalRooms;
    private List<String> amenities;
    private List<Boolean> availability; // ✅ track availability of each room
    @JsonIgnore 
    private List<byte[]> images;
}
