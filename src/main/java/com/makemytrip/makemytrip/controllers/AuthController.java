package com.makemytrip.makemytrip.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.makemytrip.makemytrip.config.Jwtutils;
import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.services.UserServices;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private Jwtutils jwtutils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserServices userServices;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam("email") String email, @RequestParam("password") String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            Users user = userServices.getUserByEmail(email);
            String token = jwtutils.generateToken(user);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(token);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Users user) {
        return userServices.signup(user);
    }

}
