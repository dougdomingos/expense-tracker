package com.dougdomingos.expensetracker.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dougdomingos.expensetracker.dto.user.CreateNewUserDTO;
import com.dougdomingos.expensetracker.dto.user.LoginRequestDTO;
import com.dougdomingos.expensetracker.dto.user.LoginResponseDTO;
import com.dougdomingos.expensetracker.dto.user.UserResponseDTO;
import com.dougdomingos.expensetracker.services.user.UserService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @Transactional
    public ResponseEntity<LoginResponseDTO> createNewUser(@RequestBody @Valid CreateNewUserDTO userDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createNewUser(userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.login(loginDTO));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> listAllUsers() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.listUsers());
    }

}
