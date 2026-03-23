package com.ecommerce.project.dto;

import java.util.List;

public record ProductVariantDTO(
        String variantId,
        String colorName,
        String colorCode,
        List<String> imageUrls,
        int stockQuantity
) {}
