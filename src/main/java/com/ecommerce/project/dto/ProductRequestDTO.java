package com.ecommerce.project.dto;

import com.ecommerce.project.entity.Product;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Map;

public record ProductRequestDTO(
        @NotBlank(message = "Product name is required")
        @Size(min = 3, max = 200, message = "Product name must be between 3 and 200 characters")
        String name,

        @NotBlank(message = "Product description is required")
        @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
        String description,

        @NotBlank(message = "Category ID is required")
        String categoryId,

        @Positive(message = "Base price must be positive")
        @DecimalMin(value = "0.01", message = "Base price must be at least 0.01")
        double price,

        // Size tier pricing - allows different prices for different size ranges
        Map<Product.SizeTier, Double> sizeTierPricing,

        // Available sizes for this product
        @NotEmpty(message = "At least one size must be available")
        List<Product.Size> availableSizes,

        @Min(value = 0, message = "Stock quantity cannot be negative")
        int stockQuantity,

        List<String> imageUrls,

        @Size(max = 50, message = "Color name must not exceed 50 characters")
        String color
) {}
