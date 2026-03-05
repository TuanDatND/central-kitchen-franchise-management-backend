package com.CocOgreen.CenFra.MS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lớp bọc phản hồi API (API Response Wrapper) chuẩn hóa cho hệ thống.
 * Cung cấp định dạng JSON thống nhất cho cả trường hợp thành công và thất bại.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private Object error;

    /**
     * Tạo phản hồi thành công.
     * 
     * @param data    Dữ liệu trả về.
     * @param message Thông báo mô tả thành công.
     * @return ApiResponse với success=true.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .error(null)
                .build();
    }

    /**
     * Tạo phản hồi lỗi.
     * 
     * @param message      Thông báo lỗi cho người dùng.
     * @param errorDetails Chi tiết lỗi (có thể là chuỗi hoặc object map chứa các
     *                     lỗi validation).
     * @return ApiResponse với success=false.
     */
    public static <T> ApiResponse<T> error(String message, Object errorDetails) {
        return ApiResponse.<T>builder()
                .success(false)
                .data(null)
                .message(message)
                .error(errorDetails)
                .build();
    }
}
