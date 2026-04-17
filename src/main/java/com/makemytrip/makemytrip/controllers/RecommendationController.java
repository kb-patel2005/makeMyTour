package com.makemytrip.makemytrip.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.makemytrip.makemytrip.models.RecommendationDTO;
import com.makemytrip.makemytrip.services.RecommandationService;

@RestController
@RequestMapping("/recommend")
public class RecommendationController {

    @Autowired
    private RecommandationService service;

    @GetMapping("/flights")
    public List<RecommendationDTO> getFlight() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) auth.getPrincipal();
        return service.recommendFlights(userId);
    }

    @GetMapping("/hotels")
    public List<RecommendationDTO> getHotel() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) auth.getPrincipal();
        return service.recommendHotels(userId);
    }

}
