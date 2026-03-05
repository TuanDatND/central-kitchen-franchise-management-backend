package com.CocOgreen.CenFra.MS.exception;

import com.CocOgreen.CenFra.MS.controller.CategoryController;
import com.CocOgreen.CenFra.MS.controller.ManufacturingOrderController;
import com.CocOgreen.CenFra.MS.controller.ProductController;
import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Trình xử lý ngoại lệ (Exception Handler) cục bộ, chỉ áp dụng riêng cho các
 * controller của Dev 2.
 * Điều này đảm bảo không can thiệp vào cách xử lý lỗi của Dev 1 và Dev 3.
 * Giới hạn phạm vi: CategoryController, ProductController,
 * ManufacturingOrderController.
 */
@RestControllerAdvice(assignableTypes = {
        CategoryController.class,
        ProductController.class,
        ManufacturingOrderController.class
})
public class Dev2ExceptionHandler {

    /**
     * Xử lý lỗi không tìm thấy tài nguyên (ResourceNotFoundException).
     * Trả về HTTP Status 404 (Not Found).
     * 
     * @param ex Ngoại lệ ResourceNotFoundException được ném ra.
     * @return ResponseEntity chứa ApiResponse định dạng chuẩn.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), "NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Xử lý lỗi xác thực dữ liệu đầu vào (Validation errors từ @Valid).
     * Trả về HTTP Status 400 (Bad Request) cùng với danh sách các trường bị lỗi.
     * 
     * @param ex Ngoại lệ MethodArgumentNotValidException khi dữ liệu không hợp lệ.
     * @return ResponseEntity chứa ApiResponse với thông tin validation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ApiResponse<Void> response = ApiResponse.error("Validation failed", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Xử lý tất cả các lỗi không mong muốn khác (Fallback).
     * Trả về HTTP Status 500 (Internal Server Error).
     * 
     * @param ex Bất kỳ Exception nào không được thiết lập cụ thể ở trên.
     * @return ResponseEntity chứa ApiResponse định dạng chuẩn.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        ApiResponse<Void> response = ApiResponse.error("Internal server error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
