package com.makemytrip.makemytrip.models;

import java.util.List;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "reviews")
@Data
public class Review {

    @Id
    private String id;
    private String userId;
    private String entityId; // hotelId OR flightId
    private String entityType; // "HOTEL" or "FLIGHT"
    private String feedback;
    private String userName;
    private int rating;
    private List<Binary> photos;
    private List<Reply> replies;
    private int helpfulCount;
    private boolean flagged;
    private String createdAt;

    // getters/setters
}