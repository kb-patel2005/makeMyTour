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
public class RoomDto {
    private String type;
    private String price;
    private String totalRooms;
    private List<String> amenities;
    private List<Boolean> availability; // ✅ track availability of each room
    private List<String> photos; // ✅ for frontend
}