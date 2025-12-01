package com.ecommerce.project.controller;

import com.ecommerce.project.dto.ProductRequestDTO;
import com.ecommerce.project.dto.ProductResponseDTO;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create product with images", description = "Create a new product and upload images automatically")
    public ResponseEntity<ProductResponseDTO> create(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("categoryId") String categoryId,
            @RequestParam("price") double price,
            @RequestParam("stockQuantity") int stockQuantity,
            @RequestParam(value = "size", required = false) Product.Size size,
            @RequestParam(value = "color", required = false) String color,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {
        
        try {
            ProductRequestDTO dto = new ProductRequestDTO(
                name, description, categoryId, price, stockQuantity, null, size, color
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
    @Operation(summary = "Update product with images", description = "Update product and optionally upload new images")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable String id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("categoryId") String categoryId,
            @RequestParam("price") double price,
            @RequestParam("stockQuantity") int stockQuantity,
            @RequestParam(value = "size", required = false) Product.Size size,
            @RequestParam(value = "color", required = false) String color,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestParam(value = "keepExistingImages", required = false, defaultValue = "true") boolean keepExistingImages) {
        
        try {
            ProductRequestDTO dto = new ProductRequestDTO(
                name, description, categoryId, price, stockQuantity, null, size, color
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
    public ResponseEntity<List<ProductResponseDTO>> list(@RequestParam(required = false) String category,
                                                        @RequestParam(required = false) String q) {
        if (q != null && !q.isBlank()) return ResponseEntity.ok(productService.searchProducts(q));
        if (category != null && !category.isBlank()) return ResponseEntity.ok(productService.getProductsByCategory(category));
        return ResponseEntity.ok(productService.getAllProducts());
    }
}
