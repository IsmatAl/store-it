package com.example.storageit.rest.controller;

import com.example.storageit.service.auth.AuthenticationService;
import com.example.storageit.rest.dto.LoginRequest;
import com.example.storageit.rest.dto.RegistrationRequest;
import com.example.storageit.rest.dto.MessageResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        Long id = authenticationService.register(request).getId();
        return ResponseEntity.ok(new MessageResponse(
                String.format("User with id %d registered successfully!", id )));
    }


    @PostMapping(path = "login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.loginUser(request));
    }

}
