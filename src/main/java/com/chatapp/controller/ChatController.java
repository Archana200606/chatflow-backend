package com.chatapp.controller;

import com.chatapp.dto.Dtos.ChatDto;
import com.chatapp.dto.Dtos.CreateChatRequest;
import com.chatapp.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatDto>> getMyChats(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chatService.getChatsForUser(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<ChatDto> createOrGetChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateChatRequest request) {
        return ResponseEntity.ok(chatService.createOrGetDirectChat(userDetails.getUsername(), request));
    }
}
