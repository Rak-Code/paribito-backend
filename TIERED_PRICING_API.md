# Tiered Pricing API Documentation

## Overview

The product system now supports tiered pricing based on size ranges:

- **S to XXL**: Standard pricing tier
- **3XL to 5XL**: Large pricing tier  
- **6XL to 8XL**: Extra Large pricing tier
- **9XL to 10XL**: Super Large pricing tier

## Size Enum Values

```
XS, S, M, L, XL, XXL, XXXL, XXXXL, XXXXXL, XXXXXXL, XXXXXXXL, XXXXXXXXL, XXXXXXXXXL, XXXXXXXXXXL
```

Mapping:
- XXXL = 3XL
- XXXXL = 4XL
- XXXXXL = 5XL
- XXXXXXL = 6XL
- XXXXXXXL = 7XL
- XXXXXXXXL = 8XL
- XXXXXXXXXL = 9XL
- XXXXXXXXXXL = 10XL

## API Endpoints

### 1. Create Product with Tiered Pricing

**POST** `/api/products`

**Form Data Parameters:**
```
name: "Sample T-Shirt"
description: "High quality cotton t-shirt"
categoryId: "category-123"
price: 299.99 (base price)
stockQuantity: 100
availableSizes: ["S", "M", "L", "XL", "XXL", "XXXL", "XXXXL"]
sizeTierPricing: {
  "STANDARD": 299.99,
  "LARGE": 399.99
}
color: "Blue"
images: [file1.jpg, file2.jpg]
```

### 2. Get Product with Pricing Information

**GET** `/api/products/{id}`

**Response:**
```json
{
  "id": "product-123",
  "name": "Sample T-Shirt",
  "description": "High quality cotton t-shirt",
  "price": 299.99,
  "sizeTierPricing": {
    "STANDARD": 299.99,
    "LARGE": 399.99
  },
  "availableSizes": ["S", "M", "L", "XL", "XXL", "XXXL", "XXXXL"],
  "stockQuantity": 100,
  "categoryId": "category-123",
  "color": "Blue",
  "imageUrls": ["https://example.com/img1.jpg"]
}
```

### 3. Get Detailed Size Pricing

**GET** `/api/products/{id}/size-pricing`

**Response:**
```json
[
  {
    "size": "S",
    "price": 299.99,
    "tier": "STANDARD",
    "tierDisplayName": "S to XXL"
  },
  {
    "size": "M",
    "price": 299.99,
    "tier": "STANDARD", 
    "tierDisplayName": "S to XXL"
  },
  {
    "size": "XXXL",
    "price": 399.99,
    "tier": "LARGE",
    "tierDisplayName": "3XL to 5XL"
  }
]
```

### 4. Get Price for Specific Size

**GET** `/api/products/{id}/price?size=XXXL`

**Response:**
```json
399.99
```

### 5. Get Available Size Tiers

**GET** `/api/products/size-tiers`

**Response:**
```json
{
  "STANDARD": "S to XXL",
  "LARGE": "3XL to 5XL", 
  "EXTRA_LARGE": "6XL to 8XL",
  "SUPER_LARGE": "9XL to 10XL"
}
```

### 6. Get All Available Sizes

**GET** `/api/products/sizes`

**Response:**
```json
["XS", "S", "M", "L", "XL", "XXL", "XXXL", "XXXXL", "XXXXXL", "XXXXXXL", "XXXXXXXL", "XXXXXXXXL", "XXXXXXXXXL", "XXXXXXXXXXL"]
```

## Frontend Display Example

For the product detail page, you can display pricing like:

```
S to XXL: ₹299.99 | 3XL to 5XL: ₹399.99
```

This is generated using the `getPricingDisplay()` method in `ProductResponseDTO`.

## Migration Notes

- Existing products will use the base `price` field if `sizeTierPricing` is null
- The `getPriceForSize()` method handles backward compatibility
- Old single-size products will continue to work with the base price