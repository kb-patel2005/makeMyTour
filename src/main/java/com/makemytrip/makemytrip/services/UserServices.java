package com.makemytrip.makemytrip.services;

import com.makemytrip.makemytrip.config.Jwtutils;
import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.repositories.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServices {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Jwtutils jwtutils;

    public ResponseEntity<String> signup(Users user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        userRepository.save(user);
        String token = jwtutils.generateToken(user);
        return ResponseEntity.ok(token);
    }

    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Users editprofile(String id, Users updatedUser) {
        Users user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setPhoneNumber(updatedUser.getPhoneNumber());
            return userRepository.save(user);
        }
        return null;
    }

    public Optional<Users> findById(String id) {
        return userRepository.findById(id);
    }

}