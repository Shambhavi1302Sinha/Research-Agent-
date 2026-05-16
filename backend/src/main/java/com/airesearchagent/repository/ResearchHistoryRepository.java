package com.airesearchagent.repository;

import com.airesearchagent.entity.ResearchHistory;
import com.airesearchagent.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResearchHistoryRepository extends JpaRepository<ResearchHistory, Long> {
    List<ResearchHistory> findTop10ByUserOrderByCreatedAtDesc(User user);
    List<ResearchHistory> findByUserOrderByCreatedAtDesc(User user);
    long countByUser(User user);
}
