# E-Commerce API Postman Test Collection

This collection provides a comprehensive set of API tests for the e-commerce Spring Boot application, covering the complete user journey from registration to product review.

## Prerequisites

1. **Application Setup**: Make sure the Spring Boot application is running on `http://localhost:8080`
2. **MongoDB**: Ensure MongoDB is running and properly configured
3. **Admin Role Setup**: Since the registration endpoint defaults users to `USER` role, you'll need to manually set an admin user in the database:
   ```javascript
   // In MongoDB, update a user document:
   db.users.updateOne(
     { email: "admin@example.com" },
     { $set: { role: "ADMIN" } }
   )
   ```

## Collection Structure

The collection is organized into 12 main folders representing the step-by-step workflow:

### 1. Admin Registration
- Register an admin user (note: role defaults to USER, requires manual database update)

### 2. User Registration
- Register a regular user
- Add address to user profile

### 3. User Login
- Authenticate user and obtain JWT token

### 4. Admin Setup
- Login as admin (requires manual role assignment)
- Obtain admin JWT token for protected operations

### 5. Category Creation
- Create product categories (Admin only)
- Categories are required before creating products

### 6. Product Creation
- Create products (Admin only)
- Products belong to categories created in step 5

### 7. Cart Operations
- Add products to cart
- View cart contents
- Update cart item quantities

### 8. Wishlist Operations
- Add/remove products from wishlist
- View user wishlist

### 9. Order Placement
- Place orders with cart items
- Include shipping address
- Calculate total amounts

### 10. Payment Processing
- Process payments for orders
- Support multiple payment methods (credit_card, debit_card, upi, etc.)

### 11. Order Status Update (Admin Only)
- Update order status from pending → processing → shipped → delivered
- Admin-only operation to simulate delivery completion

### 12. Product Review
- Add reviews for purchased products
- View product reviews

### Additional Test Scenarios
- Product search and filtering
- Category-based product listing
- Cart/wishlist management operations
- Payment verification

## Important Notes

### Role-Based Access Control
- **Admin-only endpoints**: Category creation, Product CRUD, Order status updates
- **User-only endpoints**: Cart operations, Wishlist operations, Order placement
- **Public endpoints**: Product listing/search, User registration/login

### JSON Payloads
- All request bodies are in JSON format
- Authentication uses Bearer tokens in Authorization header
- Response data is automatically captured in collection variables for use in subsequent requests

### Collection Variables
The collection uses dynamic variables that are automatically populated:
- `baseUrl`: Application base URL
- `adminToken`: JWT token for admin operations
- `userToken`: JWT token for user operations
- IDs for created entities (categoryId, productId, orderId, etc.)

### Order of Execution
Execute requests in the numbered folder order (1-12) for a complete workflow. The "Additional Test Scenarios" folder contains supplementary tests that can be run at any time.

### Status Flow
Order status progression (admin-controlled):
1. `pending` (default)
2. `processing`
3. `shipped`
4. `delivered`

### Sample Data
The collection includes realistic sample data:
- Electronics category
- Smartphone product ($599.99)
- User addresses
- Payment information

## How to Import and Use

1. **Import Collection**:
   - Open Postman
   - Click "Import" → "File" or "Link"
   - Select the `postman-test-scenarios.json` file

2. **Configure Environment**:
   - Set `baseUrl` variable to your application URL (default: `http://localhost:8080`)

3. **Execute Tests**:
   - Run requests in order (start with admin setup)
   - Tests will automatically save response data to variables
   - Use Postman's "Runner" to execute entire folders

4. **Authentication Flow**:
   - Register users first
   - Login to get JWT tokens
   - Include tokens in Authorization headers for protected endpoints

## API Endpoints Summary

| Endpoint | Method | Role Required | Description |
|----------|--------|---------------|-------------|
| `/api/auth/register` | POST | None | User registration |
| `/api/auth/login` | POST | None | User authentication |
| `/api/categories` | POST | ADMIN | Create category |
| `/api/products` | POST | ADMIN | Create product |
| `/api/cart` | POST/PUT | USER | Cart operations |
| `/api/wishlist` | POST | USER | Wishlist operations |
| `/api/orders` | POST | USER | Place order |
| `/api/orders/{id}/status` | PUT | ADMIN | Update order status |
| `/api/payments` | POST | USER | Process payment |
| `/api/reviews` | POST | USER | Add review |
| `/api/products` | GET | None | List/search products |

## Error Handling
The collection includes test scripts that validate:
- HTTP status codes
- Response structure
- Variable extraction for chaining requests

## Troubleshooting

**"403 Forbidden" errors**: Check if you're using the correct token (admin vs user) and that the user has the required role.

**"401 Unauthorized"**: Ensure you're logged in and the JWT token is valid.

**Missing variables**: Run requests in order and ensure tests are enabled to populate variables.

**Port issues**: Verify the application is running on the correct port and update `baseUrl` if necessary.

## Security Features Tested

- JWT-based authentication
- Role-based authorization (ADMIN/USER)
- Protected endpoints validation
- Token expiry handling (implicit in JWT implementation)
