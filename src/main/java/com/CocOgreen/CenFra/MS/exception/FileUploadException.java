package com.CocOgreen.CenFra.MS.exception;

/**
 * Lỗi tuỳ chỉnh khi upload file thất bại.
 */
public class FileUploadException extends RuntimeException {
    public FileUploadException(String message) {
        super(message);
    }
    
    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
