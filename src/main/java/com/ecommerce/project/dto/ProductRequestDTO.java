package com.ecommerce.project.dto;

import com.ecommerce.project.entity.Product;
import java.util.List;

public record ProductRequestDTO(
        String name,
        String description,
        String categoryId,
        double price,
        int stockQuantity,
        List<String> imageUrls,
        Product.Size size,
        String color
) {}
