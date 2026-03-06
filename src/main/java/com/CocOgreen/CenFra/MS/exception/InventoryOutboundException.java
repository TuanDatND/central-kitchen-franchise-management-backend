package com.CocOgreen.CenFra.MS.exception;

/**
 * Exception dùng để xử lý các lỗi nghiệp vụ riêng biệt cho module Outbound (Xuất kho & Giao hàng).
 * Giúp GlobalExceptionHandler có thể phân biệt và ưu tiên bắt lỗi này để trả về HTTP status 400 (Bad Request).
 */
public class InventoryOutboundException extends RuntimeException {
    
    /**
     * Khởi tạo ngoại lệ với thông báo lỗi cụ thể
     *
     * @param message Thông báo mô tả nguyên nhân lỗi nghiệp vụ (ví dụ: "Kho không đủ hàng", "Phiếu xuất đã giao")
     */
    public InventoryOutboundException(String message) {
        super(message);
    }
}
