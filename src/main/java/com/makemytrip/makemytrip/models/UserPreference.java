package com.makemytrip.makemytrip.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "user_preferences")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPreference {

    @Id
    private String id;

    private String userId;

    private Map<String, Integer> destinationScore = new HashMap<>();
    private Map<String, Integer> routeScore = new HashMap<>();
    private Map<String, Integer> hotelScore = new HashMap<>();

    private LocalDateTime lastUpdated;
}
