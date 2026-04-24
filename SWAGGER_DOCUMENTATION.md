# Swagger/OpenAPI Documentation Guide

## Overview
This document provides instructions for accessing and using the Swagger UI documentation for the Paribito E-Commerce API.

## Access URLs

### Development Environment
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Schema (JSON)**: `http://localhost:8080/v3/api-docs`

### Production Environment (Render)
- **Swagger UI**: `https://paribito-backend.onrender.com/swagger-ui.html`
- **OpenAPI Schema (JSON)**: `https://paribito-backend.onrender.com/v3/api-docs`

### Production Environment (Railway - Backup)
- **Swagger UI**: `https://paribito-backend-production.up.railway.app/swagger-ui.html`
- **OpenAPI Schema (JSON)**: `https://paribito-backend-production.up.railway.app/v3/api-docs`

## Features

### 🚀 Multiple Server Support
The Swagger UI includes dropdown menus to switch between environments:
- **Development Server**: `http://localhost:8080`
- **Production Server (Render)**: `https://paribito-backend.onrender.com`
- **Production Server (Railway)**: `https://paribito-backend-production.up.railway.app`

### 📚 API Documentation
Complete documentation for all API endpoints including:

#### 1. **Authentication** (`/api/auth/`)
- User Registration
- User Login
- Token Refresh
- Password Reset
- Email Verification

#### 2. **Products** (`/api/products/`)
- List all products with pagination
- Get product details
- Search and filter products
- Create/Update/Delete products (admin)

#### 3. **Categories** (`/api/categories/`)
- List categories
- Get category details
- Manage categories

#### 4. **Cart** (`/api/cart/`)
- Add items to cart
- Remove items from cart
- View cart
- Update cart quantities
- Clear cart

#### 5. **Wishlist** (`/api/wishlist/`)
- Add items to wishlist
- Remove items from wishlist
- View wishlist
- Clear wishlist

#### 6. **Orders** (`/api/orders/`)
- Create new order
- Get order details
- List user orders
- Update order status
- Cancel order

#### 7. **Payments** (`/api/payments/`)
- Initiate payment (Razorpay)
- Verify payment
- Get payment history
- Refund processing

#### 8. **Invoices** (`/api/invoices/`)
- Generate invoice
- Download invoice
- Email invoice
- View invoice history

#### 9. **Reviews** (`/api/reviews/`)
- Add product review
- Get product reviews
- Update review
- Delete review

#### 10. **Addresses** (`/api/addresses/`)
- Add shipping address
- Update address
- Delete address
- List user addresses

#### 11. **Bespoke Orders** (`/api/bespoke-orders/`)
- Create custom shirt order
- Upload sample designs
- Manage bespoke orders

#### 12. **Chat** (`/api/chat/`)
- Send messages to Groq AI chatbot
- Get chat responses

#### 13. **Image Upload** (`/api/images/`)
- Upload product images
- Get image URLs
- Cloudflare R2 integration

#### 14. **User** (`/api/users/`)
- Get user profile
- Update user profile
- Change password
- Delete account

#### 15. **Health Check** (`/api/health/`)
- Server health status

## How to Use Swagger UI

### 1. Open Swagger UI
Navigate to the appropriate URL based on your environment:
- Development: `http://localhost:8080/swagger-ui.html`
- Production: `https://paribito-backend.onrender.com/swagger-ui.html`

### 2. Select Environment
Using the dropdown menu at the top, select the desired server:
- Development Server
- Production Server (Render)
- Production Server (Railway)

### 3. Explore Endpoints
- Click on any endpoint to expand it
- View the request/response schema
- See required and optional parameters

### 4. Test Endpoints

#### For Endpoints Requiring Authentication:
1. First, authorize with a JWT token:
   - Click the **"Authorize"** button (lock icon) at the top
   - Enter your JWT token in format: `Bearer <YOUR_JWT_TOKEN>`
   - Click "Authorize" to confirm

2. Execute authenticated requests:
   - Fill in required parameters
   - Click **"Try it out"** button
   - Click **"Execute"** to send the request
   - View the response

#### For Public Endpoints:
1. Expand the endpoint
2. Click **"Try it out"**
3. Fill in required parameters
4. Click **"Execute"**
5. View the response

