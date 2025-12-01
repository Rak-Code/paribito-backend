# Razorpay Integration - Quick Start Guide

## 🚀 Quick Setup (5 Minutes)

### Step 1: Get Razorpay Credentials
1. Sign up at https://dashboard.razorpay.com/
2. Go to **Settings → API Keys**
3. Generate **Test Mode** keys
4. Copy **Key ID** and **Key Secret**

### Step 2: Configure Your Application
Edit `src/main/resources/application.properties`:

```properties
razorpay.key.id=rzp_test_YOUR_KEY_ID
razorpay.key.secret=YOUR_KEY_SECRET
razorpay.currency=INR
```

### Step 3: Build the Project
```bash
mvn clean install
```

### Step 4: Run the Application
```bash
mvn spring-boot:run
```

## 🧪 Testing

### Option 1: Using the HTML Test Page
1. Open `razorpay-checkout-example.html` in your browser
2. Get a JWT token by logging in to your application
3. Enter order details and JWT token
4. Click "Pay Now"
5. Use test card: **4111 1111 1111 1111**

### Option 2: Using Postman
1. Import `Razorpay_Postman_Collection.json`
2. Set variables:
   - `base_url`: http://localhost:8080
   - `jwt_token`: Your JWT token
3. Run "Create Razorpay Order" request
4. Use the response to test payment on Razorpay checkout

## 📝 Test Card Details

**Card Number:** 4111 1111 1111 1111  
**CVV:** Any 3 digits  
**Expiry:** Any future date  
**Name:** Any name

**Test UPI:** success@razorpay  
**Test Netbanking:** Select any bank

## 🔄 Payment Flow

```
1. Create Order → POST /api/payments/razorpay/create-order
   ↓
2. Open Razorpay Checkout (Frontend)
   ↓
3. User Completes Payment
   ↓
4. Verify Payment → POST /api/payments/razorpay/verify
   ↓
5. Payment Complete ✅
```

## 📚 API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/payments/razorpay/create-order` | POST | Create Razorpay order |
| `/api/payments/razorpay/verify` | POST | Verify payment |
| `/api/payments/razorpay/refund/{id}` | POST | Refund payment (Admin) |
| `/api/payments/order/{orderId}` | GET | Get payment by order |
| `/api/payments/{paymentId}` | GET | Get payment by ID |

## 🔧 What Was Added

### New Files
- `RazorpayConfig.java` - Razorpay client configuration
- `RazorpayService.java` - Service interface
- `RazorpayServiceImpl.java` - Service implementation
- `RazorpayOrderRequestDTO.java` - Order creation DTO
- `RazorpayOrderResponseDTO.java` - Order response DTO
- `RazorpayPaymentVerificationDTO.java` - Payment verification DTO

### Modified Files
- `pom.xml` - Added Razorpay SDK dependency
- `application.properties` - Added Razorpay configuration
- `Payment.java` - Added Razorpay fields
- `PaymentController.java` - Added Razorpay endpoints
- `PaymentRepository.java` - Added findByRazorpayOrderId method

## 🎯 Next Steps

1. **Production Setup**
   - Switch to Live Mode keys in Razorpay Dashboard
   - Update `application.properties` with live credentials
   - Enable HTTPS on your server

2. **Webhook Integration**
   - Configure webhooks in Razorpay Dashboard
   - Implement webhook endpoint for payment updates
   - Handle payment failures and retries

3. **Frontend Integration**
   - Integrate Razorpay checkout in your React/Angular/Vue app
   - Handle payment success/failure scenarios
   - Add loading states and error handling

## 🆘 Troubleshooting

**Issue:** Order creation fails  
**Solution:** Check if Razorpay credentials are correct

**Issue:** Payment verification fails  
**Solution:** Ensure signature verification is working correctly

**Issue:** Refund not working  
**Solution:** Verify payment is in completed status

## 📖 Full Documentation

See `RAZORPAY_INTEGRATION_GUIDE.md` for complete documentation including:
- Detailed API reference
- Frontend integration examples (React, Vanilla JS)
- Security best practices
- Webhook setup
- Production deployment guide

## 🔗 Useful Links

- [Razorpay Dashboard](https://dashboard.razorpay.com/)
- [Razorpay Documentation](https://razorpay.com/docs/)
- [Test Cards](https://razorpay.com/docs/payments/payments/test-card-details/)
- [API Reference](https://razorpay.com/docs/api/)

## 💡 Tips

- Always test in Test Mode before going live
- Never expose Key Secret on frontend
- Always verify payment signature on backend
- Use webhooks for reliable payment updates
- Store payment records for reconciliation

---

**Need Help?** Check the full guide in `RAZORPAY_INTEGRATION_GUIDE.md`
