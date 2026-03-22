package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.UploadedFileResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface chung xử lý upload file
 */
public interface FileUploadService {
    /**
     * Upload một MultipartFile lên dịch vụ đám mây (Cloudinary/S3/...)
     * vào folder mặc định cho product.
     *
     * @param file File ảnh được truyền từ client
     * @return URL bảo mật (secure url) của ảnh sau khi upload
     */
    default String uploadFile(MultipartFile file) {
        return uploadFileWithMetadata(file, "products").getSecureUrl();
    }

    /**
     * Upload file vào folder chỉ định và trả về đầy đủ metadata để quản lý ảnh.
     *
     * @param file File ảnh được truyền từ client
     * @param folder tên thư mục lưu ảnh
     * @return metadata của file sau khi upload
     */
    UploadedFileResult uploadFileWithMetadata(MultipartFile file, String folder);

    /**
     * Xóa file khỏi cloud provider theo public id.
     */
    void deleteFile(String publicId);
}
