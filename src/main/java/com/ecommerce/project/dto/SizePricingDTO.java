package com.ecommerce.project.dto;

import com.ecommerce.project.entity.Product;

public record SizePricingDTO(
        Product.Size size,
        double price,
        Product.SizeTier tier,
        String tierDisplayName
) {
    
    public static SizePricingDTO fromSizeAndProduct(Product.Size size, ProductResponseDTO product) {
        Product.SizeTier tier = Product.SizeTier.getTierForSize(size);
        double price = product.getPriceForSize(size);
        
        return new SizePricingDTO(
            size,
            price,
            tier,
            tier.getDisplayName()
        );
    }
}