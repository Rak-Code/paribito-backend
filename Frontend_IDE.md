# E-Commerce API - Frontend Integration Guide

Base URL: `http://localhost:8080`

---

## 1. AUTHENTICATION

### Register User
**POST** `/api/auth/register`

```json
{
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "password": "SecurePass123!",
  "phone": "+1234567890",
  "role": "USER"
}
```

**Response (201)**
```json
{
  "id": "user123",
  "email": "john.doe@example.com",
  "fullName": "John Doe",
  "phone": "+1234567890",
  "role": "USER",
  "createdAt": "2025-11-24T10:00:00",
  "addresses": []
}
```

### Login User
**POST** `/api/auth/login`

```json
{
  "email": "john.doe@example.com",
  "password": "SecurePass123!"
}
```

**Response (200)**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "user123",
    "email": "john.doe@example.com",
    "fullName": "John Doe",
    "phone": "+1234567890",
    "role": "USER",
    "createdAt": "2025-11-24T10:00:00",
    "addresses": []
  }
}
```

---

## 2. USER MANAGEMENT

### Get User by ID
**GET** `/api/users/{id}`
**Headers:** `Authorization: Bearer <token>`

**Response (200)**
```json
{
  "id": "user123",
  "email": "john.doe@example.com",
  "fullName": "John Doe",
  "phone": "+1234567890",
  "role": "USER",
  "createdAt": "2025-11-24T10:00:00",
  "addresses": []
}
```

### Add Address
**POST** `/api/users/{id}/addresses`
**Headers:** `Authorization: Bearer <token>`

```json
{
  "addressLine": "123 Main Street",
  "city": "New York",
  "state": "NY",
  "postalCode": "10001",
  "country": "USA",
  "isDefault": true
}
```

**Response (200)** - Returns updated User object

### Delete Address
**DELETE** `/api/users/{id}/addresses/{addressId}`
**Headers:** `Authorization: Bearer <token>`

**Response (204)** - No Content

---

## 3. CATEGORIES

### Create Category (Admin)
**POST** `/api/categories`
**Headers:** `Authorization: Bearer <token>`

```json
{
  "name": "Electronics"
}
```

**Response (201)**
```json
{
  "id": "cat123",
  "name": "Electronics",
  "createdAt": "2025-11-24T10:00:00"
}
```

### Get All Categories
**GET** `/api/categories`

**Response (200)**
```json
[
  {
    "id": "cat123",
    "name": "Electronics",
    "createdAt": "2025-11-24T10:00:00"
  }
]
```

### Get Category by ID
**GET** `/api/categories/{id}`

**Response (200)**
```json
{
  "id": "cat123",
  "name": "Electronics",
  "createdAt": "2025-11-24T10:00:00"
}
```

### Update Category (Admin)
**PUT** `/api/categories/{id}`
**Headers:** `Authorization: Bearer <token>`

```json
{
  "name": "Consumer Electronics"
}
```

**Response (200)** - Returns updated Category

### Delete Category (Admin)
**DELETE** `/api/categories/{id}`
**Headers:** `Authorization: Bearer <token>`

**Response (204)** - No Content

---

## 4. PRODUCTS

### Create Product with Images (Admin)
**POST** `/api/products`
**Headers:** `Authorization: Bearer <token>`, `Content-Type: multipart/form-data`

**Form Data:**
- `name` (string): "Wireless Headphones"
- `description` (string): "High-quality Bluetooth headphones"
- `categoryId` (string): "cat123"
- `price` (number): 99.99
- `stockQuantity` (number): 50
- `size` (string, optional): "M" (Values: XS, S, M, L, XL, XXL, XXXL)
- `color` (string, optional): "Black"
- `images` (file[], optional): Multiple image files

**Response (201)**
```json
{
  "id": "prod123",
  "name": "Wireless Headphones",
  "description": "High-quality Bluetooth headphones",
  "price": 99.99,
  "stockQuantity": 50,
  "categoryId": "cat123",
  "color": "Black",
  "size": "M",
  "imageUrls": [
    "https://r2-url.com/products/image1.jpg"
  ]
}
```

### Update Product with Images (Admin)
**PUT** `/api/products/{id}`
**Headers:** `Authorization: Bearer <token>`, `Content-Type: multipart/form-data`

**Form Data:**
- `name` (string): "Wireless Headphones Pro"
- `description` (string): "Premium Bluetooth headphones"
- `categoryId` (string): "cat123"
- `price` (number): 129.99
- `stockQuantity` (number): 75
- `size` (string, optional): "L"
- `color` (string, optional): "Silver"
- `images` (file[], optional): New image files
- `keepExistingImages` (boolean, optional): true (default: true)

**Response (200)** - Returns updated Product

### Get Product by ID
**GET** `/api/products/{id}`

**Response (200)**
```json
{
  "id": "prod123",
  "name": "Wireless Headphones",
  "description": "High-quality Bluetooth headphones",
  "price": 99.99,
  "stockQuantity": 50,
  "categoryId": "cat123",
  "color": "Black",
  "size": "M",
  "imageUrls": ["https://r2-url.com/products/image1.jpg"]
}
```

### Get All Products
**GET** `/api/products`

**Response (200)** - Returns array of Products

### Search Products
**GET** `/api/products?q={searchTerm}`

**Example:** `/api/products?q=headphones`

**Response (200)** - Returns array of matching Products

### Get Products by Category
**GET** `/api/products?category={categoryId}`

**Example:** `/api/products?category=cat123`

**Response (200)** - Returns array of Products

### Delete Product (Admin)
**DELETE** `/api/products/{id}`
**Headers:** `Authorization: Bearer <token>`

**Response (204)** - No Content

---

## 5. CART

### Add to Cart
**POST** `/api/cart`
**Headers:** `Authorization: Bearer <token>`

```json
{
  "userId": "user123",
  "productId": "prod123",
  "quantity": 2
}
```

**Response (201)**
```json
{
  "id": "cart123",
  "userId": "user123",
  "productId": "prod123",
  "quantity": 2,
  "addedAt": "2025-11-24T10:00:00"
}
```

### Update Cart Quantity
**PUT** `/api/cart`
**Headers:** `Authorization: Bearer <token>`

```json
{
  "userId": "user123",
  "productId": "prod123",
  "quantity": 5
}
```

**Response (200)** - Returns updated CartItem

### Get User Cart
**GET** `/api/cart/{userId}`
**Headers:** `Authorization: Bearer <token>`

**Response (200)**
```json
[
  {
    "id": "cart123",
    "userId": "user123",
    "productId": "prod123",
    "quantity": 2,
    "addedAt": "2025-11-24T10:00:00"
  }
]
```

### Remove from Cart
**DELETE** `/api/cart/{cartItemId}`
**Headers:** `Authorization: Bearer <token>`

**Response (204)** - No Content

### Clear Cart
**DELETE** `/api/cart/{userId}/clear`
**Headers:** `Authorization: Bearer <token>`

**Response (204)** - No Content

---

## 6. ORDERS

### Place Order
**POST** `/api/orders`
**Headers:** `Authorization: Bearer <token>`

```json
{
  "userId": "user123",
  "address": {
    "addressId": "addr123",
    "addressLine": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "postalCode": "10001",
    "country": "USA"
  },
  "totalAmount": 199.98,
  "items": [
    {
      "productId": "prod123",
      "quantity": 2,
      "price": 99.99
    }
  ]
}
```

**Response (201)**
```json
{
  "id": "order123",
  "userId": "user123",
  "address": {
    "addressId": "addr123",
    "addressLine": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "postalCode": "10001",
    "country": "USA"
  },
  "totalAmount": 199.98,
  "status": "pending",
  "orderDate": "2025-11-24T10:00:00",
  "items": [
    {
      "productId": "prod123",
      "quantity": 2,
      "price": 99.99
    }
  ]
}
```

### Get User Orders
**GET** `/api/orders/user/{userId}`
**Headers:** `Authorization: Bearer <token>`

**Response (200)** - Returns array of Orders

### Get Order by ID
**GET** `/api/orders/{orderId}`
**Headers:** `Authorization: Bearer <token>`

**Response (200)** - Returns Order object

### Update Order Status (Admin)
**PUT** `/api/orders/{orderId}/status`
**Headers:** `Authorization: Bearer <token>`

```json
{
  "status": "shipped"
}
```

**Status Values:** pending, processing, shipped, delivered, cancelled

**Response (200)** - Returns updated Order

### Get All Orders (Admin)
**GET** `/api/orders`
**Headers:** `Authorization: Bearer <token>`

**Response (200)** - Returns array of all Orders

---

## 7. PAYMENTS

### Process Payment
**POST** `/api/payments`
**Headers:** `Authorization: Bearer <token>`

```json
{
  "orderId": "order123",
  "amount": 199.98,
  "paymentMethod": "razorpay"
}
```

**Payment Methods:** credit_card, debit_card, upi, net_banking, cod, razorpay

**Response (201)**
```json
{
  "id": "pay123",
  "orderId": "order123",
  "amount": 199.98,
  "paymentMethod": "razorpay",
  "paymentStatus": "pending",
  "transactionId": "txn_abc123",
  "razorpayOrderId": null,
  "razorpayPaymentId": null,
  "razorpaySignature": null,
  "paymentDate": "2025-11-24T10:00:00"
}
```

### Get Payment by Order ID
**GET** `/api/payments/order/{orderId}`
**Headers:** `Authorization: Bearer <token>`

**Response (200)** - Returns Payment object

### Get Payment by Payment ID
**GET** `/api/payments/{paymentId}`
**Headers:** `Authorization: Bearer <token>`

**Response (200)** - Returns Payment object

### Get All Payments (Admin)
**GET** `/api/payments`
**Headers:** `Authorization: Bearer <token>`

**Response (200)** - Returns array of Payments

---

## 8. RAZORPAY INTEGRATION

### Create Razorpay Order
**POST** `/api/payments/razorpay/create-order`
**Headers:** `Authorization: Bearer <token>`

```json
{
  "orderId": "order123",
  "amount": 199.98
}
```

**Response (200)**
```json
{
  "razorpayOrderId": "order_MNOPqrstuvwxyz",
  "orderId": "order123",
  "amount": 199.98,
  "currency": "INR",
  "keyId": "rzp_test_xxxxx"
}
```

### Verify Razorpay Payment
**POST** `/api/payments/razorpay/verify`
**Headers:** `Authorization: Bearer <token>`

```json
{
  "razorpayOrderId": "order_MNOPqrstuvwxyz",
  "razorpayPaymentId": "pay_ABCDefghijklmn",
  "razorpaySignature": "signature_hash_string",
  "orderId": "order123"
}
```

**Response (200)** - Returns Payment object with status "completed"

### Refund Razorpay Payment (Admin)
**POST** `/api/payments/razorpay/refund/{paymentId}?amount={amount}`
**Headers:** `Authorization: Bearer <token>`

**Example:** `/api/payments/razorpay/refund/pay123?amount=199.98`

**Response (200)**
```json
"Refund successful. Refund ID: rfnd_XYZabcdefghijk"
```

---

## 9. REVIEWS

### Add Review
**POST** `/api/reviews`
**Headers:** `Authorization: Bearer <token>`

```json
{
  "productId": "prod123",
  "userId": "user123",
  "rating": 5,
  "comment": "Excellent product! Highly recommended."
}
```

**Rating:** 1-5 (integer)

**Response (201)**
```json
{
  "id": "rev123",
  "productId": "prod123",
  "userId": "user123",
  "rating": 5,
  "comment": "Excellent product! Highly recommended.",
  "createdAt": "2025-11-24T10:00:00"
}
```

### Get Product Reviews
**GET** `/api/reviews/product/{productId}`

**Response (200)** - Returns array of Reviews

### Get User Reviews
**GET** `/api/reviews/user/{userId}`
**Headers:** `Authorization: Bearer <token>`

**Response (200)** - Returns array of Reviews

### Update Review
**PUT** `/api/reviews/{reviewId}`
**Headers:** `Authorization: Bearer <token>`

```json
{
  "rating": 4,
  "comment": "Good product, but could be better."
}
```

**Response (200)** - Returns updated Review

### Delete Review
**DELETE** `/api/reviews/{reviewId}`
**Headers:** `Authorization: Bearer <token>`

**Response (204)** - No Content

---

## 10. WISHLIST

### Add to Wishlist
**POST** `/api/wishlist`
**Headers:** `Authorization: Bearer <token>`

```json
{
  "userId": "user123",
  "productId": "prod123"
}
```

**Response (201)**
```json
{
  "id": "wish123",
  "userId": "user123",
  "productId": "prod123",
  "addedAt": "2025-11-24T10:00:00"
}
```

### Get User Wishlist
**GET** `/api/wishlist/{userId}`
**Headers:** `Authorization: Bearer <token>`

**Response (200)** - Returns array of WishlistItems

### Remove from Wishlist
**DELETE** `/api/wishlist/{wishlistItemId}`
**Headers:** `Authorization: Bearer <token>`

**Response (204)** - No Content

---

## 11. IMAGE UPLOAD

### Upload Single Image
**POST** `/api/images/upload`
**Headers:** `Content-Type: multipart/form-data`

**Form Data:**
- `file` (file): Image file
- `folder` (string, optional): "products" (default)

**Response (200)**
```json
{
  "success": true,
  "message": "Image uploaded successfully",
  "imageUrl": "https://r2-url.com/products/image1.jpg"
}
```

### Upload Multiple Images
**POST** `/api/images/upload-multiple`
**Headers:** `Content-Type: multipart/form-data`

**Form Data:**
- `files` (file[]): Multiple image files
- `folder` (string, optional): "products" (default)

**Response (200)**
```json
{
  "success": true,
  "message": "Images uploaded successfully",
  "imageUrls": [
    "https://r2-url.com/products/image1.jpg",
    "https://r2-url.com/products/image2.jpg"
  ],
  "count": 2
}
```

### Delete Image
**DELETE** `/api/images/delete?imageUrl={imageUrl}`

**Example:** `/api/images/delete?imageUrl=https://r2-url.com/products/image1.jpg`

