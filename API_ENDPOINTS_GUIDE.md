# E-Commerce API Endpoints Guide

Base URL: `http://localhost:8080`

---

## 1. Authentication APIs

### 1.1 Register User
**Endpoint:** `POST /api/auth/register`  
**Description:** Register a new user account  
**Authentication:** None required  

**Request Body:**
```json
{
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "password": "SecurePass123!",
  "phone": "+1234567890"
}
```

**Response:** 201 Created
```json
{
  "id": "user123",
  "email": "john.doe@example.com",
  "fullName": "John Doe",
  "phone": "+1234567890",
  "role": "CUSTOMER",
  "createdAt": "2025-11-24T10:00:00Z",
  "addresses": []
}
```

---

### 1.2 Login User
**Endpoint:** `POST /api/auth/login`  
**Description:** Authenticate user and get JWT token  
**Authentication:** None required  

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePass123!"
}
```

**Response:** 200 OK
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "user123",
    "email": "john.doe@example.com",
    "fullName": "John Doe",
    "phone": "+1234567890",
    "role": "CUSTOMER",
    "createdAt": "2025-11-24T10:00:00Z",
    "addresses": []
  }
}
```

---

### 1.3 Register Admin
**Endpoint:** `POST /api/auth/register`  
**Description:** Register an admin account  
**Authentication:** None required  

**Request Body:**
```json
{
  "fullName": "Admin User",
  "email": "admin@ecommerce.com",
  "password": "AdminPass123!",
  "phone": "+1234567891"
}
```

**Note:** Role assignment might be handled differently. Check UserService implementation.

---

## 2. Category APIs

### 2.1 Create Category
**Endpoint:** `POST /api/categories`  
**Description:** Create a new product category  
**Authentication:** Required (ADMIN role)  
**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "name": "Electronics"
}
```

**Response:** 201 Created
```json
{
  "id": "cat123",
  "name": "Electronics",
  "createdAt": "2025-11-24T10:00:00Z"
}
```

---

### 2.2 Get All Categories
**Endpoint:** `GET /api/categories`  
**Description:** Retrieve all categories  
**Authentication:** None required  

**Response:** 200 OK
```json
[
  {
    "id": "cat123",
    "name": "Electronics",
    "createdAt": "2025-11-24T10:00:00Z"
  },
  {
    "id": "cat124",
    "name": "Clothing",
    "createdAt": "2025-11-24T10:05:00Z"
  }
]
```

---

### 2.3 Get Category by ID
**Endpoint:** `GET /api/categories/{id}`  
**Description:** Get a specific category  
**Authentication:** None required  
**Path Parameter:** `id` - Category ID

**Example:** `GET /api/categories/cat123`

**Response:** 200 OK
```json
{
  "id": "cat123",
  "name": "Electronics",
  "createdAt": "2025-11-24T10:00:00Z"
}
```

---

### 2.4 Update Category
**Endpoint:** `PUT /api/categories/{id}`  
**Description:** Update category name  
**Authentication:** Required (ADMIN role)  
**Headers:** `Authorization: Bearer <token>`  
**Path Parameter:** `id` - Category ID

**Request Body:**
```json
{
  "name": "Consumer Electronics"
}
```

**Response:** 200 OK
```json
{
  "id": "cat123",
  "name": "Consumer Electronics",
  "createdAt": "2025-11-24T10:00:00Z"
}
```

---

### 2.5 Delete Category
**Endpoint:** `DELETE /api/categories/{id}`  
**Description:** Delete a category  
**Authentication:** Required (ADMIN role)  
**Headers:** `Authorization: Bearer <token>`  
**Path Parameter:** `id` - Category ID

**Example:** `DELETE /api/categories/cat123`

**Response:** 204 No Content

---

## 3. Product APIs

### 3.1 Create Product
**Endpoint:** `POST /api/products`  
**Description:** Create a new product  
**Authentication:** Required (ADMIN role)  
**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "name": "Wireless Headphones",
  "description": "High-quality Bluetooth headphones with noise cancellation",
  "categoryId": "cat123",
  "price": 99.99,
  "stockQuantity": 50,
  "imageUrls": [
    "https://example.com/images/headphones1.jpg",
    "https://example.com/images/headphones2.jpg"
  ],
  "size": "MEDIUM",
  "color": "Black"
}
```