### 5. View Response
After executing a request, you'll see:
- **Status Code**: HTTP response status
- **Response Body**: JSON response with data
- **Response Headers**: HTTP headers
- **cURL**: Equivalent cURL command for the request

## API Authentication

Most endpoints require JWT (JSON Web Token) authentication. Here's how to authenticate:

### Step 1: Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password"
  }'
```

### Step 2: Get JWT Token
Response will include:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "...",
  "user": { ... }
}
```

### Step 3: Use Token in Swagger
1. Click **"Authorize"** button
2. Paste token in format: `Bearer <YOUR_JWT_TOKEN>`
3. All subsequent requests will include this token

## Configuration

### Enable/Disable Swagger UI

To disable Swagger UI in production, update `application.properties`:

```properties
springdoc.swagger-ui.enabled=false
```

Or use environment variable:
```bash
SWAGGER_UI_ENABLED=false
```

### Customize Swagger Documentation

The OpenAPI configuration is defined in:
- File: `src/main/java/com/ecommerce/project/config/OpenApiConfig.java`

To customize:
1. Edit the OpenApiConfig class
2. Update API title, description, contact info
3. Add/modify server URLs
4. Rebuild and redeploy

## Endpoints Documentation

### Common Response Codes

- **200 OK**: Request successful
- **201 Created**: Resource created successfully
- **400 Bad Request**: Invalid request parameters
- **401 Unauthorized**: Missing or invalid JWT token
- **403 Forbidden**: User doesn't have permission
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

### Authentication Header Format

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Testing in Different Environments

### Development (Local)
1. Start the application: `mvn spring-boot:run`
2. Access Swagger: `http://localhost:8080/swagger-ui.html`
3. Select "Development Server" from dropdown

### Production (Render)
1. Access Swagger: `https://paribito-backend.onrender.com/swagger-ui.html`
2. Select "Production Server" from dropdown

### Testing Production Without Frontend
```bash
# Get login token
curl -X POST https://paribito-backend.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "password"}'

# Use token in subsequent requests
curl -X GET https://paribito-backend.onrender.com/api/users/profile \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## OpenAPI Schema Download

### Download JSON Schema
```bash
# Development
curl http://localhost:8080/v3/api-docs -o dev-api-schema.json

# Production
curl https://paribito-backend.onrender.com/v3/api-docs -o prod-api-schema.json
```

### Download YAML Schema
Swagger UI automatically converts JSON to YAML and can be downloaded.

## Integration with Tools

### Postman
1. Download OpenAPI schema (JSON)
2. In Postman: File → Import → Upload JSON file
3. All endpoints will be imported with schemas

### IntelliJ IDE
1. Install OpenAPI plugin
2. Open OpenAPI schema URL
3. IDE will provide code generation and IDE hints

## Troubleshooting

### Swagger UI Not Loading
1. Check if application is running
2. Verify URL is correct
3. Check `springdoc.swagger-ui.enabled` property
4. Check CORS configuration

### Endpoints Not Appearing
1. Ensure `@RestController` annotation is present
2. Verify `@RequestMapping` is configured
3. Operations should have `@Operation` annotation
4. Rebuild application if using IDE

### Authentication Issues
1. Verify JWT token is valid
2. Check token format: `Bearer <TOKEN>`
3. Ensure token hasn't expired
4. Verify Authorization header is set

### CORS Errors
Check `cors.allowed-origins` in `application.properties`:
```properties
cors.allowed-origins=http://localhost:5173,http://localhost:3000,https://paribito-backend.onrender.com
```

## Additional Resources

- **OpenAPI Specification**: https://swagger.io/specification/
- **SpringDoc Documentation**: https://springdoc.org/
- **JWT Documentation**: https://jwt.io/
- **Razorpay API Docs**: https://razorpay.com/docs/
- **MongoDB Documentation**: https://docs.mongodb.com/

## Support

For API support or questions:
- Email: aditaenterpriseindia@gmail.com
- Website: https://theparibito.com
- Documentation: This repository's README.md

---

**Last Updated**: April 2026
**API Version**: 1.0.0
**Spring Boot Version**: 3.5.8
