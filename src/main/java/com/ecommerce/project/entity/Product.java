package com.ecommerce.project.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private String id;

    private String name;

    private String description;

    @Indexed
    private String categoryId;

    // Base price - kept for backward compatibility
    private double price;

    // Size-based pricing tiers
    private Map<SizeTier, Double> sizeTierPricing;

    // Available sizes for this product
    private List<Size> availableSizes;

    private int stockQuantity;

    private List<String> imageUrls;

    private String color;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Size { 
        XS, S, M, L, XL, XXL, 
        XXXL, // 3XL
        XXXXL, // 4XL  
        XXXXXL, // 5XL
        XXXXXXL, // 6XL
        XXXXXXXL, // 7XL
        XXXXXXXXL, // 8XL
        XXXXXXXXXL, // 9XL
        XXXXXXXXXXL // 10XL
    }

    public enum SizeTier {
        STANDARD("S to XXL", List.of(Size.S, Size.M, Size.L, Size.XL, Size.XXL)),
        LARGE("3XL to 5XL", List.of(Size.XXXL, Size.XXXXL, Size.XXXXXL)),
        EXTRA_LARGE("6XL to 8XL", List.of(Size.XXXXXXL, Size.XXXXXXXL, Size.XXXXXXXXL)),
        SUPER_LARGE("9XL to 10XL", List.of(Size.XXXXXXXXXL, Size.XXXXXXXXXXL));

        private final String displayName;
        private final List<Size> sizes;

        SizeTier(String displayName, List<Size> sizes) {
            this.displayName = displayName;
            this.sizes = sizes;
        }

        public String getDisplayName() {
            return displayName;
        }

        public List<Size> getSizes() {
            return sizes;
        }

        public static SizeTier getTierForSize(Size size) {
            for (SizeTier tier : values()) {
                if (tier.getSizes().contains(size)) {
                    return tier;
                }
            }
            return STANDARD; // Default fallback
        }
    }

    // Helper method to get price for a specific size
    public double getPriceForSize(Size size) {
        if (sizeTierPricing == null || sizeTierPricing.isEmpty()) {
            return price; // Fallback to base price
        }
        
        SizeTier tier = SizeTier.getTierForSize(size);
        return sizeTierPricing.getOrDefault(tier, price);
    }
}
