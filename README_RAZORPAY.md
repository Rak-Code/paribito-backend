# 💳 Razorpay Payment Gateway Integration

Complete Razorpay payment gateway integration for your e-commerce application.

## 📋 Table of Contents

1. [Quick Start](#quick-start)
2. [Features](#features)
3. [Documentation](#documentation)
4. [API Endpoints](#api-endpoints)
5. [Testing](#testing)
6. [Deployment](#deployment)

---

## 🚀 Quick Start

### 1. Get Razorpay Credentials
Sign up at [Razorpay Dashboard](https://dashboard.razorpay.com/) and get your API keys.

### 2. Configure
Update `src/main/resources/application.properties`:
```properties
razorpay.key.id=rzp_test_YOUR_KEY_ID
razorpay.key.secret=YOUR_KEY_SECRET
razorpay.currency=INR
```

### 3. Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

### 4. Test
Open `razorpay-checkout-example.html` in your browser or use the Postman collection.

**📖 Full Guide:** See [RAZORPAY_QUICK_START.md](RAZORPAY_QUICK_START.md)

---

## ✨ Features

✅ **Order Creation** - Create Razorpay orders programmatically  
✅ **Payment Verification** - Secure signature-based verification  
✅ **Refund Support** - Full and partial refunds (Admin only)  
✅ **Payment Tracking** - Complete payment history and status  
✅ **Multiple Payment Methods** - Cards, UPI, Net Banking, Wallets  
✅ **Test Mode** - Comprehensive testing with test cards  
✅ **Security** - JWT authentication and signature verification  
✅ **MongoDB Integration** - Persistent payment records  

---

## 📚 Documentation

| Document | Description |
|----------|-------------|
| [RAZORPAY_QUICK_START.md](RAZORPAY_QUICK_START.md) | 5-minute setup guide |
| [RAZORPAY_INTEGRATION_GUIDE.md](RAZORPAY_INTEGRATION_GUIDE.md) | Complete integration guide with examples |
| [RAZORPAY_INTEGRATION_SUMMARY.md](RAZORPAY_INTEGRATION_SUMMARY.md) | Overview of all changes |
| [RAZORPAY_DEPLOYMENT_CHECKLIST.md](RAZORPAY_DEPLOYMENT_CHECKLIST.md) | Production deployment checklist |
| [RAZORPAY_TROUBLESHOOTING.md](RAZORPAY_TROUBLESHOOTING.md) | Common issues and solutions |

---

## 🔌 API Endpoints

### Create Razorpay Order
```http
POST /api/payments/razorpay/create-order
Content-Type: application/json
Authorization: Bearer {token}

{
  "orderId": "ORDER123",
  "amount": 1000.00
}
```

### Verify Payment
```http
POST /api/payments/razorpay/verify
Content-Type: application/json
Authorization: Bearer {token}

{
  "razorpayOrderId": "order_xxx",
  "razorpayPaymentId": "pay_xxx",
  "razorpaySignature": "signature_xxx",
  "orderId": "ORDER123"
}
```

### Refund Payment (Admin)
```http
POST /api/payments/razorpay/refund/{paymentId}?amount=1000.00
Authorization: Bearer {admin_token}
```

**📖 Full API Reference:** See [RAZORPAY_INTEGRATION_GUIDE.md](RAZORPAY_INTEGRATION_GUIDE.md)

---

## 🧪 Testing

### Test Cards
- **Card Number:** 4111 1111 1111 1111
- **CVV:** Any 3 digits
- **Expiry:** Any future date
- **Test UPI:** success@razorpay

### Testing Tools

1. **HTML Test Page**
   - Open `razorpay-checkout-example.html`
   - Enter order details and JWT token
   - Test payment flow

2. **Postman Collection**
   - Import `Razorpay_Postman_Collection.json`
   - Set environment variables
   - Test all endpoints

3. **cURL**
   ```bash
   curl -X POST http://localhost:8080/api/payments/razorpay/create-order \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer YOUR_TOKEN" \
     -d '{"orderId":"TEST","amount":100}'
   ```

---

## 🏗️ Architecture

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│   Frontend  │─────▶│   Backend   │─────▶│  Razorpay   │
│             │      │             │      │     API     │
└─────────────┘      └─────────────┘      └─────────────┘
       │                    │                     │
       │                    ▼                     │
       │             ┌─────────────┐              │
       │             │   MongoDB   │              │
       │             │  (Payments) │              │
       │             └─────────────┘              │
       │                                          │
       └──────────────────────────────────────────┘
              Payment Verification Flow
```

---

## 📦 What's Included

### Backend Components
- `RazorpayConfig.java` - Configuration
- `RazorpayService.java` - Service interface
- `RazorpayServiceImpl.java` - Implementation
- `RazorpayOrderRequestDTO.java` - Request DTO
- `RazorpayOrderResponseDTO.java` - Response DTO
- `RazorpayPaymentVerificationDTO.java` - Verification DTO
- Updated `Payment.java` entity
- Updated `PaymentController.java`
- Updated `PaymentRepository.java`

### Frontend Resources
- `razorpay-checkout-example.html` - Test page
- `Razorpay_Postman_Collection.json` - API tests

### Documentation
- Complete integration guides
- Troubleshooting guide
- Deployment checklist
- Quick start guide

---

## 🔐 Security

- ✅ Signature verification for all payments
- ✅ Key Secret never exposed to frontend
- ✅ JWT authentication on all endpoints
- ✅ Admin-only refund access
- ✅ Secure payment record storage
- ✅ HTTPS recommended for production

---

## 🚀 Deployment

### Development
```bash
# Use test credentials
razorpay.key.id=rzp_test_xxxxxxxxxxxxx
razorpay.key.secret=test_secret_key
```

### Production
```bash
# Use live credentials
razorpay.key.id=rzp_live_xxxxxxxxxxxxx
razorpay.key.secret=live_secret_key

# Use environment variables
export RAZORPAY_KEY_ID=rzp_live_xxxxxxxxxxxxx
export RAZORPAY_KEY_SECRET=live_secret_key
```

**📖 Full Deployment Guide:** See [RAZORPAY_DEPLOYMENT_CHECKLIST.md](RAZORPAY_DEPLOYMENT_CHECKLIST.md)

---

## 🔄 Payment Flow

```
1. User initiates checkout
   ↓
2. Frontend calls /api/payments/razorpay/create-order
   ↓
3. Backend creates Razorpay order
   ↓
4. Frontend opens Razorpay checkout
   ↓
5. User completes payment
   ↓
6. Razorpay returns payment details
   ↓
7. Frontend calls /api/payments/razorpay/verify
   ↓
8. Backend verifies signature
   ↓
9. Payment status updated
   ↓
10. User redirected to success page
```

---

## 🛠️ Tech Stack

- **Backend:** Spring Boot 3.5.8, Java 21
- **Database:** MongoDB
- **Payment Gateway:** Razorpay Java SDK 1.4.6
- **Authentication:** JWT
- **API Documentation:** Swagger/OpenAPI

---

## 📊 Payment Methods Supported

- 💳 Credit Cards (Visa, Mastercard, Amex, etc.)
- 💳 Debit Cards
- 📱 UPI (Google Pay, PhonePe, Paytm, etc.)
- 🏦 Net Banking (All major banks)
- 💰 Wallets (Paytm, PhonePe, etc.)
- 💵 EMI Options

---

## 🆘 Support

### Issues?
Check [RAZORPAY_TROUBLESHOOTING.md](RAZORPAY_TROUBLESHOOTING.md)

### Razorpay Support
- Dashboard: https://dashboard.razorpay.com/
- Documentation: https://razorpay.com/docs/
- Support: https://razorpay.com/support/

---

## 📝 License

This integration follows your project's license.

---

## 🎯 Next Steps

1. ✅ Get Razorpay test credentials
2. ✅ Configure application.properties
3. ✅ Test with HTML page or Postman
4. ✅ Integrate into your frontend
5. ✅ Test thoroughly
6. ✅ Deploy to production
7. ✅ Monitor payments

---

## 💡 Tips

- Always test in Test Mode first
- Never commit API secrets to version control
- Use environment variables for credentials
- Implement webhooks for production
- Monitor payment success rate
- Keep payment records for reconciliation

---

## 📞 Quick Links

- [Razorpay Dashboard](https://dashboard.razorpay.com/)
- [Test Cards](https://razorpay.com/docs/payments/payments/test-card-details/)
- [API Reference](https://razorpay.com/docs/api/)
- [Status Page](https://status.razorpay.com/)

---

**Status:** ✅ Ready to use  
**Version:** 1.0  
**Last Updated:** November 24, 2025

---

Made with ❤️ for seamless payments
