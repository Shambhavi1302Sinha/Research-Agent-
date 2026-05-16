package com.airesearchagent.dto;

import lombok.Builder;

import java.util.List;

public class DashboardDtos {

    @Builder
    public record DashboardResponse(
            long totalResearchCount,
            long totalUploadedDocuments,
            long totalChatMessages,
            List<String> recentTopics,
            List<String> uploadedDocuments,
            String activitySnapshot
    ) {}
}
