package com.airesearchagent.controller;

import com.airesearchagent.dto.ChatDtos;
import com.airesearchagent.entity.User;
import com.airesearchagent.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ChatDtos.ChatResponse chat(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChatDtos.ChatRequest request
    ) {
        return chatService.sendMessage(user, request);
    }

    @GetMapping("/history")
    public List<ChatDtos.ChatMessageDto> history(@AuthenticationPrincipal User user) {
        return chatService.history(user);
    }
}
