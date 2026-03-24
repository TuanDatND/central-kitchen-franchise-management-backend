package com.CocOgreen.CenFra.MS.service.impl;

import com.CocOgreen.CenFra.MS.dto.UploadedFileResult;
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
    public UploadedFileResult uploadFileWithMetadata(MultipartFile file, String folder) {
        try {
            if (file.isEmpty()) {
                throw new FileUploadException("File gửi lên không được trống.");
            }

            String publicIdSeed = UUID.randomUUID().toString();

            log.info("Bắt đầu upload ảnh lên Cloudinary vào folder {}...", folder);
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "public_id", publicIdSeed,
                    "folder", folder
            ));

            String secureUrl = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");
            log.info("Upload thành công. URL: {}", secureUrl);
            return new UploadedFileResult(secureUrl, publicId);
        } catch (IOException e) {
            log.error("Lỗi khi upload file lên Cloudinary", e);
            throw new FileUploadException("Có lỗi xảy ra khi xử lý file tải lên: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String publicId) {
        try {
            if (publicId == null || publicId.isBlank()) {
                return;
            }
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            log.error("Lỗi khi xóa file khỏi Cloudinary", e);
            throw new FileUploadException("Có lỗi xảy ra khi xóa file trên Cloudinary: " + e.getMessage(), e);
        }
    }
}
