# Razorpay Integration Guide

## Overview
This guide explains how to integrate and use Razorpay payment gateway in your e-commerce application.

## Setup

### 1. Get Razorpay Credentials
1. Sign up at [Razorpay Dashboard](https://dashboard.razorpay.com/)
2. Navigate to Settings → API Keys
3. Generate Test/Live API Keys
4. Copy the Key ID and Key Secret

### 2. Configure Application
Update `src/main/resources/application.properties`:

```properties
razorpay.key.id=YOUR_RAZORPAY_KEY_ID
razorpay.key.secret=YOUR_RAZORPAY_KEY_SECRET
razorpay.currency=INR
```

### 3. Install Dependencies
The Razorpay Java SDK has been added to `pom.xml`. Run:
```bash
mvn clean install
```

## API Endpoints

### 1. Create Razorpay Order
**Endpoint:** `POST /api/payments/razorpay/create-order`

**Request Body:**
```json
{
  "orderId": "ORDER123",
  "amount": 1000.00
}
```

**Response:**
```json
{
  "razorpayOrderId": "order_MNOPqrstuvwxyz",
  "orderId": "ORDER123",
  "amount": 1000.00,
  "currency": "INR",
  "keyId": "rzp_test_xxxxxxxxxxxxx"
}
```

### 2. Verify Payment
**Endpoint:** `POST /api/payments/razorpay/verify`

**Request Body:**
```json
{
  "razorpayOrderId": "order_MNOPqrstuvwxyz",
  "razorpayPaymentId": "pay_ABCDefghijklmn",
  "razorpaySignature": "signature_string",
  "orderId": "ORDER123"
}
```

**Response:**
```json
{
  "id": "payment_id",
  "orderId": "ORDER123",
  "amount": 1000.00,
  "paymentMethod": "razorpay",
  "paymentStatus": "completed",
  "transactionId": "pay_ABCDefghijklmn",
  "razorpayOrderId": "order_MNOPqrstuvwxyz",
  "razorpayPaymentId": "pay_ABCDefghijklmn",
  "razorpaySignature": "signature_string",
  "paymentDate": "2025-11-24T10:30:00"
}
```

### 3. Refund Payment (Admin Only)
**Endpoint:** `POST /api/payments/razorpay/refund/{paymentId}?amount=1000.00`

**Response:**
```json
"Refund successful. Refund ID: rfnd_XYZabcdefghijk"
```

## Frontend Integration

### Step 1: Create Order
```javascript
// Call your backend to create Razorpay order
const response = await fetch('/api/payments/razorpay/create-order', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer YOUR_JWT_TOKEN'
  },
  body: JSON.stringify({
    orderId: 'ORDER123',
    amount: 1000.00
  })
});

const orderData = await response.json();
```

### Step 2: Initialize Razorpay Checkout
```html
<!-- Add Razorpay Checkout Script -->
<script src="https://checkout.razorpay.com/v1/checkout.js"></script>
```

```javascript
const options = {
  key: orderData.keyId, // Razorpay Key ID from backend
  amount: orderData.amount * 100, // Amount in paise
  currency: orderData.currency,
  name: 'Your E-commerce Store',
  description: 'Order Payment',
  order_id: orderData.razorpayOrderId,
  handler: function (response) {
    // Payment successful, verify on backend
    verifyPayment(response);
  },
  prefill: {
    name: 'Customer Name',
    email: 'customer@example.com',
    contact: '9999999999'
  },
  theme: {
    color: '#3399cc'
  }
};

const razorpay = new Razorpay(options);
razorpay.open();
```

### Step 3: Verify Payment
```javascript
async function verifyPayment(response) {
  const verifyResponse = await fetch('/api/payments/razorpay/verify', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer YOUR_JWT_TOKEN'
    },
    body: JSON.stringify({
      razorpayOrderId: response.razorpay_order_id,
      razorpayPaymentId: response.razorpay_payment_id,
      razorpaySignature: response.razorpay_signature,
      orderId: 'ORDER123'
    })
  });

  if (verifyResponse.ok) {
    const payment = await verifyResponse.json();
    console.log('Payment verified:', payment);
    // Redirect to success page
    window.location.href = '/order-success';
  } else {
    // Handle verification failure
    alert('Payment verification failed');
  }
}
```

## Complete Frontend Example (React)

```jsx
import React, { useState } from 'react';

const CheckoutButton = ({ orderId, amount }) => {
  const [loading, setLoading] = useState(false);

  const handlePayment = async () => {
    setLoading(true);
    
    try {
      // Step 1: Create Razorpay order
      const orderResponse = await fetch('/api/payments/razorpay/create-order', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify({ orderId, amount })
      });

      const orderData = await orderResponse.json();

      // Step 2: Initialize Razorpay
      const options = {
        key: orderData.keyId,
        amount: orderData.amount * 100,
        currency: orderData.currency,
        name: 'E-commerce Store',
        description: 'Order Payment',
        order_id: orderData.razorpayOrderId,
        handler: async function (response) {
          // Step 3: Verify payment
          const verifyResponse = await fetch('/api/payments/razorpay/verify', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify({
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpaySignature: response.razorpay_signature,
              orderId: orderId
            })
          });

          if (verifyResponse.ok) {
            alert('Payment successful!');
            window.location.href = '/order-success';
          } else {
            alert('Payment verification failed');
          }
        },
        prefill: {
          name: 'Customer Name',
          email: 'customer@example.com',
          contact: '9999999999'
        },
        theme: {
          color: '#3399cc'
        }
      };

      const razorpay = new window.Razorpay(options);
      razorpay.open();
    } catch (error) {
      console.error('Payment error:', error);
      alert('Payment failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <button onClick={handlePayment} disabled={loading}>
      {loading ? 'Processing...' : 'Pay Now'}
    </button>
  );
};

export default CheckoutButton;
```

## Testing

### Test Mode
Use Razorpay test credentials for development:
- Test cards: https://razorpay.com/docs/payments/payments/test-card-details/
- Test UPI: success@razorpay
- Test Netbanking: Use any bank with credentials provided by Razorpay

### Test Card Details
- **Card Number:** 4111 1111 1111 1111
- **CVV:** Any 3 digits
- **Expiry:** Any future date
- **Name:** Any name

## Security Best Practices

1. **Never expose Key Secret on frontend** - Always keep it on the backend
2. **Always verify payment signature** - Don't trust frontend data alone
3. **Use HTTPS** - Ensure your application uses SSL/TLS
4. **Validate amounts** - Always verify the amount on backend before creating order
5. **Handle webhooks** - Implement Razorpay webhooks for payment status updates
6. **Store credentials securely** - Use environment variables or secure vaults

## Webhook Integration (Optional)

To receive real-time payment updates, configure webhooks:

1. Go to Razorpay Dashboard → Settings → Webhooks
2. Add webhook URL: `https://yourdomain.com/api/payments/razorpay/webhook`
3. Select events: `payment.captured`, `payment.failed`, `refund.created`
4. Save webhook secret

## Troubleshooting

### Common Issues

1. **Invalid signature error**
   - Ensure Key Secret is correct
   - Verify the signature verification logic

2. **Order creation fails**
   - Check Razorpay credentials
   - Verify amount is in correct format (paise)

3. **Payment not reflecting**
   - Check payment status in Razorpay Dashboard
   - Verify webhook configuration

## Support

- Razorpay Documentation: https://razorpay.com/docs/
- Razorpay Support: https://razorpay.com/support/
- API Reference: https://razorpay.com/docs/api/

## Payment Flow Diagram

```
User → Frontend → Backend (Create Order) → Razorpay
                                              ↓
User ← Frontend ← Backend (Verify) ← Razorpay Payment
```

1. User initiates payment
2. Frontend calls backend to create Razorpay order
3. Backend creates order and returns order details
4. Frontend opens Razorpay checkout with order details
5. User completes payment on Razorpay
6. Razorpay sends payment details to frontend
7. Frontend sends payment details to backend for verification
8. Backend verifies signature and updates payment status
9. User is redirected to success page
