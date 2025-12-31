package com.ecommerce.project.dto;

import com.ecommerce.project.entity.Product;
import java.util.List;
import java.util.Map;

public record ProductResponseDTO(
        String id,
        String name,
        String description,
        double price, // Base price
        Map<Product.SizeTier, Double> sizeTierPricing, // Tier-based pricing
        List<Product.Size> availableSizes, // Available sizes
        int stockQuantity,
        String categoryId,
        String color,
        List<String> imageUrls
) {
    
    // Helper method to get formatted pricing display
    public String getPricingDisplay() {
        if (sizeTierPricing == null || sizeTierPricing.isEmpty()) {
            return String.format("₹%.2f", price);
        }
        
        StringBuilder display = new StringBuilder();
        for (Product.SizeTier tier : Product.SizeTier.values()) {
            if (sizeTierPricing.containsKey(tier)) {
                if (display.length() > 0) {
                    display.append(" | ");
                }
                display.append(String.format("%s: ₹%.2f", tier.getDisplayName(), sizeTierPricing.get(tier)));
            }
        }
        return display.toString();
    }
    
    // Helper method to get price for specific size
    public double getPriceForSize(Product.Size size) {
        if (sizeTierPricing == null || sizeTierPricing.isEmpty()) {
            return price;
        }
        
        Product.SizeTier tier = Product.SizeTier.getTierForSize(size);
        return sizeTierPricing.getOrDefault(tier, price);
    }
}
