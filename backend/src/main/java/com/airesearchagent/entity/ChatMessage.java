package com.airesearchagent.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chats")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String role;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String message;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String prompt;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String response;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