**Response (200)**
```json
{
  "success": true,
  "message": "Image deleted successfully"
}
```

### Delete Multiple Images
**DELETE** `/api/images/delete-multiple`

```json
[
  "https://r2-url.com/products/image1.jpg",
  "https://r2-url.com/products/image2.jpg"
]
```

**Response (200)**
```json
{
  "success": true,
  "message": "All images deleted successfully"
}
```

---

## 12. CACHE MANAGEMENT (Admin)

### Get Cache Statistics
**GET** `/api/cache/stats`
**Headers:** `Authorization: Bearer <token>`

**Response (200)**
```json
{
  "redisVersion": "7.0.0",
  "redisConnected": true,
  "totalCaches": 5,
  "cacheNames": ["products", "categories", "orders", "users", "reviews"],
  "cacheDetails": {
    "products": {
      "status": "Active",
      "type": "Redis",
      "size": 150
    }
  }
}
```

### Get All Cache Names
**GET** `/api/cache/names`
**Headers:** `Authorization: Bearer <token>`

**Response (200)**
```json
{
  "cacheNames": ["products", "categories", "orders", "users", "reviews"],
  "totalCaches": 5,
  "cacheType": "Redis"
}
```

### Get Cache Keys
**GET** `/api/cache/keys/{cacheName}`
**Headers:** `Authorization: Bearer <token>`

