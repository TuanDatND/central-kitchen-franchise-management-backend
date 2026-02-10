package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.response.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product Management", description = "APIs quản lý danh mục sản phẩm (Quang)")
public class ProductController {

    @GetMapping
    @Operation(summary = "Lấy danh sách sản phẩm", description = "Trả về list sản phẩm giả để test UI")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        // MOCK DATA: Tạo list giả
        List<ProductResponse> mockList = Arrays.asList(
                new ProductResponse(1, "Thịt Bò Úc", "kg", "Thịt", "ACTIVE", "https://via.placeholder.com/150"),
                new ProductResponse(2, "Rau Cải Ngọt", "kg", "Rau Củ", "ACTIVE", "https://via.placeholder.com/150"),
                new ProductResponse(3, "Sốt Tiêu Đen", "lít", "Gia Vị", "INACTIVE", "https://via.placeholder.com/150")
        );

        return ResponseEntity.ok(mockList);
    }

    @PostMapping
    @Operation(summary = "Tạo sản phẩm mới", description = "Test API tạo mới (chưa lưu DB)")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody Object request) {
        // Giả vờ tạo thành công và trả về chính nó kèm ID mới
        ProductResponse mockNewProduct = new ProductResponse(99, "Món Mới Test", "hộp", "Đồ Khô", "ACTIVE", null);
        return ResponseEntity.ok(mockNewProduct);
    }
}