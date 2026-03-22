package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.request.CategoryRequest;
import com.CocOgreen.CenFra.MS.dto.response.CategoryResponse;
import com.CocOgreen.CenFra.MS.entity.Category;
import com.CocOgreen.CenFra.MS.enums.CategoryStatus;
import com.CocOgreen.CenFra.MS.mapper.CategoryMapper;
import com.CocOgreen.CenFra.MS.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CocOgreen.CenFra.MS.entity.Product;
import com.CocOgreen.CenFra.MS.enums.ProductStatus;
import com.CocOgreen.CenFra.MS.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        if (CategoryStatus.INACTIVE.equals(category.getStatus())) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        category.setStatus(CategoryStatus.ACTIVE);
        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(Integer id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        category.setCategoryName(request.getCategoryName());
        
        // Nếu người dùng có gửi kèm trạng thái mới thì cập nhật
        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
            
            // Nếu trạng thái mới là INACTIVE, ta vô hiệu hóa tất cả sản phẩm thuộc danh mục này
            if (CategoryStatus.INACTIVE.equals(request.getStatus())) {
                deactivateProductsByCategory(category);
            }
        }
        
        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        if (CategoryStatus.INACTIVE.equals(category.getStatus())) {
            throw new RuntimeException("Category not found with id: " + id);
        }

        category.setStatus(CategoryStatus.INACTIVE);
        deactivateProductsByCategory(category);
        categoryRepository.save(category);
    }

    private void deactivateProductsByCategory(Category category) {
        List<Product> products = category.getProducts();
        if (products != null && !products.isEmpty()) {
            for (Product product : products) {
                product.setStatus(ProductStatus.INACTIVE);
            }
            productRepository.saveAll(products);
        }
    }
}
