package com.chatapp.controller;

import com.chatapp.dto.Dtos.MessageDto;
import com.chatapp.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageRepository messageRepository;

    @GetMapping("/{chatId}")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long chatId) {
        List<MessageDto> messages = messageRepository
            .findByChatIdOrderByTimestampAsc(chatId)
            .stream()
            .map(MessageDto::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(messages);
    }
}
