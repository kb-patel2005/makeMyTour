package com.makemytrip.makemytrip.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.makemytrip.makemytrip.models.Flight;

@Service
public class FlightService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendFlightUpdate(Flight flight) {
        messagingTemplate.convertAndSend("/topic/flights", flight);
    }

}
