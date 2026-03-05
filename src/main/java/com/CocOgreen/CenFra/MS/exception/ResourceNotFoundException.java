package com.CocOgreen.CenFra.MS.exception;

/**
 * Exception tùy chỉnh được ném ra khi không tìm thấy tài nguyên.
 * Đặc biệt dùng cho các thao tác truy vấn dữ liệu theo ID không tồn tại.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Khởi tạo ResourceNotFoundException với thông báo lỗi chi tiết.
     * 
     * @param message Thông báo mô tả tài nguyên không được tìm thấy.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