**Response (200)**
```json
{
  "cacheName": "products",
  "keys": ["prod123", "prod124", "prod125"],
  "totalKeys": 3
}
```

### Check Redis Health
**GET** `/api/cache/health`
**Headers:** `Authorization: Bearer <token>`

**Response (200)**
```json
{
  "status": "UP",
  "ping": "PONG",
  "version": "7.0.0",
  "uptime": "86400",
  "connectedClients": "5"
}
```

### Evict Specific Cache
**DELETE** `/api/cache/evict/{cacheName}`
**Headers:** `Authorization: Bearer <token>`

**Response (200)**
```json
{
  "message": "Cache evicted successfully",
  "cacheName": "products",
  "cacheType": "Redis"
}
```

### Evict All Caches
**DELETE** `/api/cache/evict-all`
**Headers:** `Authorization: Bearer <token>`

**Response (200)**
```json
{
  "message": "All caches evicted successfully",
  "evictedCaches": ["products", "categories", "orders", "users", "reviews"],
  "totalEvicted": 5,
  "cacheType": "Redis"
}
```

---

## ENUMS & CONSTANTS

### User.Role
- `USER`
- `ADMIN`

### Product.Size
- `XS`
- `S`
- `M`
- `L`
- `XL`
- `XXL`
- `XXXL`

### Order.Status
- `pending`
- `processing`
- `shipped`
- `delivered`
- `cancelled`

### Payment.PaymentMethod
- `credit_card`
- `debit_card`
- `upi`
- `net_banking`
- `cod`
- `razorpay`

### Payment.PaymentStatus
- `pending`
- `completed`
- `failed`
- `refunded`
