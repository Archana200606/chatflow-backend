package com.chatapp.controller;

import com.chatapp.dto.Dtos.UserDto;
import com.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(
            userRepository.findAll().stream().map(UserDto::from).collect(Collectors.toList())
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(
            userRepository.searchUsers(query).stream().map(UserDto::from).collect(Collectors.toList())
        );
    }
}
