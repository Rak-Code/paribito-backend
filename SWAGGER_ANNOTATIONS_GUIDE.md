# Swagger Annotations & Best Practices Guide

## Overview
This guide demonstrates how to properly annotate Spring Boot controllers and DTOs for optimal Swagger/OpenAPI documentation.

## Installation

Swagger is already configured in your project via:
- Dependency: `springdoc-openapi-starter-webmvc-ui` (v2.8.0)
- Configuration: `OpenApiConfig.java`
- Properties: `application.properties`

## Controller Annotations

### Basic Controller Setup

```java
package com.ecommerce.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {
    
    // Controller methods with annotations below
}
```

### GET Endpoint Example

```java
@GetMapping
@Operation(
    summary = "Get all products",
    description = "Retrieve all products with optional pagination and filtering"
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products list"),
    @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
})
public ResponseEntity<List<ProductDto>> getAllProducts(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(required = false) String category
) {
    // Implementation
    return ResponseEntity.ok(products);
}
```

### GET by ID Endpoint Example

```java
@GetMapping("/{id}")
@Operation(
    summary = "Get product by ID",
    description = "Retrieve detailed information about a specific product"
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Product found"),
    @ApiResponse(responseCode = "404", description = "Product not found"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
})
public ResponseEntity<ProductDto> getProductById(
    @PathVariable
    @io.swagger.v3.oas.annotations.Parameter(description = "Product ID")
    String id
) {
    // Implementation
    return ResponseEntity.ok(product);
}
```

### POST Endpoint Example (Protected)

```java
@PostMapping
@Operation(
    summary = "Create new product",
    description = "Create a new product (Admin only)"
)
@SecurityRequirement(name = "bearerAuth")
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Product created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid product data"),
    @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
    @ApiResponse(responseCode = "403", description = "Forbidden - admin access required"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
})
public ResponseEntity<ProductDto> createProduct(
    @RequestBody
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Product details",
        required = true
    )
    ProductCreateRequest request
) {
    // Implementation
    return ResponseEntity.status(201).body(product);
}
```

### PUT Endpoint Example

```java
@PutMapping("/{id}")
@Operation(
    summary = "Update product",
    description = "Update an existing product (Admin only)"
)
@SecurityRequirement(name = "bearerAuth")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Product updated successfully"),
    @ApiResponse(responseCode = "404", description = "Product not found"),
    @ApiResponse(responseCode = "400", description = "Invalid product data"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Forbidden - admin access required"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
})
public ResponseEntity<ProductDto> updateProduct(
    @PathVariable String id,
    @RequestBody ProductUpdateRequest request
) {
    // Implementation
    return ResponseEntity.ok(updatedProduct);
}
```

### DELETE Endpoint Example

```java
@DeleteMapping("/{id}")
@Operation(
    summary = "Delete product",
    description = "Delete a product (Admin only)"
)
@SecurityRequirement(name = "bearerAuth")
@ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
    @ApiResponse(responseCode = "404", description = "Product not found"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Forbidden - admin access required"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
})
public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
    // Implementation
    return ResponseEntity.noContent().build();
}
```

## DTO/Model Annotations

### Request DTO Example

```java
package com.ecommerce.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ProductCreateRequest", description = "Request payload for creating a new product")
public class ProductCreateRequest {

    @Schema(
        description = "Product name",
        example = "Premium Shirt",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 100)
    private String name;

    @Schema(
        description = "Product description",
        example = "High-quality cotton shirt with premium finish",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Product description is required")
    @Size(min = 10, max = 500)
    private String description;

    @Schema(
        description = "Product price in INR",
        example = "999.99",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Schema(
        description = "Product category ID",
        example = "6608f3a4e8b4a21c8c5e8f12",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Category ID is required")
    private String categoryId;

    @Schema(
        description = "Available stock quantity",
        example = "100",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Stock is required")
    @PositiveOrZero(message = "Stock must be zero or positive")
    private Integer stock;

    @Schema(
        description = "Product SKU",
        example = "PREM-SHT-001"
    )
    private String sku;

    @Schema(
        description = "Tax rate percentage",
        example = "18",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Double taxRate;
}
```

### Response DTO Example

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ProductDto", description = "Complete product information")
public class ProductDto {

