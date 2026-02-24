package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.request.ProductRequest;
import com.CocOgreen.CenFra.MS.dto.response.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; // Import để validation hoạt động
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product Management", description = "APIs quản lý danh mục sản phẩm (Dev 2)")
public class ProductController {

    @GetMapping
    @Operation(summary = "Lấy danh sách sản phẩm", description = "Trả về list sản phẩm giả để test UI")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        // MOCK DATA: Cập nhật đủ trường theo DTO ProductResponse
        // Thứ tự: productId, productName, unit, imageUrl, description, status, categoryName
        List<ProductResponse> mockList = Arrays.asList(
                new ProductResponse(1, "Thịt Bò Úc", "kg", "https://via.placeholder.com/150", "Thịt bò nhập khẩu cao cấp", "ACTIVE", "Thịt"),
                new ProductResponse(2, "Rau Cải Ngọt", "kg", "https://via.placeholder.com/150", "Rau sạch VietGAP", "ACTIVE", "Rau Củ"),
                new ProductResponse(3, "Sốt Tiêu Đen", "lít", "https://via.placeholder.com/150", "Sốt làm sẵn", "INACTIVE", "Gia Vị")
        );

        return ResponseEntity.ok(mockList);
    }

    @PostMapping
    @Operation(summary = "Tạo sản phẩm mới", description = "Nhận ProductRequest, validate và trả về object vừa tạo (Mock)")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {

        // MOCK LOGIC: Lấy dữ liệu từ Request đắp vào Response
        // Giả lập việc tìm tên Category từ ID (Vì request chỉ gửi categoryId)
        String mockCategoryName = "Danh mục (ID: " + request.getCategoryId() + ")";

        ProductResponse newProduct = new ProductResponse(
                99,                         // ID giả tự sinh
                request.getProductName(),   // Lấy tên từ request
                request.getUnit(),          // Lấy đơn vị từ request
                request.getImageUrl(),      // Lấy ảnh
                request.getDescription(),   // Lấy mô tả
                "ACTIVE",                   // Mặc định Active khi tạo
                mockCategoryName            // Tên danh mục giả
        );

        return ResponseEntity.ok(newProduct);
    }

    // Bổ sung thêm API lấy chi tiết (Frontend thường cần cái này khi click vào sửa)
    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết sản phẩm", description = "Lấy thông tin 1 món ăn theo ID")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Integer id) {
        // Trả về dữ liệu giả
        ProductResponse product = new ProductResponse(
                id,
                "Sản phẩm Demo ID " + id,
                "kg",
                "https://via.placeholder.com/150",
                "Mô tả chi tiết sản phẩm...",
                "ACTIVE",
                "Thịt"
        );
        return ResponseEntity.ok(product);
    }
}