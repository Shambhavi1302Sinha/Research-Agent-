package com.airesearchagent.repository;

import com.airesearchagent.entity.Citation;
import com.airesearchagent.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitationRepository extends JpaRepository<Citation, Long> {
    List<Citation> findTop10ByUserOrderByCreatedAtDesc(User user);
}
