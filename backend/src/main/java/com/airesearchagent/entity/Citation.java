package com.airesearchagent.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "citations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Citation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String sourceTitle;

    @Column(nullable = false)
    private String style;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String generatedCitation;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
