package com.CocOgreen.CenFra.MS.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interface chung xử lý upload file
 */
public interface FileUploadService {
    /**
     * Upload một MultipartFile lên dịch vụ đám mây (Cloudinary/S3/...)
     * @param file File ảnh được truyền từ client
     * @return URL bảo mật (secure url) của ảnh sau khi upload
     */
    String uploadFile(MultipartFile file);
}
