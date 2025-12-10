# Pagination Implementation Summary

## Overview
Added pagination support to all `findAll` and `getAll` methods across the application. The implementation maintains backward compatibility by keeping the original non-paginated methods while adding new paginated versions.

## Changes Made

### 1. User Management
**Endpoint:** `GET /api/users`
- **Paginated:** `?page=0&size=10&sortBy=id&sortDirection=ASC`
- **Non-paginated:** No query parameters (returns all users)

### 2. Categories
**Endpoint:** `GET /api/categories`
- **Paginated:** `?page=0&size=10&sortBy=name&sortDirection=ASC`
- **Non-paginated:** No query parameters (returns all categories)

### 3. Orders
**Endpoints:**
- `GET /api/orders` (Admin only)
  - **Paginated:** `?page=0&size=10&sortBy=orderDate&sortDirection=DESC`
  - **Non-paginated:** No query parameters
  
- `GET /api/orders/user/{userId}`
  - **Paginated:** `?page=0&size=10&sortBy=orderDate&sortDirection=DESC`
  - **Non-paginated:** No query parameters

### 4. Payments
**Endpoint:** `GET /api/payments` (Admin only)
- **Paginated:** `?page=0&size=10&sortBy=paymentDate&sortDirection=DESC`
- **Non-paginated:** No query parameters (returns all payments)

### 5. Reviews
**Endpoint:** `GET /api/reviews/product/{productId}`
- **Paginated:** `?page=0&size=10&sortBy=reviewDate&sortDirection=DESC`
- **Non-paginated:** No query parameters (returns all reviews for product)

## Query Parameters

All paginated endpoints support the following optional query parameters:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | Integer | - | Page number (0-indexed) |
| `size` | Integer | - | Number of items per page |
| `sortBy` | String | Varies by endpoint | Field to sort by |
| `sortDirection` | String | ASC/DESC | Sort direction (ASC or DESC) |

## Response Format

### Paginated Response
When pagination parameters are provided, the response follows Spring's `Page` format:

```json
{
  "content": [...],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 5,
  "totalElements": 50,
  "last": false,
  "first": true,
  "size": 10,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 10,
  "empty": false
}
```

### Non-Paginated Response
When no pagination parameters are provided, the response is a simple array:

```json
[...]
```

## Implementation Details

### Service Layer
- Added overloaded methods accepting `Pageable` parameter
- Kept original methods for backward compatibility
- Example:
  ```java
  List<User> getAllUsers();
  Page<User> getAllUsers(Pageable pageable);
  ```

### Repository Layer
- MongoDB repositories automatically support pagination via `MongoRepository`
- Added custom paginated methods where needed (e.g., `findByUserId(String userId, Pageable pageable)`)

### Controller Layer
- Controllers check if pagination parameters are provided
- If `page` and `size` are present, use paginated service method
- Otherwise, use non-paginated method
- Returns `ResponseEntity<?>` to support both response types

## Backward Compatibility

All existing API calls without pagination parameters will continue to work as before, returning complete lists. This ensures no breaking changes for existing clients.

## Usage Examples

### Get first page of users (10 per page)
```
GET /api/users?page=0&size=10
```

### Get second page of orders sorted by date descending
```
GET /api/orders?page=1&size=20&sortBy=orderDate&sortDirection=DESC
```

### Get all categories (non-paginated)
```
GET /api/categories
```

### Get product reviews with pagination
```
GET /api/reviews/product/123?page=0&size=5&sortBy=rating&sortDirection=DESC
```

## Files Modified

### Services (Interfaces)
- `UserService.java`
- `CategoryService.java`
- `OrderService.java`
- `PaymentService.java`
- `ReviewService.java`

### Services (Implementations)
- `UserServiceImpl.java`
- `CategoryServiceImpl.java`
- `OrderServiceImpl.java`
- `PaymentServiceImpl.java`
- `ReviewServiceImpl.java`

### Controllers
- `UserController.java`
- `CategoryController.java`
- `OrderController.java`
- `PaymentController.java`
- `ReviewController.java`

### Repositories
- `OrderRepository.java` - Added `Page<Order> findByUserId(String userId, Pageable pageable)`
- `ReviewRepository.java` - Added `Page<Review> findByProductId(String productId, Pageable pageable)`

## Notes

- Products already had pagination implemented
- Cart, Wishlist, and user-specific endpoints (like user addresses) were not paginated as they typically return small result sets
- All pagination uses 0-based page indexing (first page is 0)
- Default sort directions vary by endpoint to provide the most logical ordering
