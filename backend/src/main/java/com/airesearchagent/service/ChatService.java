package com.airesearchagent.service;

import com.airesearchagent.dto.ChatDtos;
import com.airesearchagent.entity.ChatMessage;
import com.airesearchagent.entity.User;
import com.airesearchagent.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final AiClientService aiClientService;

    public ChatDtos.ChatResponse sendMessage(User user, ChatDtos.ChatRequest request) {
        chatMessageRepository.save(ChatMessage.builder()
                .user(user)
                .role("user")
                .message(request.message())
                .prompt(request.message())
                .response("")
                .createdAt(LocalDateTime.now())
                .build());

        String prompt = """
                You are an expert AI research assistant. Reply conversationally, but keep your answer practical,
                evidence-aware, and useful for someone doing research work.

                User message:
                %s
                """.formatted(request.message());

        String reply = aiClientService.generateText(prompt);
        ChatMessage assistantMessage = chatMessageRepository.save(ChatMessage.builder()
                .user(user)
                .role("assistant")
                .message(reply)
                .prompt(request.message())
                .response(reply)
                .createdAt(LocalDateTime.now())
                .build());

        List<ChatDtos.ChatMessageDto> history = chatMessageRepository.findTop30ByUserOrderByCreatedAtAsc(user).stream()
                .map(message -> ChatDtos.ChatMessageDto.builder()
                        .role(message.getRole())
                        .message(message.getMessage())
                        .createdAt(message.getCreatedAt())
                        .build())
                .toList();

        return ChatDtos.ChatResponse.builder()
                .reply(ChatDtos.ChatMessageDto.builder()
                        .role(assistantMessage.getRole())
                        .message(assistantMessage.getMessage())
                        .createdAt(assistantMessage.getCreatedAt())
                        .build())
                .history(history)
                .build();
    }

    public List<ChatDtos.ChatMessageDto> history(User user) {
        return chatMessageRepository.findTop30ByUserOrderByCreatedAtAsc(user).stream()
                .map(message -> ChatDtos.ChatMessageDto.builder()
                        .role(message.getRole())
                        .message(message.getMessage())
                        .createdAt(message.getCreatedAt())
                        .build())
                .toList();
    }
}