**Note:** Size values: SMALL, MEDIUM, LARGE, XL, XXL

**Response:** 201 Created
```json
{
  "id": "prod123",
  "name": "Wireless Headphones",
  "description": "High-quality Bluetooth headphones with noise cancellation",
  "categoryId": "cat123",
  "price": 99.99,
  "stockQuantity": 50,
  "imageUrls": [
    "https://example.com/images/headphones1.jpg",
    "https://example.com/images/headphones2.jpg"
  ],
  "size": "MEDIUM",
  "color": "Black",
  "createdAt": "2025-11-24T10:00:00Z"
}
```

---

### 3.2 Get All Products
**Endpoint:** `GET /api/products`  
**Description:** Get all products  
**Authentication:** None required  

**Response:** 200 OK
```json
[
  {
    "id": "prod123",
    "name": "Wireless Headphones",
    "description": "High-quality Bluetooth headphones",
    "categoryId": "cat123",
    "price": 99.99,
    "stockQuantity": 50,
    "imageUrls": ["https://example.com/images/headphones1.jpg"],
    "size": "MEDIUM",
    "color": "Black"
  }
]
```

---

### 3.3 Get Product by ID
**Endpoint:** `GET /api/products/{id}`  
**Description:** Get a specific product  
**Authentication:** None required  
**Path Parameter:** `id` - Product ID

**Example:** `GET /api/products/prod123`

**Response:** 200 OK
```json
{
  "id": "prod123",
  "name": "Wireless Headphones",
  "description": "High-quality Bluetooth headphones",
  "categoryId": "cat123",
  "price": 99.99,
  "stockQuantity": 50,
  "imageUrls": ["https://example.com/images/headphones1.jpg"],
  "size": "MEDIUM",
  "color": "Black"
}
```

---

### 3.4 Search Products
**Endpoint:** `GET /api/products?q={searchTerm}`  
**Description:** Search products by name or description  
**Authentication:** None required  
**Query Parameter:** `q` - Search term

**Example:** `GET /api/products?q=headphones`

**Response:** 200 OK
```json
[
  {
    "id": "prod123",
    "name": "Wireless Headphones",
    "description": "High-quality Bluetooth headphones",
    "price": 99.99
  }
]
```

---

### 3.5 Get Products by Category
**Endpoint:** `GET /api/products?category={categoryId}`  
**Description:** Get all products in a category  
**Authentication:** None required  
**Query Parameter:** `category` - Category ID

**Example:** `GET /api/products?category=cat123`

**Response:** 200 OK
```json
[
  {
    "id": "prod123",
    "name": "Wireless Headphones",
    "categoryId": "cat123",
    "price": 99.99
  }
]
```

---

### 3.6 Update Product
**Endpoint:** `PUT /api/products/{id}`  
**Description:** Update product details  
**Authentication:** Required (ADMIN role)  
**Headers:** `Authorization: Bearer <token>`  
**Path Parameter:** `id` - Product ID

**Request Body:**
```json
{
  "name": "Wireless Headphones Pro",
  "description": "Premium Bluetooth headphones with active noise cancellation",
  "categoryId": "cat123",
  "price": 129.99,
  "stockQuantity": 75,
  "imageUrls": [
    "https://example.com/images/headphones-pro1.jpg"
  ],
  "size": "LARGE",
  "color": "Silver"
}
```

**Response:** 200 OK

---

### 3.7 Delete Product
**Endpoint:** `DELETE /api/products/{id}`  
**Description:** Delete a product  
**Authentication:** Required (ADMIN role)  
**Headers:** `Authorization: Bearer <token>`  
**Path Parameter:** `id` - Product ID

**Example:** `DELETE /api/products/prod123`

**Response:** 204 No Content

---

