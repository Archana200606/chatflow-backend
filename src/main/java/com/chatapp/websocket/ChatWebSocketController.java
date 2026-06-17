package com.chatapp.websocket;

import com.chatapp.dto.Dtos.*;
import com.chatapp.model.Chat;
import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.repository.ChatRepository;
import com.chatapp.repository.MessageRepository;
import com.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    @MessageMapping("/chat.send")
    @Transactional
    public void sendMessage(SendMessageRequest request, Principal principal) {
        String email = principal.getName();
        User sender = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Chat chat = chatRepository.findById(request.getChatId())
            .orElseThrow(() -> new RuntimeException("Chat not found"));

        // Validate sender is participant
        boolean isParticipant = chat.getParticipants().stream()
            .anyMatch(p -> p.getId().equals(sender.getId()));
        if (!isParticipant) {
            throw new RuntimeException("Not a participant of this chat");
        }

        Message message = Message.builder()
            .sender(sender)
            .chat(chat)
            .content(request.getContent())
            .timestamp(LocalDateTime.now())
            .build();

        messageRepository.save(message);

        MessageDto dto = MessageDto.from(message);
        messagingTemplate.convertAndSend("/topic/chat/" + chat.getId(), dto);
    }

    @MessageMapping("/chat.typing")
    @Transactional
    public void typing(TypingRequest request, Principal principal) {
        String email = principal.getName();
        User sender = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        MessageDto typingDto = MessageDto.builder()
            .type("TYPING")
            .chatId(request.getChatId())
            .senderId(sender.getId())
            .senderName(sender.getUsername())
            .build();

        messagingTemplate.convertAndSend("/topic/chat/" + request.getChatId(), typingDto);
    }
}
