package com.cognizant.Auth.controller;

import com.cognizant.Auth.exception.InvalidCredentialsException;
import com.cognizant.Auth.exception.UserAlreadyExistsException;
import com.cognizant.Auth.exception.UserNotFoundException;
import com.cognizant.Auth.model.ResponseDto;
import com.cognizant.Auth.model.TokenResponse;
import com.cognizant.Auth.model.UserDto;
import com.cognizant.Auth.service.AuthServiceImpl;
import com.cognizant.Auth.util.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "${tmem-ui}", allowCredentials = "true")
public class AuthController {


    @Autowired
    AuthServiceImpl authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody UserDto dto) throws InvalidCredentialsException, UserNotFoundException {

        try {
            ResponseDto responseDto = authService.validateUser(dto);
            String token = JWTService.generateToken(responseDto);

            return ResponseEntity.ok().body(new TokenResponse(token, responseDto.getId()));


        } catch (InvalidCredentialsException | UserNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody UserDto dto) {
        try {
            ResponseDto responseDto = authService.registerUser(dto);
            String token = JWTService.generateToken(responseDto);
            return ResponseEntity.ok().body(new TokenResponse(token, responseDto.getId()));

        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody String jwt){
        if(JWTService.parseToken(jwt)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
