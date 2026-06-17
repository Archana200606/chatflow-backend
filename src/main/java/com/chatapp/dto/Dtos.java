package com.chatapp.dto;

import com.chatapp.model.Chat;
import com.chatapp.model.Message;
import com.chatapp.model.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Dtos {

    // ── Auth ──────────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AuthResponse {
        private String token;
        private UserDto user;
    }

    // ── User ──────────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UserDto {
        private Long id;
        private String username;
        private String email;
        private String status;

        public static UserDto from(User user) {
            return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .build();
        }
    }

    // ── Chat ──────────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class CreateChatRequest {
        private Long userId;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ChatDto {
        private Long id;
        private String chatType;
        private String name;
        private List<UserDto> participants;
        private String lastMessage;
        private LocalDateTime lastMessageTime;

        public static ChatDto from(Chat chat) {
            return ChatDto.builder()
                .id(chat.getId())
                .chatType(chat.getChatType().name())
                .name(chat.getName())
                .participants(chat.getParticipants().stream()
                    .map(UserDto::from)
                    .collect(Collectors.toList()))
                .build();
        }
    }

    // ── Message ───────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MessageDto {
        private Long id;
        private Long chatId;
        private Long senderId;
        private String senderName;
        private String content;
        private LocalDateTime timestamp;
        private String type; // MESSAGE or TYPING

        public static MessageDto from(Message message) {
            return MessageDto.builder()
                .id(message.getId())
                .chatId(message.getChat().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getUsername())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .type("MESSAGE")
                .build();
        }
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class SendMessageRequest {
        private Long chatId;
        private String content;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class TypingRequest {
        private Long chatId;
    }
}
