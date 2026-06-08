package com.backend.domain.product.product.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductImageStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final Path uploadPath = Path.of("uploads", "product-images");

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalStateException("업로드할 이미지 파일을 선택해주세요.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalStateException("jpg, png, webp, gif 이미지만 업로드할 수 있습니다.");
        }

        try {
            Files.createDirectories(uploadPath);

            String extension = getExtension(contentType);
            String fileName = UUID.randomUUID() + extension;
            Path target = uploadPath.resolve(fileName).normalize();

            file.transferTo(target);

            return "/uploads/product-images/" + fileName;
        } catch (IOException e) {
            throw new IllegalStateException("이미지 파일 저장에 실패했습니다.", e);
        }
    }

    private String getExtension(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
    }
}
