package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.response.CloudinaryResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    CloudinaryResponse uploadFile(final MultipartFile file, final String fileName);

    void deleteImage(String publicId);
}
