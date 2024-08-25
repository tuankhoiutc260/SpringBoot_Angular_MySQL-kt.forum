package com.tuankhoi.backend.service.Impl;

import com.cloudinary.Cloudinary;
import com.tuankhoi.backend.dto.response.CloudinaryResponse;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public CloudinaryResponse uploadFile(MultipartFile file, String fileName) {
        try {
            final Map result = this.cloudinary.uploader()
                    .upload(file.getBytes(), Map.of("public_id", "kt-forum/" + fileName));

            final String url = (String) result.get("secure_url");
            final String publicId = (String) result.get("public_id");

            return CloudinaryResponse.builder().publicId(publicId).url(url).build();
        } catch (final Exception e) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }
    }

    @Override
    public void deleteImage(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, Map.of());
            String status = (String) result.get("result");
            if (!"ok".equals(status)) {
                throw new AppException(ErrorCode.DELETE_FAILED);
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.DELETE_FAILED);
        }
    }
}
