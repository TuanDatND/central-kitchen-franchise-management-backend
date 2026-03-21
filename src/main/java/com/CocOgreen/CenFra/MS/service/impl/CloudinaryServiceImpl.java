package com.CocOgreen.CenFra.MS.service.impl;

import com.CocOgreen.CenFra.MS.exception.FileUploadException;
import com.CocOgreen.CenFra.MS.service.FileUploadService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Triển khai FileUploadService sử dụng Cloudinary.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements FileUploadService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new FileUploadException("File gửi lên không được trống.");
            }

            // Sinh random ID tránh trùng lặp tên file
            String publicId = UUID.randomUUID().toString();

            // Tiến hành upload lên Cloudinary
            log.info("Bắt đầu upload ảnh lên Cloudinary...");
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", "products"
            ));

            String secureUrl = (String) result.get("secure_url");
            log.info("Upload thành công. URL: {}", secureUrl);
            return secureUrl;
        } catch (IOException e) {
            log.error("Lỗi khi upload file lên Cloudinary", e);
            throw new FileUploadException("Có lỗi xảy ra khi xử lý file tải lên: " + e.getMessage(), e);
        }
    }
}
