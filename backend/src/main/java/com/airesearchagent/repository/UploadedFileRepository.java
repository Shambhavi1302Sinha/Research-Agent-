package com.airesearchagent.repository;

import com.airesearchagent.entity.UploadedFile;
import com.airesearchagent.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
    List<UploadedFile> findTop10ByUserOrderByCreatedAtDesc(User user);
    long countByUser(User user);
}