    @Schema(
        description = "Product unique identifier",
        example = "6608f3a4e8b4a21c8c5e8f12",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private String id;

    @Schema(
        description = "Product name",
        example = "Premium Shirt"
    )
    private String name;

    @Schema(
        description = "Product description",
        example = "High-quality cotton shirt"
    )
    private String description;

    @Schema(
        description = "Product price in INR",
        example = "999.99"
    )
    private BigDecimal price;

    @Schema(
        description = "Category information",
        implementation = CategoryDto.class
    )
    private CategoryDto category;

    @Schema(
        description = "Current stock level",
        example = "100"
    )
    private Integer stock;

    @Schema(
        description = "Average rating (0-5)",
        example = "4.5"
    )
    private Double rating;

    @Schema(
        description = "Total number of reviews",
        example = "42"
    )
    private Integer reviewCount;

    @Schema(
        description = "Product creation timestamp",
        example = "2024-01-15T10:30:00Z",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime createdAt;

    @Schema(
        description = "Product last update timestamp",
        example = "2024-01-20T15:45:00Z",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime updatedAt;

    @Schema(
        description = "Product images URLs",
        example = "[\"https://r2.dev/image1.jpg\", \"https://r2.dev/image2.jpg\"]"
    )
    private List<String> imageUrls;
}
```

## Authentication Configuration

### Security Scheme Definition in OpenApiConfig

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(apiInfo())
        .servers(servers())
        .components(new Components()
            .addSecuritySchemes("bearerAuth", 
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token from login endpoint")
            )
        );
}
```

### Protected Endpoint Usage

```java
@PostMapping
@Operation(
    summary = "Place order",
    description = "Place a new order"
)
@SecurityRequirement(name = "bearerAuth")  // This makes it protected
public ResponseEntity<OrderDto> placeOrder(@RequestBody OrderCreateRequest request) {
    // Implementation
}
```

## Common Patterns

### Paginated Response

```java
@GetMapping
@Operation(summary = "Get all products with pagination")
public ResponseEntity<Page<ProductDto>> getAllProducts(
    @PageableDefault(size = 10) Pageable pageable
) {
    // Implementation
}
```

### Error Response

```java
@Data
@Schema(description = "Error response")
public class ErrorResponse {
    @Schema(example = "PRODUCT_NOT_FOUND")
    private String errorCode;
    
    @Schema(example = "Product not found")
    private String message;
    
    @Schema(example = "2024-01-20T15:45:00Z")
    private LocalDateTime timestamp;
    
    @Schema(example = "/api/products/invalid-id")
    private String path;
}
```

## Best Practices

### 1. Always Add @Operation Annotation
```java
// ❌ Don't do this
@PostMapping
public ResponseEntity<Void> saveProduct(ProductCreateRequest req) { }

// ✅ Do this
@PostMapping
@Operation(summary = "Create product", description = "Creates a new product")
public ResponseEntity<ProductDto> createProduct(ProductCreateRequest req) { }
```

### 2. Document All Response Codes
```java
// ❌ Don't do this
@GetMapping("/{id}")
public ResponseEntity<ProductDto> getProduct(@PathVariable String id) { }

// ✅ Do this
@GetMapping("/{id}")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Product found"),
    @ApiResponse(responseCode = "404", description = "Product not found")
})
public ResponseEntity<ProductDto> getProduct(@PathVariable String id) { }
```

### 3. Use @Schema in DTOs
```java
// ❌ Don't do this
private String productName;  // No description

// ✅ Do this
@Schema(description = "Product name", example = "Premium Shirt")
private String productName;
```

### 4. Mark Security Requirements
```java
// ❌ Don't do this
@PostMapping
public ResponseEntity<OrderDto> placeOrder(OrderRequest req) { }

// ✅ Do this
@PostMapping
@SecurityRequirement(name = "bearerAuth")
@Operation(summary = "Place order")
public ResponseEntity<OrderDto> placeOrder(OrderRequest req) { }
```

### 5. Validate and Document Parameters
```java
// ❌ Don't do this
@GetMapping("/search")
public List<ProductDto> search(String searchTerm) { }

// ✅ Do this
@GetMapping("/search")
@Operation(summary = "Search products")
public List<ProductDto> search(
    @RequestParam
    @io.swagger.v3.oas.annotations.Parameter(
        description = "Search term for product name or description",
        example = "cotton shirt",
        required = true
    )
    String searchTerm
) { }
```

## Testing Your Documentation

### 1. Verify Swagger UI Loads
```bash
curl -s http://localhost:8080/swagger-ui.html | grep -q "title" && echo "Swagger UI is loaded"
```

### 2. Validate OpenAPI Schema
```bash
curl http://localhost:8080/v3/api-docs | jq '.paths | length'
```

### 3. Test in UI
1. Open http://localhost:8080/swagger-ui.html
2. Verify all endpoints are listed
3. Try "Try it out" on a public endpoint
4. Login and test protected endpoints

## Troubleshooting

### Endpoints not appearing in Swagger
- [ ] Check `@RestController` annotation exists
- [ ] Verify `@RequestMapping` or HTTP method annotation (e.g., `@GetMapping`)
- [ ] Add `@Operation` annotation
- [ ] Rebuild: `mvn clean package`

### DTO fields not appearing
- [ ] Import `io.swagger.v3.oas.annotations.media.Schema`
- [ ] Add `@Schema` annotations to fields
- [ ] Rebuild project

### Security not showing
- [ ] Add `@SecurityRequirement(name = "bearerAuth")`
- [ ] Verify security scheme is defined in OpenApiConfig
- [ ] Check component configuration

## References

- SpringDoc Documentation: https://springdoc.org/
- OpenAPI 3.0 Spec: https://swagger.io/specification/
- Swagger Annotations: https://github.com/swagger-api/swagger-core

---

**Version**: 1.0  
**Last Updated**: April 2026
