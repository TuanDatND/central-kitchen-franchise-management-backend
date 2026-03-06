package com.CocOgreen.CenFra.MS.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.CocOgreen.CenFra.MS.dto.ApiResponse;

import jakarta.persistence.EntityNotFoundException;

/**
 * Nơi xử lý tập trung (Global Exception Handler) cho toàn bộ Controller trong hệ thống.
 * Chuyển đổi các Exception Java thành cấu trúc chuẩn ApiResponse cho Frontend dễ xử lý.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bắt lỗi khi truy vấn không tìm thấy Entity trong database 
     * (thường do truyền sai ID hoặc dữ liệu đã bị xóa).
     * Trả về HTTP Status: 404 Not Found.
     *
     * @param ex Lỗi EntityNotFoundException ném ra từ Service
     * @return ResponseEntity chứa ApiResponse với success=false và kèm theo mã lỗi "ENTITY_NOT_FOUND" 
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFoundException(EntityNotFoundException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), "ENTITY_NOT_FOUND");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Bắt lỗi đặc thù thuộc nhóm nghiệp vụ Outbound (Xuất kho, Giao hàng, FEFO).
     * Trả về HTTP Status: 400 Bad Request.
     *
     * @param ex Lỗi InventoryOutboundException ném ra từ Service mảng Outbound (Dev 3)
     * @return ResponseEntity chứa ApiResponse với thông báo lỗi rõ ràng gửi về Frontend.
     */
    @ExceptionHandler(InventoryOutboundException.class)
    public ResponseEntity<ApiResponse<Void>> handleOutboundException(InventoryOutboundException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), "OUTBOUND_BUSINESS_ERROR");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Bắt tất cả các loại Exception không xác định khác (General Exception fallback).
     * Đây là lưới bảo vệ cuối cùng nếu lỗi chưa được bắt bởi các phương thức trên.
     * Trả về HTTP Status: 500 Internal Server Error.
     *
     * @param ex Lỗi chung Exception chưa được xử lý.
     * @return ResponseEntity thông báo có lỗi hệ thống bên trong.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex) {
        ApiResponse<Void> response = ApiResponse.error("Đã xảy ra lỗi hệ thống: " + ex.getMessage(), "INTERNAL_SERVER_ERROR");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
