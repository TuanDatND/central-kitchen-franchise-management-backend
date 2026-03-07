package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.request.ProductRequest;
import com.CocOgreen.CenFra.MS.dto.response.ProductResponse;
import com.CocOgreen.CenFra.MS.entity.Category;
import com.CocOgreen.CenFra.MS.entity.Product;
import com.CocOgreen.CenFra.MS.enums.ProductStatus;
import com.CocOgreen.CenFra.MS.mapper.ProductMapper;
import com.CocOgreen.CenFra.MS.repository.CategoryRepository;
import com.CocOgreen.CenFra.MS.repository.ProductRepository;
import com.CocOgreen.CenFra.MS.repository.UnitRepository;
import com.CocOgreen.CenFra.MS.entity.Unit;
import com.CocOgreen.CenFra.MS.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UnitRepository unitRepository;
    private final ProductMapper productMapper;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return productMapper.toResponse(product);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + request.getUnitId()));

        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setUnit(unit);
        product.setStatus(ProductStatus.ACTIVE); // Default status

        product = productRepository.save(product);
        return productMapper.toResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Integer id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + request.getUnitId()));

        productMapper.updateProduct(product, request);
        product.setCategory(category);
        product.setUnit(unit);

        product = productRepository.save(product);
        return productMapper.toResponse(product);
    }

    @Transactional
    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setStatus(ProductStatus.INACTIVE); // đánh dấu ngưng hoạt động
    }
}
