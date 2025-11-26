package org.example.foodanddrinkproject.repository.specification;

import org.example.foodanddrinkproject.entity.Product;
import org.example.foodanddrinkproject.enums.ProductType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Component
public class ProductSpecification {

    //Name (Partial match, Case-insensitive)
    public Specification<Product> hasName(String name) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(name)) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    //Brand (Partial match)
    public Specification<Product> hasBrand(String brand) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(brand)) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("brand")), "%" + brand.toLowerCase() + "%");
        };
    }

    //Category ID
    public Specification<Product> hasCategory(Integer categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    // Type (FOOD or DRINK)
    public Specification<Product> hasType(ProductType type) {
        return (root, query, cb) -> {
            if (type == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("productType"), type);
        };
    }

    // Price Range
    public Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice != null && maxPrice != null) {
                return cb.between(root.get("price"), minPrice, maxPrice);
            }
            if (minPrice != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            }
            if (maxPrice != null) {
                return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
            return cb.conjunction();
        };
    }

    // Rating
    public Specification<Product> ratingGreaterThanOrEqual(Double minRating) {
        return (root, query, cb) -> {
            if (minRating == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("avgRating"), minRating);
        };
    }

    // filter out inactive products for public view
    public Specification<Product> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("isActive"));
    }
}
