package com.airesearchagent.service;

import com.airesearchagent.dto.DashboardDtos;
import com.airesearchagent.entity.User;
import com.airesearchagent.repository.ChatMessageRepository;
import com.airesearchagent.repository.ResearchHistoryRepository;
import com.airesearchagent.repository.UploadedFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ResearchHistoryRepository researchHistoryRepository;
    private final UploadedFileRepository uploadedFileRepository;
    private final ChatMessageRepository chatMessageRepository;

    public DashboardDtos.DashboardResponse getDashboard(User user) {
        long researchCount = researchHistoryRepository.countByUser(user);
        long uploadedCount = uploadedFileRepository.countByUser(user);
        long chatCount = chatMessageRepository.countByUser(user);

        List<String> recentTopics = researchHistoryRepository.findTop10ByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(item -> item.getTopic())
                .toList();

        List<String> uploadedDocuments = uploadedFileRepository.findTop10ByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(item -> item.getFileName())
                .toList();

        String snapshot = "You have %d research sessions, %d uploaded documents, and %d chat messages stored."
                .formatted(researchCount, uploadedCount, chatCount);

        return DashboardDtos.DashboardResponse.builder()
                .totalResearchCount(researchCount)
                .totalUploadedDocuments(uploadedCount)
                .totalChatMessages(chatCount)
                .recentTopics(recentTopics)
                .uploadedDocuments(uploadedDocuments)
                .activitySnapshot(snapshot)
                .build();
    }
}
