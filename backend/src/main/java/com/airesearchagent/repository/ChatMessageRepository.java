package com.airesearchagent.repository;

import com.airesearchagent.entity.ChatMessage;
import com.airesearchagent.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findTop30ByUserOrderByCreatedAtAsc(User user);
    long countByUser(User user);
}
