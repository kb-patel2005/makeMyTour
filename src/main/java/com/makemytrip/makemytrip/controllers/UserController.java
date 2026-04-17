package com.makemytrip.makemytrip.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.services.UserServices;
import java.util.Optional;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServices userServices;

    @GetMapping("/getUser")
    public ResponseEntity<Users> getuserbyemail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication);
        Optional<Users> user = userServices.findById((String) authentication.getPrincipal());
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editProfile(@RequestParam("id") String id,
                                         @RequestBody Users updatedUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        String currentUserEmail = authentication.getName();
        Optional<Users> existingUser = userServices.findById(id);
        if (existingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (!existingUser.get().getEmail().equals(currentUserEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only edit your own profile");
        }
        Users savedUser = userServices.editprofile(id, updatedUser);
        return ResponseEntity.ok(savedUser);
    }

}