## 4. Cart APIs

### 4.1 Add to Cart
**Endpoint:** `POST /api/cart`  
**Description:** Add a product to user's cart  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "userId": "user123",
  "productId": "prod123",
  "quantity": 2
}
```

**Response:** 201 Created
```json
{
  "id": "cart123",
  "userId": "user123",
  "productId": "prod123",
  "quantity": 2,
  "addedAt": "2025-11-24T10:00:00Z"
}
```

---

### 4.2 Update Cart Item Quantity
**Endpoint:** `PUT /api/cart`  
**Description:** Update quantity of a cart item  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "userId": "user123",
  "productId": "prod123",
  "quantity": 5
}
```

**Response:** 200 OK
```json
{
  "id": "cart123",
  "userId": "user123",
  "productId": "prod123",
  "quantity": 5,
  "addedAt": "2025-11-24T10:00:00Z"
}
```

---

### 4.3 Get User Cart
**Endpoint:** `GET /api/cart/{userId}`  
**Description:** Get all items in user's cart  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`  
**Path Parameter:** `userId` - User ID

**Example:** `GET /api/cart/user123`

**Response:** 200 OK
```json
[
  {
    "id": "cart123",
    "userId": "user123",
    "productId": "prod123",
    "quantity": 2,
    "addedAt": "2025-11-24T10:00:00Z"
  },
  {
    "id": "cart124",
    "userId": "user123",
    "productId": "prod124",
    "quantity": 1,
    "addedAt": "2025-11-24T10:05:00Z"
  }
]
```

---

### 4.4 Remove from Cart
**Endpoint:** `DELETE /api/cart/{cartItemId}`  
**Description:** Remove an item from cart  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`  
**Path Parameter:** `cartItemId` - Cart Item ID

**Example:** `DELETE /api/cart/cart123`

**Response:** 204 No Content

---

### 4.5 Clear Cart
**Endpoint:** `DELETE /api/cart/{userId}/clear`  
**Description:** Remove all items from user's cart  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`  
**Path Parameter:** `userId` - User ID

**Example:** `DELETE /api/cart/user123/clear`

**Response:** 204 No Content

---

## 5. Order APIs

