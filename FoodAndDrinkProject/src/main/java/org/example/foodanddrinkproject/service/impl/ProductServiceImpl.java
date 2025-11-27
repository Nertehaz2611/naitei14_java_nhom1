package org.example.foodanddrinkproject.service.impl;

import org.example.foodanddrinkproject.dto.ProductDto;
import org.example.foodanddrinkproject.enums.ProductType;
import org.example.foodanddrinkproject.exception.ResourceNotFoundException;
import org.example.foodanddrinkproject.entity.Product;
import org.example.foodanddrinkproject.repository.ProductRepository;
import org.example.foodanddrinkproject.repository.specification.ProductSpecification;
import org.example.foodanddrinkproject.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductSpecification productSpecification;

    public ProductServiceImpl(ProductRepository productRepository, ProductSpecification productSpecification) {
        this.productSpecification = productSpecification;
        this.productRepository = productRepository;
    }
    @Override
    public Page<ProductDto> getAllProducts(
            String name, String brand, Integer categoryId, ProductType type,
            BigDecimal minPrice, BigDecimal maxPrice, Double minRating,
            Pageable pageable
            ) {
        Specification<Product> spec =
                        productSpecification.isActive()
                        .and(productSpecification.hasName(name))
                        .and(productSpecification.hasBrand(brand))
                        .and(productSpecification.hasCategory(categoryId))
                        .and(productSpecification.hasType(type))
                        .and(productSpecification.priceBetween(minPrice, maxPrice))
                        .and(productSpecification.ratingGreaterThanOrEqual(minRating));

        Page<Product> productsPage = productRepository.findAll(spec, pageable);
        return productsPage.map(this::convertToDto);
    }

    @Override
    public ProductDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return convertToDto(product);
    }

    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setDiscountPrice(product.getDiscountPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setSku(product.getSku());
        dto.setBrand(product.getBrand());
        dto.setWeight(product.getWeight());
        dto.setActive(product.isActive());
        dto.setProductType(product.getProductType().name());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setAvgRating(product.getAvgRating());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        return dto;
    }
}
