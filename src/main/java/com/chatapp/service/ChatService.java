package com.chatapp.service;

import com.chatapp.dto.Dtos.ChatDto;
import com.chatapp.dto.Dtos.CreateChatRequest;
import com.chatapp.model.Chat;
import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.repository.ChatRepository;
import com.chatapp.repository.MessageRepository;
import com.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Transactional(readOnly = true)
    public List<ChatDto> getChatsForUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        List<Chat> chats = chatRepository.findByParticipant(user);

        return chats.stream().map(chat -> {
            ChatDto dto = ChatDto.from(chat);
            List<Message> msgs = messageRepository.findByChatIdOrderByTimestampAsc(chat.getId());
            if (!msgs.isEmpty()) {
                Message last = msgs.get(msgs.size() - 1);
                dto.setLastMessage(last.getContent());
                dto.setLastMessageTime(last.getTimestamp());
            }
            return dto;
        })
                .sorted((a, b) -> {
                    LocalDateTime t1 = a.getLastMessageTime();
                    LocalDateTime t2 = b.getLastMessageTime();
                    if (t1 == null && t2 == null) return 0;
                    if (t1 == null) return 1;
                    if (t2 == null) return -1;
                    return t2.compareTo(t1);
                })
        .collect(Collectors.toList());
    }

    @Transactional
    public ChatDto createOrGetDirectChat(String email, CreateChatRequest request) {
        User currentUser = userRepository.findByEmail(email).orElseThrow();
        User otherUser = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if a direct chat already exists
        Optional<Chat> existing = chatRepository.findDirectChat(currentUser, otherUser);
        if (existing.isEmpty()) {
            existing = chatRepository.findDirectChat(otherUser, currentUser);
        }

        if (existing.isPresent()) {
            return ChatDto.from(existing.get());
        }

        Chat chat = Chat.builder()
            .chatType(Chat.ChatType.DIRECT)
            .participants(Set.of(currentUser, otherUser))
            .build();

        chatRepository.save(chat);
        return ChatDto.from(chat);
    }
}