### 5.1 Place Order
**Endpoint:** `POST /api/orders`  
**Description:** Create a new order  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "userId": "user123",
  "address": {
    "street": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
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

**Response:** 201 Created
```json
{
  "id": "order123",
  "userId": "user123",
  "address": {
    "street": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  },
  "totalAmount": 199.98,
  "status": "pending",
  "items": [
    {
      "productId": "prod123",
      "quantity": 2,
      "price": 99.99
    }
  ],
  "createdAt": "2025-11-24T10:00:00Z"
}
```

---

### 5.2 Get User Orders
**Endpoint:** `GET /api/orders/user/{userId}`  
**Description:** Get all orders for a user  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`  
**Path Parameter:** `userId` - User ID

**Example:** `GET /api/orders/user/user123`

**Response:** 200 OK
```json
[
  {
    "id": "order123",
    "userId": "user123",
    "totalAmount": 199.98,
    "status": "pending",
    "createdAt": "2025-11-24T10:00:00Z"
  }
]
```

---

### 5.3 Get Order by ID
**Endpoint:** `GET /api/orders/{orderId}`  
**Description:** Get order details  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`  
**Path Parameter:** `orderId` - Order ID

**Example:** `GET /api/orders/order123`

**Response:** 200 OK
```json
{
  "id": "order123",
  "userId": "user123",
  "address": {
    "street": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  },
  "totalAmount": 199.98,
  "status": "pending",
  "items": [
    {
      "productId": "prod123",
      "quantity": 2,
      "price": 99.99
    }
  ],
  "createdAt": "2025-11-24T10:00:00Z"
}
```

---

### 5.4 Update Order Status
**Endpoint:** `PUT /api/orders/{orderId}/status`  
**Description:** Update order status (Admin only)  
**Authentication:** Required (ADMIN role)  
**Headers:** `Authorization: Bearer <token>`  
**Path Parameter:** `orderId` - Order ID

**Request Body:**
```json
{
  "status": "shipped"
}
```

**Status values:** pending, processing, shipped, delivered, cancelled

**Response:** 200 OK
```json
{
  "id": "order123",
  "userId": "user123",
  "status": "shipped",
  "totalAmount": 199.98,
  "updatedAt": "2025-11-24T11:00:00Z"
}
```

---

### 5.5 Get All Orders (Admin)
**Endpoint:** `GET /api/orders`  
**Description:** Get all orders (Admin only)  
**Authentication:** Required (ADMIN role)  
**Headers:** `Authorization: Bearer <token>`

**Response:** 200 OK
```json
[
  {
    "id": "order123",
    "userId": "user123",
    "totalAmount": 199.98,
    "status": "pending",
    "createdAt": "2025-11-24T10:00:00Z"
  },
  {
    "id": "order124",
    "userId": "user124",
    "totalAmount": 299.99,
    "status": "shipped",
    "createdAt": "2025-11-24T09:00:00Z"
  }
]
```

---

## 6. Payment APIs

### 6.1 Process Payment
**Endpoint:** `POST /api/payments`  
**Description:** Process payment for an order  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "orderId": "order123",
  "amount": 199.98,
  "paymentMethod": "CREDIT_CARD"
}
```

**Payment Methods:** CREDIT_CARD, DEBIT_CARD, PAYPAL, UPI, NET_BANKING

**Response:** 201 Created
```json
{
  "id": "pay123",
  "orderId": "order123",
  "amount": 199.98,
  "paymentMethod": "CREDIT_CARD",
  "status": "SUCCESS",
  "transactionId": "txn_abc123",
  "createdAt": "2025-11-24T10:00:00Z"
}
```

---

### 6.2 Get Payment by Order ID
**Endpoint:** `GET /api/payments/order/{orderId}`  
**Description:** Get payment details for an order  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`  
**Path Parameter:** `orderId` - Order ID

**Example:** `GET /api/payments/order/order123`

**Response:** 200 OK
```json
{
  "id": "pay123",
  "orderId": "order123",
  "amount": 199.98,
  "paymentMethod": "CREDIT_CARD",
  "status": "SUCCESS",
  "transactionId": "txn_abc123",
  "createdAt": "2025-11-24T10:00:00Z"
}
```

---

### 6.3 Get Payment by Payment ID
**Endpoint:** `GET /api/payments/{paymentId}`  
**Description:** Get payment details by payment ID  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`  
**Path Parameter:** `paymentId` - Payment ID

**Example:** `GET /api/payments/pay123`

**Response:** 200 OK
```json
{
  "id": "pay123",
  "orderId": "order123",
  "amount": 199.98,
  "paymentMethod": "CREDIT_CARD",
  "status": "SUCCESS",
  "transactionId": "txn_abc123",
  "createdAt": "2025-11-24T10:00:00Z"
}
```

---

### 6.4 Get All Payments (Admin)
**Endpoint:** `GET /api/payments`  
**Description:** Get all payments (Admin only)  
**Authentication:** Required (ADMIN role)  
**Headers:** `Authorization: Bearer <token>`

**Response:** 200 OK
```json
[
  {
    "id": "pay123",
    "orderId": "order123",
    "amount": 199.98,
    "paymentMethod": "CREDIT_CARD",
    "status": "SUCCESS",
    "createdAt": "2025-11-24T10:00:00Z"
  }
]
```

---

## 7. Review APIs

### 7.1 Add Review
**Endpoint:** `POST /api/reviews`  
**Description:** Add a product review  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "productId": "prod123",
  "userId": "user123",
  "rating": 5,
  "comment": "Excellent product! Highly recommended."
}
```

**Rating:** 1-5 (integer)

**Response:** 201 Created
```json
{
  "id": "rev123",
  "productId": "prod123",
  "userId": "user123",
  "rating": 5,
  "comment": "Excellent product! Highly recommended.",
  "createdAt": "2025-11-24T10:00:00Z"
}
```

