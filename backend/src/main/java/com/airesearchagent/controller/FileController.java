package com.airesearchagent.controller;

import com.airesearchagent.dto.FileUploadDtos;
import com.airesearchagent.entity.User;
import com.airesearchagent.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public FileUploadDtos.FileUploadResponse upload(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file
    ) {
        return fileService.uploadPdf(user, file);
    }
}
