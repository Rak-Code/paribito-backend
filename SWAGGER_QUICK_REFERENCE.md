# Swagger/OpenAPI Quick Reference

## Quick Access Links

| Environment | Swagger UI | API Docs (JSON) |
|-------------|-----------|-----------------|
| **Local Dev** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) |
| **Production (Render)** | [https://paribito-backend.onrender.com/swagger-ui.html](https://paribito-backend.onrender.com/swagger-ui.html) | [https://paribito-backend.onrender.com/v3/api-docs](https://paribito-backend.onrender.com/v3/api-docs) |
| **Production (Railway)** | [https://paribito-backend-production.up.railway.app/swagger-ui.html](https://paribito-backend-production.up.railway.app/swagger-ui.html) | [https://paribito-backend-production.up.railway.app/v3/api-docs](https://paribito-backend-production.up.railway.app/v3/api-docs) |

## Common API Base URLs

```
Development:    http://localhost:8080/api
Production:     https://paribito-backend.onrender.com/api
Backup Prod:    https://paribito-backend-production.up.railway.app/api
```

## Key API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Refresh token
- `POST /api/auth/logout` - User logout

### Products
- `GET /api/products` - List products (paginated)
- `GET /api/products/{id}` - Get product details
- `GET /api/products/category/{categoryId}` - Get products by category

### Cart
- `POST /api/cart/add` - Add item to cart
- `GET /api/cart` - View cart
- `DELETE /api/cart/remove/{itemId}` - Remove item from cart

### Orders
- `POST /api/orders` - Create order
- `GET /api/orders` - List user orders
- `GET /api/orders/{id}` - Get order details

### Payments
- `POST /api/payments/initiate` - Initiate payment
- `POST /api/payments/verify` - Verify payment

### Invoices
- `GET /api/invoices/{orderId}` - Get invoice
- `POST /api/invoices/{orderId}/email` - Email invoice

## Authorization Header

Add to all authenticated requests:
```
Authorization: Bearer <JWT_TOKEN>
```

## Example cURL Request

### Get Login Token
```bash
curl -X POST https://paribito-backend.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password"
  }'
```

### Use Token in Request
```bash
curl -X GET https://paribito-backend.onrender.com/api/users/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Testing Checklist

- [ ] Swagger UI loads successfully
- [ ] Environment dropdown shows all 3 servers
- [ ] Can toggle between Development and Production
- [ ] Authentication (Authorize) button works
- [ ] Public endpoints can be tested
- [ ] Protected endpoints require token
- [ ] Responses match API schema
- [ ] CORS headers are present

## Environment Configuration

### Required Environment Variables (Production)

```bash
# JWT
JWT_SECRET=<strong-random-secret>

# MongoDB
MONGODB_URI=<mongodb-connection-string>

# Razorpay
RAZORPAY_KEY_ID=<razorpay-key>
RAZORPAY_KEY_SECRET=<razorpay-secret>

# AWS SES
AWS_ACCESS_KEY_ID=<aws-key>
AWS_SECRET_ACCESS_KEY=<aws-secret>
AWS_REGION=ap-south-1

# Cloudflare R2
R2_ACCOUNT_ID=<r2-account-id>
R2_ACCESS_KEY_ID=<r2-key>
R2_SECRET_ACCESS_KEY=<r2-secret>
R2_BUCKET_NAME=<bucket-name>

# Email
FROM_EMAIL=no-reply@theparibito.com
ADMIN_EMAIL=admin@example.com

# App URL
APP_URL=https://paribito-backend.onrender.com

# Groq AI
GROQ_API_KEY=<groq-api-key>
```

## Features of Swagger UI

✅ **Server Selection**: Switch between environments  
✅ **Authorization**: Add JWT tokens with one click  
✅ **Try It Out**: Test endpoints directly from UI  
✅ **Schema Validation**: See request/response schemas  
✅ **cURL Export**: Get equivalent cURL commands  
✅ **Search**: Find endpoints quickly  
✅ **Response Codes**: View all possible HTTP responses  
✅ **Model Definitions**: See data models and types  

## Support & Troubleshooting

| Issue | Solution |
|-------|----------|
| Swagger not loading | Check if app is running and URL is correct |
| Endpoints missing | Rebuild project and restart |
| Auth issues | Verify JWT token is valid and not expired |
| CORS errors | Check CORS configuration in application.properties |
| 404 on endpoints | Verify base URL matches selected server |

---

**Version**: 1.0.0  
**Last Updated**: April 2026  
**API Base Version**: 3.x