---

### 7.2 Get Product Reviews
**Endpoint:** `GET /api/reviews/product/{productId}`  
**Description:** Get all reviews for a product  
**Authentication:** None required  
**Path Parameter:** `productId` - Product ID

**Example:** `GET /api/reviews/product/prod123`

**Response:** 200 OK
```json
[
  {
    "id": "rev123",
    "productId": "prod123",
    "userId": "user123",
    "rating": 5,
    "comment": "Excellent product!",
    "createdAt": "2025-11-24T10:00:00Z"
  },
  {
    "id": "rev124",
    "productId": "prod123",
    "userId": "user124",
    "rating": 4,
    "comment": "Good quality",
    "createdAt": "2025-11-24T09:00:00Z"
  }
]
```

---

### 7.3 Get User Reviews
**Endpoint:** `GET /api/reviews/user/{userId}`  
**Description:** Get all reviews by a user  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`  
**Path Parameter:** `userId` - User ID

**Example:** `GET /api/reviews/user/user123`

**Response:** 200 OK
```json
[
  {
    "id": "rev123",
    "productId": "prod123",
    "userId": "user123",
    "rating": 5,
    "comment": "Excellent product!",
    "createdAt": "2025-11-24T10:00:00Z"
  }
]
```

---

### 7.4 Update Review
**Endpoint:** `PUT /api/reviews/{reviewId}`  
**Description:** Update a review  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`  
**Path Parameter:** `reviewId` - Review ID

**Request Body:**
```json
{
  "rating": 4,
  "comment": "Good product, but could be better."
}
```

**Response:** 200 OK
```json
{
  "id": "rev123",
  "productId": "prod123",
  "userId": "user123",
  "rating": 4,
  "comment": "Good product, but could be better.",
  "updatedAt": "2025-11-24T11:00:00Z"
}
```

---

### 7.5 Delete Review
**Endpoint:** `DELETE /api/reviews/{reviewId}`  
**Description:** Delete a review  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`  
**Path Parameter:** `reviewId` - Review ID

**Example:** `DELETE /api/reviews/rev123`

**Response:** 204 No Content

---

## 8. Wishlist APIs

### 8.1 Add to Wishlist
**Endpoint:** `POST /api/wishlist`  
**Description:** Add a product to wishlist  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "userId": "user123",
  "productId": "prod123"
}
```

**Response:** 201 Created
```json
{
  "id": "wish123",
  "userId": "user123",
  "productId": "prod123",
  "addedAt": "2025-11-24T10:00:00Z"
}
```

---

### 8.2 Get User Wishlist
**Endpoint:** `GET /api/wishlist/{userId}`  
**Description:** Get all items in user's wishlist  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`  
**Path Parameter:** `userId` - User ID

**Example:** `GET /api/wishlist/user123`

**Response:** 200 OK
```json
[
  {
    "id": "wish123",
    "userId": "user123",
    "productId": "prod123",
    "addedAt": "2025-11-24T10:00:00Z"
  },
  {
    "id": "wish124",
    "userId": "user123",
    "productId": "prod124",
    "addedAt": "2025-11-24T09:00:00Z"
  }
]
```

---

### 8.3 Remove from Wishlist
**Endpoint:** `DELETE /api/wishlist/{wishlistItemId}`  
**Description:** Remove an item from wishlist  
**Authentication:** Required  
**Headers:** `Authorization: Bearer <token>`  
**Path Parameter:** `wishlistItemId` - Wishlist Item ID

**Example:** `DELETE /api/wishlist/wish123`

**Response:** 204 No Content

---

## Notes

1. **Authentication:** Most endpoints require JWT token in the Authorization header
2. **Admin Endpoints:** Endpoints marked with (ADMIN role) require admin privileges
3. **Base URL:** All endpoints use `http://localhost:8080` as base URL
4. **Content-Type:** All POST/PUT requests should include `Content-Type: application/json` header
5. **Error Responses:** API returns appropriate HTTP status codes (400, 401, 404, 409, 500) with error messages
