package com.ecommerce.project.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Represents a color variant of a product with its own images and stock
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {
    
    private String variantId; // Unique identifier for this variant
    private String colorName; // e.g., "Red", "Blue", "Navy"
    private String colorCode; // Hex code e.g., "#FF0000"
    private List<String> imageUrls; // Images specific to this color variant
    private int stockQuantity; // Stock for this specific variant
    
    public ProductVariant(String colorName, String colorCode) {
        this.colorName = colorName;
        this.colorCode = colorCode;
    }
}
