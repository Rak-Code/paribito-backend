# E-Commerce API Manual Test Guide

This guide provides step-by-step APIs with hardcoded JSON requests for testing the complete e-commerce workflow.

**Base URL:** `http://localhost:8080`

## Step 1: Admin Registration

### Register Admin User
**URL:** `POST /api/auth/register`  
**Headers:** `Content-Type: application/json`  

**Request Body:**
```json
{
  "fullName": "Admin User",
  "email": "admin@example.com",
  "password": "admin123",
  "phone": "9876543210"
}
```
**Note:** After registration, manually update the user role to ADMIN in MongoDB: `db.users.updateOne({email: "admin@example.com"}, {$set: {role: "ADMIN"}})`

---

## Step 2: Regular User Registration

### Register Customer User
**URL:** `POST /api/auth/register`  
**Headers:** `Content-Type: application/json`

**Request Body:**
```json
{
  "fullName": "John Doe",
  "email": "user@example.com",
  "password": "user123",
  "phone": "9876543211"
}
```

### Add Address to User (Replace USER_ID with actual user ID from registration response)
**URL:** `POST /api/auth/login`  
**Headers:** `Content-Type: application/json`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "user123"
}
```
**Expected Response:** Save the returned JWT token for authentication

---

## Step 3: Login as Admin

### Admin Login (Use token from response)
**URL:** `POST /api/auth/login`  
**Headers:** `Content-Type: application/json`

**Request Body:**
```json
{
  "email": "admin@example.com",
  "password": "admin123"
}
```
**Expected Response:** Save the admin JWT token

---

## Step 4: Create Category

### Create Electronics Category
**URL:** `POST /api/categories`  
**Headers:**
- `Content-Type: application/json`
- `Authorization: Bearer [ADMIN_JWT_TOKEN]`

**Request Body:**
```json
{
  "name": "Electronics"
}
```
**Expected Response:** Save the category ID for later use

---

## Step 5: Create Product

### Create Smartphone Product (Replace CATEGORY_ID with actual category ID)
**URL:** `POST /api/products`  
**Headers:**
- `Content-Type: application/json`
- `Authorization: Bearer [ADMIN_JWT_TOKEN]`

**Request Body:**
```json
{
  "name": "Smartphone XYZ",
  "description": "A high-quality smartphone with advanced features",
  "categoryId": "[CATEGORY_ID]",
  "price": 599.99,
  "stockQuantity": 50,
  "imageUrls": [
    "https://example.com/image1.jpg",
    "https://example.com/image2.jpg"
  ],
  "size": "M",
  "color": "Black"
}
```
**Expected Response:** Save the product ID for later use

---

## Step 6: Add Product to Cart

### Add Product to Cart
**URL:** `POST /api/cart`  
**Headers:**
- `Content-Type: application/json`
- `Authorization: Bearer [USER_JWT_TOKEN]`

**Request Body:**
```json
{
  "userId": "[USER_ID]",
  "productId": "[PRODUCT_ID]",
  "quantity": 2
}
```
**Expected Response:** Save the cart item ID

---

## Step 7: View Cart

### Get User's Cart
**URL:** `GET /api/cart/[USER_ID]`  
**Headers:** `Authorization: Bearer [USER_JWT_TOKEN]`

**Request Body:** None

---

## Step 8: Add to Wishlist

### Add Product to Wishlist
**URL:** `POST /api/wishlist`  
**Headers:**
- `Content-Type: application/json`
- `Authorization: Bearer [USER_JWT_TOKEN]`

**Request Body:**
```json
{
  "userId": "[USER_ID]",
  "productId": "[PRODUCT_ID]"
}
```

---

## Step 9: Place Order

### Create Order
**URL:** `POST /api/orders`  
**Headers:**
- `Content-Type: application/json`
- `Authorization: Bearer [USER_JWT_TOKEN]`

**Request Body:**
```json
{
  "userId": "[USER_ID]",
  "address": {
    "addressId": "addr_001",
    "addressLine": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "postalCode": "10001",
    "country": "USA"
  },
  "totalAmount": 1199.98,
  "items": [
    {
      "productId": "[PRODUCT_ID]",
      "quantity": 2,
      "price": 599.99
    }
  ]
}
```
**Expected Response:** Save the order ID

---

## Step 10: Process Payment

### Make Payment
**URL:** `POST /api/payments`  
**Headers:**
- `Content-Type: application/json`
- `Authorization: Bearer [USER_JWT_TOKEN]`

**Request Body:**
```json
{
  "orderId": "[ORDER_ID]",
  "amount": 1199.98,
  "paymentMethod": "credit_card"
}
```
**Expected Response:** Save the payment ID

---

## Step 11: Update Order Status (Admin Operations)

### Update to Processing
**URL:** `PUT /api/orders/[ORDER_ID]/status`  
**Headers:**
- `Content-Type: application/json`
- `Authorization: Bearer [ADMIN_JWT_TOKEN]`

**Request Body:**
```json
{
  "status": "processing"
}
```

### Update to Shipped
**URL:** `PUT /api/orders/[ORDER_ID]/status`  
**Headers:**
- `Content-Type: application/json`
- `Authorization: Bearer [ADMIN_JWT_TOKEN]`

**Request Body:**
```json
{
  "status": "shipped"
}
```

### Update to Delivered
**URL:** `PUT /api/orders/[ORDER_ID]/status`  
**Headers:**
- `Content-Type: application/json`
- `Authorization: Bearer [ADMIN_JWT_TOKEN]`

**Request Body:**
```json
{
  "status": "delivered"
}
```

---

## Step 12: Add Product Review

### Submit Product Review
**URL:** `POST /api/reviews`  
**Headers:**
- `Content-Type: application/json`
- `Authorization: Bearer [USER_JWT_TOKEN]`

**Request Body:**
```json
{
  "productId": "[PRODUCT_ID]",
  "userId": "[USER_ID]",
  "rating": 5,
  "comment": "Great product! Excellent quality and fast delivery. Highly recommended!"
}
```

---

## Additional Useful APIs for Testing

### Get All Products
**URL:** `GET /api/products`  
**Headers:** None

### Search Products
**URL:** `GET /api/products?q=smartphone`  
**Headers:** None

### Get Products by Category
**URL:** `GET /api/products?category=[CATEGORY_ID]`  
**Headers:** None

### Get User Orders
**URL:** `GET /api/orders/user/[USER_ID]`  
**Headers:** `Authorization: Bearer [USER_JWT_TOKEN]`

### Get Payment Details
**URL:** `GET /api/payments/order/[ORDER_ID]`  
**Headers:** `Authorization: Bearer [USER_JWT_TOKEN]`

### Get Product Reviews
**URL:** `GET /api/reviews/product/[PRODUCT_ID]`  
**Headers:** None

### Get User Wishlist
**URL:** `GET /api/wishlist/[USER_ID]`  
**Headers:** `Authorization: Bearer [USER_JWT_TOKEN]`

### Update Cart Quantity
**URL:** `PUT /api/cart`  
**Headers:**
- `Content-Type: application/json`
- `Authorization: Bearer [USER_JWT_TOKEN]`

**Request Body:**
```json
{
  "userId": "[USER_ID]",
  "productId": "[PRODUCT_ID]",
  "quantity": 3
}
```

### Remove Item from Cart
**URL:** `DELETE /api/cart/[CART_ITEM_ID]`  
**Headers:** `Authorization: Bearer [USER_JWT_TOKEN]`

---

## Placeholder Values to Replace:

- `[USER_ID]`: Replace with actual user ID from registration response
- `[ADMIN_JWT_TOKEN]`: Replace with JWT token from admin login response
- `[USER_JWT_TOKEN]`: Replace with JWT token from user login response
- `[CATEGORY_ID]`: Replace with actual category ID from category creation response
- `[PRODUCT_ID]`: Replace with actual product ID from product creation response
- `[ORDER_ID]`: Replace with actual order ID from order creation response
- `[CART_ITEM_ID]`: Replace with actual cart item ID from add to cart response

## Execution Order:
1. Register two users (one admin, one customer)
2. Login both users and save tokens
3. Create category (admin)
4. Create product (admin)
5. Add to cart (customer)
6. View cart (customer)
7. Add to wishlist (customer)
8. Place order (customer)
9. Process payment (customer)
10. Update order status through delivery (admin)
11. Add product review (customer)
