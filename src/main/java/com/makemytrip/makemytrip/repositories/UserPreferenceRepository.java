package com.makemytrip.makemytrip.repositories;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.makemytrip.makemytrip.models.UserPreference;

public interface UserPreferenceRepository 
        extends MongoRepository<UserPreference, String> {
    Optional<UserPreference> findByUserId(String userId);
}
