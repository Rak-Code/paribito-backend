package com.ecommerce.project.controller;

import com.ecommerce.project.dto.ProductRequestDTO;
import com.ecommerce.project.dto.ProductResponseDTO;
import com.ecommerce.project.dto.SizePricingDTO;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.service.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> create(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("categoryId") String categoryId,
            @RequestParam("price") double price,
            @RequestParam("stockQuantity") int stockQuantity,
            @RequestParam(value = "availableSizes") String availableSizesJson,
            @RequestParam(value = "sizeTierPricing", required = false) String sizeTierPricingJson,
            @RequestParam(value = "color", required = false) String color,
            @RequestParam(value = "productType", required = false) String productType,
            @RequestParam(value = "availableDesigns", required = false) String availableDesignsJson,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {
        
        try {
            // Validate required fields
            validateProductInputs(name, description, categoryId, price, stockQuantity);
            
            // Parse available sizes with better error handling
            List<Product.Size> availableSizes = parseAvailableSizes(availableSizesJson);
            
            // Parse size tier pricing if provided
            Map<Product.SizeTier, Double> sizeTierPricing = null;
            if (sizeTierPricingJson != null && !sizeTierPricingJson.isBlank()) {
                sizeTierPricing = parseSizeTierPricing(sizeTierPricingJson);
            }
            
            ProductRequestDTO dto = new ProductRequestDTO(
                name, description, categoryId, price, sizeTierPricing, 
                availableSizes, stockQuantity, null, color, null, null
            );
            
            ProductResponseDTO response = productService.createProductWithImages(dto, images);
            return ResponseEntity.status(201).body(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid input for product creation: {}", e.getMessage());
            throw e; // Re-throw to be handled by GlobalExceptionHandler
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage());
            throw new RuntimeException("Failed to create product: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable String id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("categoryId") String categoryId,
            @RequestParam("price") double price,
            @RequestParam("stockQuantity") int stockQuantity,
            @RequestParam(value = "availableSizes") String availableSizesJson,
            @RequestParam(value = "sizeTierPricing", required = false) String sizeTierPricingJson,
            @RequestParam(value = "color", required = false) String color,
            @RequestParam(value = "productType", required = false) String productType,
            @RequestParam(value = "availableDesigns", required = false) String availableDesignsJson,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestParam(value = "keepExistingImages", required = false, defaultValue = "true") boolean keepExistingImages) {
        
        try {
            // Validate required fields
            validateProductInputs(name, description, categoryId, price, stockQuantity);
            
            // Parse available sizes with better error handling
            List<Product.Size> availableSizes = parseAvailableSizes(availableSizesJson);
            
            // Parse size tier pricing if provided
            Map<Product.SizeTier, Double> sizeTierPricing = null;
            if (sizeTierPricingJson != null && !sizeTierPricingJson.isBlank()) {
                sizeTierPricing = parseSizeTierPricing(sizeTierPricingJson);
            }
            
            ProductRequestDTO dto = new ProductRequestDTO(
                name, description, categoryId, price, sizeTierPricing, 
                availableSizes, stockQuantity, null, color, null, null
            );
            
            ProductResponseDTO response = productService.updateProductWithImages(id, dto, images, keepExistingImages);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid input for product update: {}", e.getMessage());
            throw e; // Re-throw to be handled by GlobalExceptionHandler
        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage());
            throw new RuntimeException("Failed to update product: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> get(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {
        
        // Handle search and category filtering (non-paginated)
        if (q != null && !q.isBlank()) {
            return ResponseEntity.ok(productService.searchProducts(q));
        }
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(productService.getProductsByCategory(category));
        }
        
        // Handle pagination if page and size are provided
        if (page != null && size != null) {
            Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<ProductResponseDTO> productPage = productService.getAllProducts(pageable);
            return ResponseEntity.ok(productPage);
        }
        
        // Default: return all products without pagination
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // New endpoint to get size tiers and their display names
    @GetMapping("/size-tiers")
    public ResponseEntity<Map<Product.SizeTier, String>> getSizeTiers() {
        Map<Product.SizeTier, String> sizeTiers = Map.of(
            Product.SizeTier.STANDARD, Product.SizeTier.STANDARD.getDisplayName(),
            Product.SizeTier.LARGE, Product.SizeTier.LARGE.getDisplayName(),
            Product.SizeTier.EXTRA_LARGE, Product.SizeTier.EXTRA_LARGE.getDisplayName(),
            Product.SizeTier.SUPER_LARGE, Product.SizeTier.SUPER_LARGE.getDisplayName()
        );
        return ResponseEntity.ok(sizeTiers);
    }

    // New endpoint to get all available sizes
    @GetMapping("/sizes")
    public ResponseEntity<Product.Size[]> getAllSizes() {
        return ResponseEntity.ok(Product.Size.values());
    }

    // New endpoint to get price for a specific size of a product
    @GetMapping("/{id}/price")
    public ResponseEntity<Double> getPriceForSize(
            @PathVariable String id,
            @RequestParam Product.Size size) {
        ProductResponseDTO product = productService.getProduct(id);
        double price = product.getPriceForSize(size);
        return ResponseEntity.ok(price);
    }

    // New endpoint to get detailed size pricing for a product
    @GetMapping("/{id}/size-pricing")
    public ResponseEntity<List<SizePricingDTO>> getSizePricing(@PathVariable String id) {
        ProductResponseDTO product = productService.getProduct(id);
        
        List<SizePricingDTO> sizePricing = product.availableSizes().stream()
            .map(size -> SizePricingDTO.fromSizeAndProduct(size, product))
            .toList();
            
        return ResponseEntity.ok(sizePricing);
    }

    /**
     * Helper method to parse available sizes JSON with better error handling
     */
    private List<Product.Size> parseAvailableSizes(String availableSizesJson) {
        if (availableSizesJson == null || availableSizesJson.isBlank()) {
            throw new IllegalArgumentException("Available sizes cannot be empty");
        }

        try {
            // First try to parse as proper JSON array
            return objectMapper.readValue(availableSizesJson, new TypeReference<List<Product.Size>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse availableSizes as JSON array: {}. Attempting fallback parsing.", e.getMessage());
            
            // Fallback: try to handle common malformed formats
            try {
                // Remove brackets and split by comma, then clean up each size
                String cleaned = availableSizesJson.trim();
                if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
                    cleaned = cleaned.substring(1, cleaned.length() - 1);
                }
                
                String[] sizeStrings = cleaned.split(",");
                List<Product.Size> sizes = new ArrayList<>();
                
                for (String sizeStr : sizeStrings) {
                    String trimmed = sizeStr.trim().replaceAll("[\"\']", ""); // Remove quotes
                    try {
                        Product.Size size = Product.Size.valueOf(trimmed.toUpperCase());
                        sizes.add(size);
                    } catch (IllegalArgumentException ex) {
                        log.error("Invalid size value: {}", trimmed);
                        throw new IllegalArgumentException("Invalid size: " + trimmed + ". Valid sizes are: " + Arrays.toString(Product.Size.values()));
                    }
                }
                
                if (sizes.isEmpty()) {
                    throw new IllegalArgumentException("No valid sizes found in: " + availableSizesJson);
                }
                
                return sizes;
            } catch (Exception fallbackException) {
                log.error("Failed to parse availableSizes even with fallback: {}", fallbackException.getMessage());
                throw new IllegalArgumentException("Invalid availableSizes format. Expected JSON array like [\"S\",\"M\",\"L\"] but got: " + availableSizesJson);
            }
        }
    }

    /**
     * Helper method to parse size tier pricing JSON with better error handling
     */
    private Map<Product.SizeTier, Double> parseSizeTierPricing(String sizeTierPricingJson) {
        try {
            return objectMapper.readValue(sizeTierPricingJson, new TypeReference<Map<Product.SizeTier, Double>>() {});
        } catch (Exception e) {
            log.error("Failed to parse sizeTierPricing: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid sizeTierPricing format. Expected JSON object like {\"STANDARD\":100.0} but got: " + sizeTierPricingJson);
        }
    }

    /**
     * Helper method to validate product input parameters
     */
    private void validateProductInputs(String name, String description, String categoryId, double price, int stockQuantity) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Product description cannot be empty");
        }
        if (categoryId == null || categoryId.trim().isEmpty()) {
            throw new IllegalArgumentException("Category ID cannot be empty");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
    }

    // ========== COLOR VARIANT MANAGEMENT ==========
    
    @PostMapping("/{productId}/variants")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> addColorVariant(
            @PathVariable String productId,
            @RequestParam("colorName") String colorName,
            @RequestParam("colorCode") String colorCode,
            @RequestParam(value = "stockQuantity", defaultValue = "0") int stockQuantity,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {
        
        try {
            ProductResponseDTO response = productService.addColorVariant(productId, colorName, colorCode, stockQuantity, images);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error adding color variant: {}", e.getMessage());
            throw new RuntimeException("Failed to add color variant: " + e.getMessage());
        }
    }
    
    @PutMapping("/{productId}/variants/{variantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> updateColorVariant(
            @PathVariable String productId,
            @PathVariable String variantId,
            @RequestParam(value = "colorName", required = false) String colorName,
            @RequestParam(value = "colorCode", required = false) String colorCode,
            @RequestParam(value = "stockQuantity", required = false) Integer stockQuantity,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestParam(value = "keepExistingImages", defaultValue = "true") boolean keepExistingImages) {
        
        try {
            ProductResponseDTO response = productService.updateColorVariant(
                productId, variantId, colorName, colorCode, stockQuantity, images, keepExistingImages);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating color variant: {}", e.getMessage());
            throw new RuntimeException("Failed to update color variant: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{productId}/variants/{variantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> deleteColorVariant(
            @PathVariable String productId,
            @PathVariable String variantId) {
        
        ProductResponseDTO response = productService.deleteColorVariant(productId, variantId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{productId}/variants")
    public ResponseEntity<List<Map<String, Object>>> getColorVariants(@PathVariable String productId) {
        List<Map<String, Object>> variants = productService.getColorVariants(productId);
        return ResponseEntity.ok(variants);
    }
    
    // ========== INDIVIDUAL IMAGE MANAGEMENT ==========
    
    @DeleteMapping("/{productId}/images")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> deleteProductImage(
            @PathVariable String productId,
            @RequestParam("imageUrl") String imageUrl) {
        
        try {
            ProductResponseDTO response = productService.deleteProductImage(productId, imageUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting product image: {}", e.getMessage());
            throw new RuntimeException("Failed to delete image: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{productId}/variants/{variantId}/images")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> deleteVariantImage(
            @PathVariable String productId,
            @PathVariable String variantId,
            @RequestParam("imageUrl") String imageUrl) {
        
        try {
            ProductResponseDTO response = productService.deleteVariantImage(productId, variantId, imageUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting variant image: {}", e.getMessage());
            throw new RuntimeException("Failed to delete variant image: " + e.getMessage());
        }
    }
    
    @PostMapping("/{productId}/images")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> addProductImages(
            @PathVariable String productId,
            @RequestParam("images") MultipartFile[] images) {
        
        try {
            ProductResponseDTO response = productService.addProductImages(productId, images);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error adding product images: {}", e.getMessage());
            throw new RuntimeException("Failed to add images: " + e.getMessage());
        }
    }
    
    @PostMapping("/{productId}/variants/{variantId}/images")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> addVariantImages(
            @PathVariable String productId,
            @PathVariable String variantId,
            @RequestParam("images") MultipartFile[] images) {
        
        try {
            ProductResponseDTO response = productService.addVariantImages(productId, variantId, images);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error adding variant images: {}", e.getMessage());
            throw new RuntimeException("Failed to add variant images: " + e.getMessage());
        }
    }
}