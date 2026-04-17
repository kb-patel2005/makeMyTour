package com.makemytrip.makemytrip.repositories;
import com.makemytrip.makemytrip.models.Hotel;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends MongoRepository<Hotel,String>{

    List<Hotel> findByLocationIgnoreCase(String location);

}