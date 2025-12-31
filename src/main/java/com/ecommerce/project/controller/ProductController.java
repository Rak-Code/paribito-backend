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
            @RequestParam(value = "images", required = false) MultipartFile[] images) {
        
        try {
            // Parse available sizes
            List<Product.Size> availableSizes = objectMapper.readValue(
                availableSizesJson, 
                new TypeReference<List<Product.Size>>() {}
            );
            
            // Parse size tier pricing if provided
            Map<Product.SizeTier, Double> sizeTierPricing = null;
            if (sizeTierPricingJson != null && !sizeTierPricingJson.isBlank()) {
                sizeTierPricing = objectMapper.readValue(
                    sizeTierPricingJson, 
                    new TypeReference<Map<Product.SizeTier, Double>>() {}
                );
            }
            
            ProductRequestDTO dto = new ProductRequestDTO(
                name, description, categoryId, price, sizeTierPricing, 
                availableSizes, stockQuantity, null, color
            );
            
            ProductResponseDTO response = productService.createProductWithImages(dto, images);
            return ResponseEntity.status(201).body(response);
            
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
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestParam(value = "keepExistingImages", required = false, defaultValue = "true") boolean keepExistingImages) {
        
        try {
            // Parse available sizes
            List<Product.Size> availableSizes = objectMapper.readValue(
                availableSizesJson, 
                new TypeReference<List<Product.Size>>() {}
            );
            
            // Parse size tier pricing if provided
            Map<Product.SizeTier, Double> sizeTierPricing = null;
            if (sizeTierPricingJson != null && !sizeTierPricingJson.isBlank()) {
                sizeTierPricing = objectMapper.readValue(
                    sizeTierPricingJson, 
                    new TypeReference<Map<Product.SizeTier, Double>>() {}
                );
            }
            
            ProductRequestDTO dto = new ProductRequestDTO(
                name, description, categoryId, price, sizeTierPricing, 
                availableSizes, stockQuantity, null, color
            );
            
            ProductResponseDTO response = productService.updateProductWithImages(id, dto, images, keepExistingImages);
            return ResponseEntity.ok(response);
            
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
}
