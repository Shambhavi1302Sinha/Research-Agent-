package com.airesearchagent.service;

import com.airesearchagent.dto.FileUploadDtos;
import com.airesearchagent.entity.UploadedFile;
import com.airesearchagent.entity.User;
import com.airesearchagent.exception.ApiException;
import com.airesearchagent.repository.UploadedFileRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FileService {

    private final UploadedFileRepository uploadedFileRepository;
    private final AiClientService aiClientService;

    public FileUploadDtos.FileUploadResponse uploadPdf(User user, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException("Please upload a PDF file.");
        }
        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new ApiException("Only PDF uploads are supported.");
        }

        String extractedText = extractText(file);
        String preview = extractedText.length() > 1000 ? extractedText.substring(0, 1000) : extractedText;
        String prompt = """
                Summarize the following PDF content for a researcher.
                Include the central thesis, methods or structure if visible, and practical takeaways.

                Content:
                %s
                """.formatted(preview);

        String summary = aiClientService.generateText(prompt);
        UploadedFile saved = uploadedFileRepository.save(UploadedFile.builder()
                .user(user)
                .fileName(file.getOriginalFilename())
                .storagePath("memory://" + file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .legacySize(file.getSize())
                .extractedText(extractedText)
                .summary(summary)
                .createdAt(LocalDateTime.now())
                .build());

        return FileUploadDtos.FileUploadResponse.builder()
                .id(saved.getId())
                .fileName(saved.getFileName())
                .summary(saved.getSummary())
                .extractedTextPreview(preview)
                .createdAt(saved.getCreatedAt())
                .build();
    }

    private String extractText(MultipartFile file) {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            return new PDFTextStripper().getText(document).trim();
        } catch (IOException exception) {
            throw new ApiException("Could not read the uploaded PDF.");
        }
    }
}
