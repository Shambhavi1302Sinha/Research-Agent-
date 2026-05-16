package com.airesearchagent.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "research_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String topic;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String summary;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String keyInsights;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String importantPoints;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String relatedTopics;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
