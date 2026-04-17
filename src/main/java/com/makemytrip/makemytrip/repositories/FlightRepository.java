package com.makemytrip.makemytrip.repositories;
import com.makemytrip.makemytrip.models.Flight;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightRepository  extends MongoRepository<Flight,String>{

    List<Flight> findByToIgnoreCase(String topDestination);

    List<Flight> findByFromIgnoreCaseAndToIgnoreCase(String from, String to);

}