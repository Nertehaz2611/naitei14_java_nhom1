package org.example.foodanddrinkproject.service;

import org.example.foodanddrinkproject.dto.ProductDto;
import org.example.foodanddrinkproject.enums.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

public interface ProductService {
    Page<ProductDto> getAllProducts(
            String name, String brand, Integer categoryId, ProductType type,
            BigDecimal minPrice, BigDecimal maxPrice, Double minRating,
            Pageable pageable
    );

    ProductDto getProductById(Long productId);
}
