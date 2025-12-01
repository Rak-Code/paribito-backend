# Checkout & Order Placement - Frontend Integration Guide

## Table of Contents
1. [Overview](#overview)
2. [Checkout Flow](#checkout-flow)
3. [API Endpoints Reference](#api-endpoints-reference)
4. [Address Management](#address-management)
5. [Cart Management](#cart-management)
6. [Order Placement](#order-placement)
7. [Payment Integration](#payment-integration)
8. [Complete React Implementation](#complete-react-implementation)
9. [Vanilla JavaScript Implementation](#vanilla-javascript-implementation)
10. [Error Handling](#error-handling)

---

## Overview

### Base URL
```
http://localhost:8080/api
```

### Authentication
All checkout endpoints require JWT token:
```javascript
headers: {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
}
```

### Checkout Process Flow
```
1. User adds items to cart
   ↓
2. User views cart and proceeds to checkout
   ↓
3. User selects/adds delivery address
   ↓
4. System calculates order total
   ↓
5. User reviews order summary
   ↓
6. Create Razorpay order
   ↓
7. User completes payment
   ↓
8. Verify payment and create order
   ↓
9. Clear cart and show confirmation
```

---

## API Endpoints Reference

### Cart APIs

#### 1. Add to Cart
**Endpoint:** `POST /api/cart`

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
  "addedAt": "2025-11-25T10:00:00Z"
}
```

#### 2. Get User Cart
**Endpoint:** `GET /api/cart/{userId}`

**Response:** 200 OK
```json
[
  {
    "id": "cart123",
    "userId": "user123",
    "productId": "prod123",
    "quantity": 2,
    "addedAt": "2025-11-25T10:00:00Z"
  }
]
```

#### 3. Update Cart Item Quantity
**Endpoint:** `PUT /api/cart`

**Request Body:**
```json
{
  "userId": "user123",
  "productId": "prod123",
  "quantity": 5
}
```

#### 4. Remove from Cart
**Endpoint:** `DELETE /api/cart/{cartItemId}`

**Response:** 204 No Content

#### 5. Clear Cart
**Endpoint:** `DELETE /api/cart/{userId}/clear`

**Response:** 204 No Content

---

### Address Management APIs

#### 1. Get User Profile (with addresses)
**Endpoint:** `GET /api/users/{userId}`

**Response:** 200 OK
```json
{
  "id": "user123",
  "email": "john@example.com",
  "fullName": "John Doe",
  "phone": "+1234567890",
  "role": "CUSTOMER",
  "addresses": [
    {
      "id": "addr123",
      "street": "123 Main St",
      "city": "New York",
      "state": "NY",
      "zipCode": "10001",
      "country": "USA",
      "isDefault": true
    }
  ]
}
```

#### 2. Add Address
**Endpoint:** `POST /api/users/{userId}/addresses`

**Request Body:**
```json
{
  "street": "456 Oak Avenue",
  "city": "Los Angeles",
  "state": "CA",
  "zipCode": "90001",
  "country": "USA",
  "isDefault": false
}
```

**Response:** 200 OK (Returns updated user with new address)

#### 3. Delete Address
**Endpoint:** `DELETE /api/users/{userId}/addresses/{addressId}`

**Response:** 204 No Content

---

### Order APIs

#### 1. Place Order
**Endpoint:** `POST /api/orders`

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
  "createdAt": "2025-11-25T10:00:00Z"
}
```

#### 2. Get User Orders
**Endpoint:** `GET /api/orders/user/{userId}`

**Response:** 200 OK (Array of orders)

#### 3. Get Order by ID
**Endpoint:** `GET /api/orders/{orderId}`

**Response:** 200 OK (Single order details)

---

### Payment APIs (Razorpay Integration)

#### 1. Create Razorpay Order
**Endpoint:** `POST /api/payments/razorpay/create-order`

**Request Body:**
```json
{
  "amount": 199.98,
  "currency": "INR",
  "receipt": "order_rcpt_123",
  "notes": {
    "userId": "user123",
    "orderId": "order123"
  }
}
```

**Response:** 200 OK
```json
{
  "razorpayOrderId": "order_MNOPqrstuvwxyz",
  "amount": 19998,
  "currency": "INR",
  "receipt": "order_rcpt_123",
  "status": "created"
}
```

#### 2. Verify Payment
**Endpoint:** `POST /api/payments/razorpay/verify`

**Request Body:**
```json
{
  "razorpayOrderId": "order_MNOPqrstuvwxyz",
  "razorpayPaymentId": "pay_ABCDefghijklmn",
  "razorpaySignature": "signature_string",
  "orderId": "order123",
  "amount": 199.98
}
```

**Response:** 200 OK
```json
{
  "id": "pay123",
  "orderId": "order123",
  "amount": 199.98,
  "paymentMethod": "RAZORPAY",
  "status": "SUCCESS",
  "razorpayOrderId": "order_MNOPqrstuvwxyz",
  "razorpayPaymentId": "pay_ABCDefghijklmn",
  "createdAt": "2025-11-25T10:00:00Z"
}
```

#### 3. Get Payment by Order ID
**Endpoint:** `GET /api/payments/order/{orderId}`

**Response:** 200 OK (Payment details)

---

## Complete React Implementation

### 1. Checkout Page Component

```jsx
'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';

export default function CheckoutPage() {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [cartItems, setCartItems] = useState([]);
  const [products, setProducts] = useState({});
  const [addresses, setAddresses] = useState([]);
  const [selectedAddress, setSelectedAddress] = useState(null);
  const [showAddressForm, setShowAddressForm] = useState(false);
  const [orderSummary, setOrderSummary] = useState({
    subtotal: 0,
    shipping: 50,
    tax: 0,
    total: 0
  });

  const userId = localStorage.getItem('userId');
  const token = localStorage.getItem('token');

  // Fetch cart items and user addresses
  useEffect(() => {
    fetchCartAndAddresses();
  }, []);

  const fetchCartAndAddresses = async () => {
    try {
      // Fetch cart
      const cartRes = await fetch(`http://localhost:8080/api/cart/${userId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      const cartData = await cartRes.json();
      setCartItems(cartData);

      // Fetch product details for each cart item
      const productPromises = cartData.map(item =>
        fetch(`http://localhost:8080/api/products/${item.productId}`)
          .then(res => res.json())
      );
      const productsData = await Promise.all(productPromises);
      
      const productsMap = {};
      productsData.forEach(product => {
        productsMap[product.id] = product;
      });
      setProducts(productsMap);

      // Calculate order summary
      calculateOrderSummary(cartData, productsMap);

      // Fetch user addresses
      const userRes = await fetch(`http://localhost:8080/api/users/${userId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      const userData = await userRes.json();
      setAddresses(userData.addresses || []);
      
      // Select default address
      const defaultAddr = userData.addresses?.find(a => a.isDefault);
      if (defaultAddr) setSelectedAddress(defaultAddr);
      
    } catch (error) {
      console.error('Error fetching data:', error);
      alert('Failed to load checkout data');
    }
  };

  const calculateOrderSummary = (cart, prods) => {
    const subtotal = cart.reduce((sum, item) => {
      const product = prods[item.productId];
      return sum + (product?.price || 0) * item.quantity;
    }, 0);

    const tax = subtotal * 0.18; // 18% tax
    const shipping = 50;
    const total = subtotal + tax + shipping;

    setOrderSummary({ subtotal, shipping, tax, total });
  };

  const handlePlaceOrder = async () => {
    if (!selectedAddress) {
      alert('Please select a delivery address');
      return;
    }

    if (cartItems.length === 0) {
      alert('Your cart is empty');
      return;
    }

    setLoading(true);

    try {
      // Step 1: Create Razorpay Order
      const razorpayOrderRes = await fetch(
        'http://localhost:8080/api/payments/razorpay/create-order',
        {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            amount: orderSummary.total,
            currency: 'INR',
            receipt: `receipt_${Date.now()}`,
            notes: {
              userId: userId,
              cartItems: JSON.stringify(cartItems.map(item => item.id))
            }
          })
        }
      );

      const razorpayOrder = await razorpayOrderRes.json();

      // Step 2: Open Razorpay Checkout
      const options = {
        key: 'rzp_test_YOUR_KEY_ID', // Get from backend or env
        amount: razorpayOrder.amount,
        currency: razorpayOrder.currency,
        name: 'Your Store Name',
        description: 'Order Payment',
        order_id: razorpayOrder.razorpayOrderId,
        handler: async function (response) {
          // Step 3: Verify Payment
          await verifyPaymentAndCreateOrder(response, razorpayOrder);
        },
        prefill: {
          name: localStorage.getItem('userName'),
          email: localStorage.getItem('userEmail'),
          contact: localStorage.getItem('userPhone')
        },
        theme: {
          color: '#3399cc'
        }
      };

      const rzp = new window.Razorpay(options);
      rzp.open();

    } catch (error) {
      console.error('Error creating order:', error);
      alert('Failed to initiate payment');
    } finally {
      setLoading(false);
    }
  };

  const verifyPaymentAndCreateOrder = async (razorpayResponse, razorpayOrder) => {
    try {
      // Create order first
      const orderItems = cartItems.map(item => ({
        productId: item.productId,
        quantity: item.quantity,
        price: products[item.productId]?.price || 0
      }));

      const orderRes = await fetch('http://localhost:8080/api/orders', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          userId: userId,
          address: selectedAddress,
          totalAmount: orderSummary.total,
          items: orderItems
        })
      });

      const order = await orderRes.json();

      // Verify payment
      const verifyRes = await fetch(
        'http://localhost:8080/api/payments/razorpay/verify',
        {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            razorpayOrderId: razorpayResponse.razorpay_order_id,
            razorpayPaymentId: razorpayResponse.razorpay_payment_id,
            razorpaySignature: razorpayResponse.razorpay_signature,
            orderId: order.id,
            amount: orderSummary.total
          })
        }
      );

      if (verifyRes.ok) {
        // Clear cart
        await fetch(`http://localhost:8080/api/cart/${userId}/clear`, {
          method: 'DELETE',
          headers: { 'Authorization': `Bearer ${token}` }
        });

        // Redirect to success page
        router.push(`/order-success?orderId=${order.id}`);
      } else {
        alert('Payment verification failed');
      }

    } catch (error) {
      console.error('Error verifying payment:', error);
      alert('Payment verification failed. Please contact support.');
    }
  };

  return (
    <div className="max-w-6xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Checkout</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column - Address & Cart */}
        <div className="lg:col-span-2 space-y-6">
          
          {/* Delivery Address Section */}
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-semibold mb-4">Delivery Address</h2>
            
            {addresses.length > 0 ? (
              <div className="space-y-3">
                {addresses.map(address => (
                  <div
                    key={address.id}
                    onClick={() => setSelectedAddress(address)}
                    className={`p-4 border rounded-lg cursor-pointer ${
                      selectedAddress?.id === address.id
                        ? 'border-blue-500 bg-blue-50'
                        : 'border-gray-300'
                    }`}
                  >
                    <div className="flex items-start justify-between">
                      <div>
                        <p className="font-medium">{address.street}</p>
                        <p className="text-gray-600">
                          {address.city}, {address.state} {address.zipCode}
                        </p>
                        <p className="text-gray-600">{address.country}</p>
                      </div>
                      {address.isDefault && (
                        <span className="text-xs bg-green-100 text-green-800 px-2 py-1 rounded">
                          Default
                        </span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-500">No saved addresses</p>
            )}

            <button
              onClick={() => setShowAddressForm(true)}
              className="mt-4 text-blue-600 hover:underline"
            >
              + Add New Address
            </button>
          </div>

          {/* Cart Items Section */}
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-semibold mb-4">
              Order Items ({cartItems.length})
            </h2>
            
            <div className="space-y-4">
              {cartItems.map(item => {
                const product = products[item.productId];
                if (!product) return null;

                return (
                  <div key={item.id} className="flex gap-4 border-b pb-4">
                    <img
                      src={product.imageUrls?.[0] || '/placeholder.png'}
                      alt={product.name}
                      className="w-20 h-20 object-cover rounded"
                    />
                    <div className="flex-1">
                      <h3 className="font-medium">{product.name}</h3>
                      <p className="text-sm text-gray-600">
                        Qty: {item.quantity}
                      </p>
                      <p className="text-lg font-semibold text-blue-600">
                        ₹{(product.price * item.quantity).toFixed(2)}
                      </p>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </div>

        {/* Right Column - Order Summary */}
        <div className="lg:col-span-1">
          <div className="bg-white rounded-lg shadow p-6 sticky top-6">
            <h2 className="text-xl font-semibold mb-4">Order Summary</h2>
            
            <div className="space-y-3 mb-4">
              <div className="flex justify-between">
                <span className="text-gray-600">Subtotal</span>
                <span className="font-medium">
                  ₹{orderSummary.subtotal.toFixed(2)}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Shipping</span>
                <span className="font-medium">
                  ₹{orderSummary.shipping.toFixed(2)}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Tax (18%)</span>
                <span className="font-medium">
                  ₹{orderSummary.tax.toFixed(2)}
                </span>
              </div>
              <div className="border-t pt-3 flex justify-between text-lg font-bold">
                <span>Total</span>
                <span className="text-blue-600">
                  ₹{orderSummary.total.toFixed(2)}
                </span>
              </div>
            </div>

            <button
              onClick={handlePlaceOrder}
              disabled={loading || !selectedAddress || cartItems.length === 0}
              className="w-full bg-blue-600 text-white py-3 rounded-lg font-medium hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
            >
              {loading ? 'Processing...' : 'Proceed to Payment'}
            </button>

            <div className="mt-4 text-xs text-gray-500 text-center">
              <p>Secure payment powered by Razorpay</p>
            </div>
          </div>
        </div>
      </div>

      {/* Add Address Modal */}
      {showAddressForm && (
        <AddressFormModal
          userId={userId}
          token={token}
          onClose={() => setShowAddressForm(false)}
          onSuccess={() => {
            setShowAddressForm(false);
            fetchCartAndAddresses();
          }}
        />
      )}
    </div>
  );
}
```

### 2. Address Form Modal Component

```jsx
function AddressFormModal({ userId, token, onClose, onSuccess }) {
  const [formData, setFormData] = useState({
    street: '',
    city: '',
    state: '',
    zipCode: '',
    country: 'India',
    isDefault: false
  });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await fetch(
        `http://localhost:8080/api/users/${userId}/addresses`,
        {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(formData)
        }
      );

      if (response.ok) {
        onSuccess();
      } else {
        alert('Failed to add address');
      }
    } catch (error) {
      console.error('Error adding address:', error);
      alert('Failed to add address');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
        <h2 className="text-2xl font-bold mb-4">Add New Address</h2>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">
              Street Address *
            </label>
            <input
              type="text"
              value={formData.street}
              onChange={(e) => setFormData({...formData, street: e.target.value})}
              required
              className="w-full px-3 py-2 border rounded-lg"
              placeholder="123 Main Street"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">City *</label>
              <input
                type="text"
                value={formData.city}
                onChange={(e) => setFormData({...formData, city: e.target.value})}
                required
                className="w-full px-3 py-2 border rounded-lg"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">State *</label>
              <input
                type="text"
                value={formData.state}
                onChange={(e) => setFormData({...formData, state: e.target.value})}
                required
                className="w-full px-3 py-2 border rounded-lg"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">ZIP Code *</label>
              <input
                type="text"
                value={formData.zipCode}
                onChange={(e) => setFormData({...formData, zipCode: e.target.value})}
                required
                className="w-full px-3 py-2 border rounded-lg"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Country *</label>
              <input
                type="text"
                value={formData.country}
                onChange={(e) => setFormData({...formData, country: e.target.value})}
                required
                className="w-full px-3 py-2 border rounded-lg"
              />
            </div>
          </div>

          <label className="flex items-center space-x-2">
            <input
              type="checkbox"
              checked={formData.isDefault}
              onChange={(e) => setFormData({...formData, isDefault: e.target.checked})}
            />
            <span className="text-sm">Set as default address</span>
          </label>

          <div className="flex gap-3">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2 border rounded-lg hover:bg-gray-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-400"
            >
              {loading ? 'Adding...' : 'Add Address'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
```

### 3. Order Success Page

```jsx
'use client';

import { useEffect, useState } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';

export default function OrderSuccessPage() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const orderId = searchParams.get('orderId');
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (orderId) {
      fetchOrderDetails();
    }
  }, [orderId]);

  const fetchOrderDetails = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:8080/api/orders/${orderId}`,
        {
          headers: { 'Authorization': `Bearer ${token}` }
        }
      );
      const data = await response.json();
      setOrder(data);
    } catch (error) {
      console.error('Error fetching order:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="text-center py-20">Loading...</div>;
  }

  return (
    <div className="max-w-2xl mx-auto p-6">
      <div className="bg-white rounded-lg shadow-lg p-8 text-center">
        <div className="mb-6">
          <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg className="w-10 h-10 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <h1 className="text-3xl font-bold text-green-600 mb-2">
            Order Placed Successfully!
          </h1>
          <p className="text-gray-600">
            Thank you for your purchase. Your order has been confirmed.
          </p>
        </div>

        {order && (
          <div className="bg-gray-50 rounded-lg p-6 mb-6 text-left">
            <h2 className="text-xl font-semibold mb-4">Order Details</h2>
            
            <div className="space-y-2 mb-4">
              <div className="flex justify-between">
                <span className="text-gray-600">Order ID:</span>
                <span className="font-medium">{order.id}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Order Date:</span>
                <span className="font-medium">
                  {new Date(order.createdAt).toLocaleDateString()}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Total Amount:</span>
                <span className="font-bold text-lg text-blue-600">
                  ₹{order.totalAmount.toFixed(2)}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Status:</span>
                <span className="px-3 py-1 bg-yellow-100 text-yellow-800 rounded-full text-sm">
                  {order.status}
                </span>
              </div>
            </div>

            <div className="border-t pt-4">
              <h3 className="font-semibold mb-2">Delivery Address:</h3>
              <p className="text-gray-600">
                {order.address.street}<br />
                {order.address.city}, {order.address.state} {order.address.zipCode}<br />
                {order.address.country}
              </p>
            </div>
          </div>
        )}

        <div className="flex gap-4">
          <button
            onClick={() => router.push('/orders')}
            className="flex-1 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            View My Orders
          </button>
          <button
            onClick={() => router.push('/')}
            className="flex-1 px-6 py-3 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            Continue Shopping
          </button>
        </div>
      </div>
    </div>
  );
}
```

### 4. Cart Page Component

```jsx
'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';

export default function CartPage() {
  const router = useRouter();
  const [cartItems, setCartItems] = useState([]);
  const [products, setProducts] = useState({});
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);

  const userId = localStorage.getItem('userId');
  const token = localStorage.getItem('token');

  useEffect(() => {
    fetchCart();
  }, []);

  const fetchCart = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/cart/${userId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      const data = await response.json();
      setCartItems(data);

      // Fetch product details
      const productPromises = data.map(item =>
        fetch(`http://localhost:8080/api/products/${item.productId}`)
          .then(res => res.json())
      );
      const productsData = await Promise.all(productPromises);
      
      const productsMap = {};
      productsData.forEach(product => {
        productsMap[product.id] = product;
      });
      setProducts(productsMap);
    } catch (error) {
      console.error('Error fetching cart:', error);
    } finally {
      setLoading(false);
    }
  };

  const updateQuantity = async (productId, newQuantity) => {
    if (newQuantity < 1) return;
    
    setUpdating(true);
    try {
      await fetch('http://localhost:8080/api/cart', {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          userId,
          productId,
          quantity: newQuantity
        })
      });
      
      await fetchCart();
    } catch (error) {
      console.error('Error updating quantity:', error);
      alert('Failed to update quantity');
    } finally {
      setUpdating(false);
    }
  };

  const removeItem = async (cartItemId) => {
    setUpdating(true);
    try {
      await fetch(`http://localhost:8080/api/cart/${cartItemId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      
      await fetchCart();
    } catch (error) {
      console.error('Error removing item:', error);
      alert('Failed to remove item');
    } finally {
      setUpdating(false);
    }
  };

  const calculateTotal = () => {
    return cartItems.reduce((sum, item) => {
      const product = products[item.productId];
      return sum + (product?.price || 0) * item.quantity;
    }, 0);
  };

  if (loading) {
    return <div className="text-center py-20">Loading cart...</div>;
  }

  if (cartItems.length === 0) {
    return (
      <div className="max-w-2xl mx-auto p-6 text-center">
        <h1 className="text-3xl font-bold mb-4">Your Cart is Empty</h1>
        <p className="text-gray-600 mb-6">Add some products to get started!</p>
        <button
          onClick={() => router.push('/products')}
          className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          Browse Products
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Shopping Cart</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Cart Items */}
        <div className="lg:col-span-2 space-y-4">
          {cartItems.map(item => {
            const product = products[item.productId];
            if (!product) return null;

            return (
              <div key={item.id} className="bg-white rounded-lg shadow p-4">
                <div className="flex gap-4">
                  <img
                    src={product.imageUrls?.[0] || '/placeholder.png'}
                    alt={product.name}
                    className="w-24 h-24 object-cover rounded"
                  />
                  
                  <div className="flex-1">
                    <h3 className="font-semibold text-lg">{product.name}</h3>
                    <p className="text-gray-600 text-sm mb-2">
                      {product.description?.substring(0, 100)}...
                    </p>
                    <p className="text-xl font-bold text-blue-600">
                      ₹{product.price}
                    </p>
                  </div>

                  <div className="flex flex-col items-end justify-between">
                    <button
                      onClick={() => removeItem(item.id)}
                      disabled={updating}
                      className="text-red-600 hover:text-red-800"
                    >
                      Remove
                    </button>

                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => updateQuantity(item.productId, item.quantity - 1)}
                        disabled={updating || item.quantity <= 1}
                        className="w-8 h-8 border rounded hover:bg-gray-100 disabled:opacity-50"
                      >
                        -
                      </button>
                      <span className="w-12 text-center font-medium">
                        {item.quantity}
                      </span>
                      <button
                        onClick={() => updateQuantity(item.productId, item.quantity + 1)}
                        disabled={updating}
                        className="w-8 h-8 border rounded hover:bg-gray-100 disabled:opacity-50"
                      >
                        +
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        {/* Cart Summary */}
        <div className="lg:col-span-1">
          <div className="bg-white rounded-lg shadow p-6 sticky top-6">
            <h2 className="text-xl font-semibold mb-4">Cart Summary</h2>
            
            <div className="space-y-3 mb-6">
              <div className="flex justify-between">
                <span className="text-gray-600">Items ({cartItems.length})</span>
                <span className="font-medium">₹{calculateTotal().toFixed(2)}</span>
              </div>
              <div className="border-t pt-3 flex justify-between text-lg font-bold">
                <span>Subtotal</span>
                <span className="text-blue-600">₹{calculateTotal().toFixed(2)}</span>
              </div>
            </div>

            <button
              onClick={() => router.push('/checkout')}
              className="w-full bg-blue-600 text-white py-3 rounded-lg font-medium hover:bg-blue-700"
            >
              Proceed to Checkout
            </button>

            <button
              onClick={() => router.push('/products')}
              className="w-full mt-3 border border-gray-300 py-3 rounded-lg hover:bg-gray-50"
            >
              Continue Shopping
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
```

---

## Vanilla JavaScript Implementation

### Complete Checkout Page (HTML + JS)

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Checkout</title>
  <script src="https://checkout.razorpay.com/v1/checkout.js"></script>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body { font-family: Arial, sans-serif; background: #f5f5f5; padding: 20px; }
    .container { max-width: 1200px; margin: 0 auto; }
    .grid { display: grid; grid-template-columns: 2fr 1fr; gap: 20px; }
    .card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
    .address-item { padding: 15px; border: 2px solid #ddd; border-radius: 8px; margin-bottom: 10px; cursor: pointer; }
    .address-item.selected { border-color: #007bff; background: #e7f3ff; }
    .cart-item { display: flex; gap: 15px; padding: 15px; border-bottom: 1px solid #eee; }
    .cart-item img { width: 80px; height: 80px; object-fit: cover; border-radius: 4px; }
    .btn { padding: 12px 24px; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
    .btn-primary { background: #007bff; color: white; width: 100%; }
    .btn-primary:hover { background: #0056b3; }
    .btn-primary:disabled { background: #ccc; cursor: not-allowed; }
    .summary-row { display: flex; justify-between; margin-bottom: 10px; }
    .total { font-size: 20px; font-weight: bold; color: #007bff; }
    h1, h2 { margin-bottom: 20px; }
  </style>
</head>
<body>
  <div class="container">
    <h1>Checkout</h1>
    
    <div class="grid">
      <!-- Left Column -->
      <div>
        <!-- Addresses -->
        <div class="card">
          <h2>Delivery Address</h2>
          <div id="addressList"></div>
          <button onclick="showAddressForm()" class="btn" style="margin-top: 10px;">
            + Add New Address
          </button>
        </div>

        <!-- Cart Items -->
        <div class="card" style="margin-top: 20px;">
          <h2>Order Items</h2>
          <div id="cartItems"></div>
        </div>
      </div>

      <!-- Right Column - Summary -->
      <div>
        <div class="card" style="position: sticky; top: 20px;">
          <h2>Order Summary</h2>
          <div id="orderSummary"></div>
          <button id="checkoutBtn" onclick="handleCheckout()" class="btn btn-primary">
            Proceed to Payment
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- Address Form Modal -->
  <div id="addressModal" style="display: none; position: fixed; inset: 0; background: rgba(0,0,0,0.5); align-items: center; justify-content: center;">
    <div class="card" style="max-width: 500px; width: 90%;">
      <h2>Add New Address</h2>
      <form id="addressForm" style="display: grid; gap: 15px;">
        <input type="text" id="street" placeholder="Street Address" required style="padding: 10px; border: 1px solid #ddd; border-radius: 4px;">
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px;">
          <input type="text" id="city" placeholder="City" required style="padding: 10px; border: 1px solid #ddd; border-radius: 4px;">
          <input type="text" id="state" placeholder="State" required style="padding: 10px; border: 1px solid #ddd; border-radius: 4px;">
        </div>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px;">
          <input type="text" id="zipCode" placeholder="ZIP Code" required style="padding: 10px; border: 1px solid #ddd; border-radius: 4px;">
          <input type="text" id="country" placeholder="Country" value="India" required style="padding: 10px; border: 1px solid #ddd; border-radius: 4px;">
        </div>
        <div style="display: flex; gap: 10px;">
          <button type="button" onclick="hideAddressForm()" class="btn" style="flex: 1;">Cancel</button>
          <button type="submit" class="btn btn-primary" style="flex: 1;">Add Address</button>
        </div>
      </form>
    </div>
  </div>

  <script>
    const API_BASE = 'http://localhost:8080/api';
    const userId = localStorage.getItem('userId');
    const token = localStorage.getItem('token');
    
    let cartItems = [];
    let products = {};
    let addresses = [];
    let selectedAddress = null;
    let orderSummary = { subtotal: 0, shipping: 50, tax: 0, total: 0 };

    // Initialize
    async function init() {
      await fetchCart();
      await fetchAddresses();
      renderAll();
    }

    // Fetch cart
    async function fetchCart() {
      const res = await fetch(`${API_BASE}/cart/${userId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      cartItems = await res.json();

      // Fetch product details
      for (const item of cartItems) {
        const prodRes = await fetch(`${API_BASE}/products/${item.productId}`);
        products[item.productId] = await prodRes.json();
      }

      calculateSummary();
    }

    // Fetch addresses
    async function fetchAddresses() {
      const res = await fetch(`${API_BASE}/users/${userId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      const user = await res.json();
      addresses = user.addresses || [];
      selectedAddress = addresses.find(a => a.isDefault) || addresses[0];
    }

    // Calculate order summary
    function calculateSummary() {
      const subtotal = cartItems.reduce((sum, item) => {
        const product = products[item.productId];
        return sum + (product?.price || 0) * item.quantity;
      }, 0);

      const tax = subtotal * 0.18;
      const shipping = 50;
      const total = subtotal + tax + shipping;

      orderSummary = { subtotal, shipping, tax, total };
    }

    // Render all sections
    function renderAll() {
      renderAddresses();
      renderCartItems();
      renderSummary();
    }

    // Render addresses
    function renderAddresses() {
      const html = addresses.map(addr => `
        <div class="address-item ${selectedAddress?.id === addr.id ? 'selected' : ''}"
             onclick="selectAddress('${addr.id}')">
          <div><strong>${addr.street}</strong></div>
          <div>${addr.city}, ${addr.state} ${addr.zipCode}</div>
          <div>${addr.country}</div>
        </div>
      `).join('');
      document.getElementById('addressList').innerHTML = html || '<p>No addresses found</p>';
    }

    // Render cart items
    function renderCartItems() {
      const html = cartItems.map(item => {
        const product = products[item.productId];
        if (!product) return '';
        return `
          <div class="cart-item">
            <img src="${product.imageUrls?.[0] || '/placeholder.png'}" alt="${product.name}">
            <div style="flex: 1;">
              <h3>${product.name}</h3>
              <p>Qty: ${item.quantity}</p>
              <p style="font-size: 18px; font-weight: bold; color: #007bff;">
                ₹${(product.price * item.quantity).toFixed(2)}
              </p>
            </div>
          </div>
        `;
      }).join('');
      document.getElementById('cartItems').innerHTML = html;
    }

    // Render summary
    function renderSummary() {
      document.getElementById('orderSummary').innerHTML = `
        <div class="summary-row">
          <span>Subtotal</span>
          <span>₹${orderSummary.subtotal.toFixed(2)}</span>
        </div>
        <div class="summary-row">
          <span>Shipping</span>
          <span>₹${orderSummary.shipping.toFixed(2)}</span>
        </div>
        <div class="summary-row">
          <span>Tax (18%)</span>
          <span>₹${orderSummary.tax.toFixed(2)}</span>
        </div>
        <hr style="margin: 15px 0;">
        <div class="summary-row total">
          <span>Total</span>
          <span>₹${orderSummary.total.toFixed(2)}</span>
        </div>
      `;
    }

    // Select address
    function selectAddress(addressId) {
      selectedAddress = addresses.find(a => a.id === addressId);
      renderAddresses();
    }

    // Show/hide address form
    function showAddressForm() {
      document.getElementById('addressModal').style.display = 'flex';
    }

    function hideAddressForm() {
      document.getElementById('addressModal').style.display = 'none';
      document.getElementById('addressForm').reset();
    }

    // Add address
    document.getElementById('addressForm').addEventListener('submit', async (e) => {
      e.preventDefault();
      
      const newAddress = {
        street: document.getElementById('street').value,
        city: document.getElementById('city').value,
        state: document.getElementById('state').value,
        zipCode: document.getElementById('zipCode').value,
        country: document.getElementById('country').value,
        isDefault: false
      };

      try {
        await fetch(`${API_BASE}/users/${userId}/addresses`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(newAddress)
        });

        await fetchAddresses();
        renderAddresses();
        hideAddressForm();
      } catch (error) {
        alert('Failed to add address');
      }
    });

    // Handle checkout
    async function handleCheckout() {
      if (!selectedAddress) {
        alert('Please select a delivery address');
        return;
      }

      const btn = document.getElementById('checkoutBtn');
      btn.disabled = true;
      btn.textContent = 'Processing...';

      try {
        // Step 1: Create Razorpay Order
        const razorpayRes = await fetch(`${API_BASE}/payments/razorpay/create-order`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            amount: orderSummary.total,
            currency: 'INR',
            receipt: `receipt_${Date.now()}`,
            notes: { userId }
          })
        });

        const razorpayOrder = await razorpayRes.json();

        // Step 2: Open Razorpay Checkout
        const options = {
          key: 'rzp_test_YOUR_KEY_ID',
          amount: razorpayOrder.amount,
          currency: razorpayOrder.currency,
          name: 'Your Store',
          description: 'Order Payment',
          order_id: razorpayOrder.razorpayOrderId,
          handler: async function (response) {
            await verifyAndCreateOrder(response, razorpayOrder);
          },
          prefill: {
            name: localStorage.getItem('userName'),
            email: localStorage.getItem('userEmail'),
            contact: localStorage.getItem('userPhone')
          },
          theme: { color: '#007bff' }
        };

        const rzp = new Razorpay(options);
        rzp.open();

      } catch (error) {
        alert('Payment initiation failed');
      } finally {
        btn.disabled = false;
        btn.textContent = 'Proceed to Payment';
      }
    }

    // Verify payment and create order
    async function verifyAndCreateOrder(razorpayResponse, razorpayOrder) {
      try {
        // Create order
        const orderItems = cartItems.map(item => ({
          productId: item.productId,
          quantity: item.quantity,
          price: products[item.productId]?.price || 0
        }));

        const orderRes = await fetch(`${API_BASE}/orders`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            userId,
            address: selectedAddress,
            totalAmount: orderSummary.total,
            items: orderItems
          })
        });

        const order = await orderRes.json();

        // Verify payment
        const verifyRes = await fetch(`${API_BASE}/payments/razorpay/verify`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            razorpayOrderId: razorpayResponse.razorpay_order_id,
            razorpayPaymentId: razorpayResponse.razorpay_payment_id,
            razorpaySignature: razorpayResponse.razorpay_signature,
            orderId: order.id,
            amount: orderSummary.total
          })
        });

        if (verifyRes.ok) {
          // Clear cart
          await fetch(`${API_BASE}/cart/${userId}/clear`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
          });

          // Redirect to success page
          window.location.href = `/order-success.html?orderId=${order.id}`;
        } else {
          alert('Payment verification failed');
        }

      } catch (error) {
        alert('Order creation failed');
      }
    }

    // Initialize on page load
    init();
  </script>
</body>
</html>
```

---

## Error Handling

### Comprehensive Error Handler

```javascript
class CheckoutError extends Error {
  constructor(message, code) {
    super(message);
    this.code = code;
  }
}

async function safeApiCall(url, options = {}) {
  try {
    const response = await fetch(url, options);
    
    if (response.status === 401) {
      throw new CheckoutError('Session expired. Please login again.', 'AUTH_EXPIRED');
    }
    
    if (response.status === 403) {
      throw new CheckoutError('Access denied.', 'FORBIDDEN');
    }
    
    if (response.status === 404) {
      throw new CheckoutError('Resource not found.', 'NOT_FOUND');
    }
    
    if (response.status === 400) {
      const error = await response.json();
      throw new CheckoutError(error.message || 'Invalid request', 'VALIDATION_ERROR');
    }
    
    if (!response.ok) {
      throw new CheckoutError('Server error. Please try again.', 'SERVER_ERROR');
    }
    
    return await response.json();
    
  } catch (error) {
    if (error instanceof CheckoutError) {
      throw error;
    }
    
    if (error.name === 'TypeError' && error.message.includes('fetch')) {
      throw new CheckoutError('Network error. Check your connection.', 'NETWORK_ERROR');
    }
    
    throw new CheckoutError('An unexpected error occurred.', 'UNKNOWN_ERROR');
  }
}

// Usage
try {
  const order = await safeApiCall(`${API_BASE}/orders`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(orderData)
  });
  
  console.log('Order created:', order);
  
} catch (error) {
  if (error.code === 'AUTH_EXPIRED') {
    // Redirect to login
    window.location.href = '/login';
  } else {
    // Show error to user
    alert(error.message);
  }
}
```

### React Error Boundary

```jsx
class CheckoutErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('Checkout error:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="p-6 text-center">
          <h2 className="text-2xl font-bold text-red-600 mb-4">
            Something went wrong
          </h2>
          <p className="text-gray-600 mb-4">
            {this.state.error?.message || 'An unexpected error occurred'}
          </p>
          <button
            onClick={() => window.location.reload()}
            className="px-6 py-3 bg-blue-600 text-white rounded-lg"
          >
            Reload Page
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}

// Usage
<CheckoutErrorBoundary>
  <CheckoutPage />
</CheckoutErrorBoundary>
```

---

## Quick Reference

### Essential API Calls for Checkout

```javascript
// 1. Get Cart Items
const cart = await fetch(`${API_BASE}/cart/${userId}`, {
  headers: { 'Authorization': `Bearer ${token}` }
}).then(res => res.json());

// 2. Get User Addresses
const user = await fetch(`${API_BASE}/users/${userId}`, {
  headers: { 'Authorization': `Bearer ${token}` }
}).then(res => res.json());

// 3. Add New Address
await fetch(`${API_BASE}/users/${userId}/addresses`, {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(addressData)
});

// 4. Create Razorpay Order
const razorpayOrder = await fetch(`${API_BASE}/payments/razorpay/create-order`, {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    amount: totalAmount,
    currency: 'INR',
    receipt: `receipt_${Date.now()}`
  })
}).then(res => res.json());

// 5. Create Order
const order = await fetch(`${API_BASE}/orders`, {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    userId,
    address: selectedAddress,
    totalAmount,
    items: orderItems
  })
}).then(res => res.json());

// 6. Verify Payment
await fetch(`${API_BASE}/payments/razorpay/verify`, {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    razorpayOrderId,
    razorpayPaymentId,
    razorpaySignature,
    orderId,
    amount
  })
});

// 7. Clear Cart
await fetch(`${API_BASE}/cart/${userId}/clear`, {
  method: 'DELETE',
  headers: { 'Authorization': `Bearer ${token}` }
});
```

### Razorpay Integration Script

Add this to your HTML `<head>`:
```html
<script src="https://checkout.razorpay.com/v1/checkout.js"></script>
```

### Razorpay Options Object

```javascript
const options = {
  key: 'rzp_test_YOUR_KEY_ID', // Your Razorpay Key ID
  amount: 19998, // Amount in paise (₹199.98 = 19998 paise)
  currency: 'INR',
  name: 'Your Store Name',
  description: 'Order Payment',
  order_id: razorpayOrderId, // From create-order API
  handler: function (response) {
    // Called on successful payment
    console.log(response.razorpay_payment_id);
    console.log(response.razorpay_order_id);
    console.log(response.razorpay_signature);
  },
  prefill: {
    name: 'Customer Name',
    email: 'customer@example.com',
    contact: '+919999999999'
  },
  theme: {
    color: '#3399cc'
  },
  modal: {
    ondismiss: function() {
      // Called when user closes the payment modal
      console.log('Payment cancelled');
    }
  }
};

const rzp = new Razorpay(options);
rzp.open();
```

---

## Testing Checklist

### Before Going Live

- [ ] Cart displays correct items and quantities
- [ ] Cart total calculation is accurate
- [ ] User can add/update/remove cart items
- [ ] User can view saved addresses
- [ ] User can add new addresses
- [ ] User can select delivery address
- [ ] Order summary shows correct amounts (subtotal, tax, shipping, total)
- [ ] Razorpay checkout opens correctly
- [ ] Test payment works (use test card: 4111 1111 1111 1111)
- [ ] Payment verification succeeds
- [ ] Order is created in database
- [ ] Cart is cleared after successful order
- [ ] User is redirected to success page
- [ ] Order details display correctly on success page
- [ ] User can view order in "My Orders"
- [ ] Error messages display for failed payments
- [ ] Loading states show during API calls
- [ ] Authentication errors redirect to login
- [ ] Network errors are handled gracefully

### Test Cards (Razorpay Test Mode)

**Success:**
- Card: 4111 1111 1111 1111
- CVV: Any 3 digits
- Expiry: Any future date

**Failure:**
- Card: 4000 0000 0000 0002

**UPI:**
- success@razorpay (Success)
- failure@razorpay (Failure)

---

## Common Issues & Solutions

### Issue: "Authorization header missing"
**Solution:** Ensure JWT token is included in all API requests:
```javascript
headers: {
  'Authorization': `Bearer ${localStorage.getItem('token')}`
}
```

### Issue: "Cart is empty after adding items"
**Solution:** Check if userId is correct and cart API is returning data:
```javascript
console.log('User ID:', localStorage.getItem('userId'));
```

### Issue: "Razorpay checkout not opening"
**Solution:** 
1. Verify Razorpay script is loaded: `<script src="https://checkout.razorpay.com/v1/checkout.js"></script>`
2. Check browser console for errors
3. Ensure `window.Razorpay` is available

### Issue: "Payment verification failed"
**Solution:** 
1. Check signature verification on backend
2. Ensure all three parameters are sent: orderId, paymentId, signature
3. Verify Razorpay secret key is correct in backend

### Issue: "Order created but payment not verified"
**Solution:** Implement webhook to handle payment updates asynchronously

### Issue: "Address not saving"
**Solution:** Check address object structure matches backend expectations:
```javascript
{
  street: "string",
  city: "string",
  state: "string",
  zipCode: "string",
  country: "string",
  isDefault: boolean
}
```

---

## Production Deployment

### Environment Variables

Create `.env.local` for Next.js:
```env
NEXT_PUBLIC_API_BASE_URL=https://your-api.com/api
NEXT_PUBLIC_RAZORPAY_KEY_ID=rzp_live_YOUR_KEY_ID
```

### Security Best Practices

1. **Never expose Razorpay Key Secret on frontend**
2. **Always verify payment signature on backend**
3. **Use HTTPS in production**
4. **Implement rate limiting on payment endpoints**
5. **Store sensitive data encrypted**
6. **Validate all user inputs**
7. **Implement CSRF protection**
8. **Use secure session management**

### Performance Optimization

1. **Lazy load Razorpay script:**
```javascript
const loadRazorpay = () => {
  return new Promise((resolve) => {
    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.onload = () => resolve(true);
    script.onerror = () => resolve(false);
    document.body.appendChild(script);
  });
};
```

2. **Cache product data:**
```javascript
const productCache = new Map();

async function getProduct(productId) {
  if (productCache.has(productId)) {
    return productCache.get(productId);
  }
  
  const product = await fetch(`/api/products/${productId}`).then(r => r.json());
  productCache.set(productId, product);
  return product;
}
```

3. **Debounce cart updates:**
```javascript
import { debounce } from 'lodash';

const updateQuantity = debounce(async (productId, quantity) => {
  await fetch('/api/cart', {
    method: 'PUT',
    body: JSON.stringify({ userId, productId, quantity })
  });
}, 500);
```

---

## Additional Resources

- **API Documentation:** See `API_ENDPOINTS_GUIDE.md`
- **Razorpay Integration:** See `RAZORPAY_QUICK_START.md`
- **Frontend Guide:** See `FRONTEND_IMPLEMENTATION_GUIDE.md`
- **Razorpay Docs:** https://razorpay.com/docs/
- **Test Cards:** https://razorpay.com/docs/payments/payments/test-card-details/

---

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review API endpoint documentation
3. Check browser console for errors
4. Verify backend logs for API errors
5. Test with Razorpay test mode first

---

**You're ready to build your checkout flow!** 🚀

This guide covers everything you need to implement a complete checkout experience with address management, cart handling, order placement, and Razorpay payment integration.
