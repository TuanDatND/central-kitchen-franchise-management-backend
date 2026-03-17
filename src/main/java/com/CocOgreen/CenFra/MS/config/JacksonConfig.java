package com.CocOgreen.CenFra.MS.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Cấu hình Jackson ObjectMapper cho hệ thống.
 * Việc định nghĩa Bean ở đây đảm bảo Spring có thể inject ObjectMapper vào DeliveryService
 * và các service khác mà không gây lỗi "Bean not found".
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Đăng ký module để hỗ trợ Java 8 Time (LocalDate, OffsetDateTime...)
        mapper.registerModule(new JavaTimeModule());
        
        // Không viết các ngày dưới dạng timestamps (ISO format)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }
}
