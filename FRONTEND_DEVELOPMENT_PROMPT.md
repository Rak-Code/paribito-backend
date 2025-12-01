# Complete E-Commerce Frontend Development Prompt

## Project Overview
Build a modern, responsive e-commerce frontend application that integrates with a Spring Boot backend API. The application includes user authentication, product browsing, cart management, wishlist, order processing, payment integration (Razorpay), reviews, and automated email reminders.

---

## Technology Stack Recommendations

### Option 1: React/Next.js (Recommended)
- **Framework:** Next.js 14+ (App Router) or React 18+
- **Styling:** Tailwind CSS / Material-UI / Chakra UI
- **State Management:** React Context API / Redux Toolkit / Zustand
- **HTTP Client:** Axios / Fetch API
- **Form Handling:** React Hook Form + Zod validation
- **Image Handling:** Next.js Image component / React Image Gallery


---

## Backend API Configuration

### Base URL
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

### Authentication
All protected endpoints require JWT token in Authorization header:
```javascript
headers: {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
}
```

**Note:** For multipart/form-data (image uploads), omit Content-Type header.

---

## Complete API Endpoints Reference

### 1. AUTHENTICATION APIs

#### 1.1 Register User
**Endpoint:** `POST /api/auth/register`  
**Authentication:** None  
**Request Body:**
```json
{
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "password": "SecurePass123!",
  "phone": "+1234567890"
}
```
**Response (201 Created):**
```json
{
  "id": "user123",
  "email": "john.doe@example.com",
  "fullName": "John Doe",
  "phone": "+1234567890",
  "role": "USER",
  "createdAt": "2025-11-24T10:00:00Z",
  "addresses": []
}
```

#### 1.2 Login User
**Endpoint:** `POST /api/auth/login`  
**Authentication:** None  
**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePass123!"
}
```
**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "user123",
    "email": "john.doe@example.com",
    "fullName": "John Doe",
    "phone": "+1234567890",
    "role": "USER",
    "createdAt": "2025-11-24T10:00:00Z",
    "addresses": []
  }
}
```

**Frontend Implementation Notes:**
- Store JWT token in localStorage or secure cookie
- Store user data in global state (Context/Redux)
- Implement auto-logout on token expiration
- Add password strength validation
- Show loading states during authentication

---

### 2. USER PROFILE APIs

#### 2.1 Get User Profile
**Endpoint:** `GET /api/users/{userId}`  
**Authentication:** Required  
**Response (200 OK):**
```json
{
  "id": "user123",
  "email": "john.doe@example.com",
  "fullName": "John Doe",
  "phone": "+1234567890",
  "role": "USER",
  "createdAt": "2025-11-24T10:00:00Z",
  "addresses": [
    {
      "addressId": "addr123",
      "addressLine": "123 Main Street",
      "city": "New York",
      "state": "NY",
      "postalCode": "10001",
      "country": "USA",
      "isDefault": true
    }
  ]
}
```

#### 2.2 Update User Profile
**Endpoint:** `PUT /api/users/{userId}`  
**Authentication:** Required  
**Request Body:**
```json
{
  "fullName": "John Updated Doe",
  "phone": "+1234567891"
}
```

#### 2.3 Add Address
**Endpoint:** `POST /api/users/{userId}/addresses`  
**Authentication:** Required  
**Request Body:**
```json
{
  "addressLine": "456 Oak Avenue",
  "city": "Los Angeles",
  "state": "CA",
  "postalCode": "90001",
  "country": "USA",
  "isDefault": false
}
```

#### 2.4 Update Address
**Endpoint:** `PUT /api/users/{userId}/addresses/{addressId}`  
**Authentication:** Required

#### 2.5 Delete Address
**Endpoint:** `DELETE /api/users/{userId}/addresses/{addressId}`  
**Authentication:** Required

**Frontend Implementation Notes:**
- Create profile management page
- Address book with add/edit/delete functionality
- Set default address option
- Form validation for all fields

---

### 3. CATEGORY APIs

#### 3.1 Get All Categories
**Endpoint:** `GET /api/categories`  
**Authentication:** None  
**Response (200 OK):**
```json
[
  {
    "id": "cat123",
    "name": "Electronics"
  },
  {
    "id": "cat124",
    "name": "Clothing"
  }
]
```

#### 3.2 Create Category (Admin Only)
**Endpoint:** `POST /api/categories`  
**Authentication:** Required (ADMIN role)  
**Request Body:**
```json
{
  "name": "Electronics"
}
```

#### 3.3 Update Category (Admin Only)
**Endpoint:** `PUT /api/categories/{id}`  
**Authentication:** Required (ADMIN role)

#### 3.4 Delete Category (Admin Only)
**Endpoint:** `DELETE /api/categories/{id}`  
**Authentication:** Required (ADMIN role)

**Frontend Implementation Notes:**
- Display categories in navigation menu
- Category filter on product listing page
- Admin panel for category management
- Category-based product browsing

---

### 4. PRODUCT APIs

#### 4.1 Get All Products
**Endpoint:** `GET /api/products`  
**Authentication:** None  
**Response (200 OK):**
```json
[
  {
    "id": "prod123",
    "name": "Wireless Headphones",
    "description": "High-quality Bluetooth headphones with noise cancellation",
    "categoryId": "cat123",
    "price": 99.99,
    "stockQuantity": 50,
    "imageUrls": [
      "https://pub-xxxxx.r2.dev/products/img1.jpg",
      "https://pub-xxxxx.r2.dev/products/img2.jpg"
    ],
    "size": "M",
    "color": "Black",
    "createdAt": "2025-11-24T10:00:00Z"
  }
]
```

#### 4.2 Get Product by ID
**Endpoint:** `GET /api/products/{id}`  
**Authentication:** None

#### 4.3 Search Products
**Endpoint:** `GET /api/products?q={searchTerm}`  
**Authentication:** None  
**Example:** `GET /api/products?q=headphones`

#### 4.4 Filter by Category
**Endpoint:** `GET /api/products?category={categoryId}`  
**Authentication:** None

#### 4.5 Create Product (Admin Only)
**Endpoint:** `POST /api/products`  
**Authentication:** Required (ADMIN role)  
**Content-Type:** `multipart/form-data`  
**Request Parameters:**
```javascript
const formData = new FormData();
formData.append('name', 'Wireless Headphones');
formData.append('description', 'High-quality Bluetooth headphones');
formData.append('categoryId', 'cat123');
formData.append('price', '99.99');
formData.append('stockQuantity', '50');
formData.append('size', 'M'); // Optional: XS, S, M, L, XL, XXL, XXXL
formData.append('color', 'Black'); // Optional
formData.append('images', imageFile1); // Multiple files allowed
formData.append('images', imageFile2);
```

**Response (201 Created):**
```json
{
  "id": "prod123",
  "name": "Wireless Headphones",
  "description": "High-quality Bluetooth headphones",
  "categoryId": "cat123",
  "price": 99.99,
  "stockQuantity": 50,
  "imageUrls": [
    "https://pub-xxxxx.r2.dev/products/img1.jpg",
    "https://pub-xxxxx.r2.dev/products/img2.jpg"
  ],
  "size": "M",
  "color": "Black",
  "createdAt": "2025-11-24T10:00:00Z"
}
```

#### 4.6 Update Product (Admin Only)
**Endpoint:** `PUT /api/products/{id}`  
**Authentication:** Required (ADMIN role)  
**Content-Type:** `multipart/form-data`  
**Additional Parameter:**
- `keepExistingImages` (boolean, default: true)
  - `true`: Add new images to existing ones
  - `false`: Replace all images with new ones

#### 4.7 Delete Product (Admin Only)
**Endpoint:** `DELETE /api/products/{id}`  
**Authentication:** Required (ADMIN role)  
**Note:** Automatically deletes associated images from cloud storage

**Frontend Implementation Notes:**
- Product listing page with grid/list view
- Product detail page with image gallery
- Search functionality with debouncing
- Category filtering
- Price range filtering (client-side)
- Sort by price, name, date
- Admin product management panel
- Image upload with preview
- Multiple image support (carousel/gallery)
- Stock availability indicator
- Size and color selection

---

### 5. CART APIs

#### 5.1 Add to Cart
**Endpoint:** `POST /api/cart/add`  
**Authentication:** Required  
**Request Body:**
```json
{
  "productId": "prod123",
  "quantity": 2
}
```
**Response (201 Created):**
```json
{
  "id": "cart123",
  "userId": "user123",
  "productId": "prod123",
  "quantity": 2,
  "addedAt": "2025-11-24T10:00:00Z"
}
```
**Note:** Automatically schedules a reminder email after 30 minutes

#### 5.2 Get User Cart
**Endpoint:** `GET /api/cart`  
**Authentication:** Required  
**Response (200 OK):**
```json
[
  {
    "id": "cart123",
    "userId": "user123",
    "productId": "prod123",
    "quantity": 2,
    "addedAt": "2025-11-24T10:00:00Z"
  }
]
```

#### 5.3 Update Cart Item Quantity
**Endpoint:** `PUT /api/cart/{cartItemId}`  
**Authentication:** Required  
**Request Body:**
```json
{
  "quantity": 5
}
```

#### 5.4 Remove from Cart
**Endpoint:** `DELETE /api/cart/{cartItemId}`  
**Authentication:** Required

#### 5.5 Clear Cart
**Endpoint:** `DELETE /api/cart/clear`  
**Authentication:** Required

**Frontend Implementation Notes:**
- Shopping cart page with item list
- Quantity increment/decrement buttons
- Remove item functionality
- Cart total calculation (fetch product details and calculate)
- Cart icon with item count badge
- Mini cart dropdown in header
- Empty cart state
- Continue shopping button
- Proceed to checkout button
- Save cart to localStorage for guest users (optional)

---

### 6. WISHLIST APIs

#### 6.1 Add to Wishlist
**Endpoint:** `POST /api/wishlist/add`  
**Authentication:** Required  
**Request Body:**
```json
{
  "productId": "prod123"
}
```
**Response (201 Created):**
```json
{
  "id": "wish123",
  "userId": "user123",
  "productId": "prod123",
  "addedAt": "2025-11-24T10:00:00Z"
}
```
**Note:** Automatically schedules a reminder email after 60 minutes

#### 6.2 Get User Wishlist
**Endpoint:** `GET /api/wishlist`  
**Authentication:** Required  
**Response (200 OK):**
```json
[
  {
    "id": "wish123",
    "userId": "user123",
    "productId": "prod123",
    "addedAt": "2025-11-24T10:00:00Z"
  }
]
```

#### 6.3 Remove from Wishlist
**Endpoint:** `DELETE /api/wishlist/{wishlistItemId}`  
**Authentication:** Required

#### 6.4 Move to Cart
**Endpoint:** `POST /api/wishlist/{wishlistItemId}/move-to-cart`  
**Authentication:** Required

**Frontend Implementation Notes:**
- Wishlist page with product grid
- Heart icon to add/remove from wishlist
- Move to cart button
- Remove from wishlist button
- Empty wishlist state
- Wishlist icon with item count badge
- Show wishlist status on product cards

---

### 7. ORDER APIs

#### 7.1 Place Order
**Endpoint:** `POST /api/orders`  
**Authentication:** Required  
**Request Body:**
```json
{
  "addressId": "addr123",
  "items": [
    {
      "productId": "prod123",
      "quantity": 2,
      "price": 99.99
    }
  ],
  "totalAmount": 199.98
}
```
**Response (201 Created):**
```json
{
  "id": "order123",
  "userId": "user123",
  "address": {
    "addressId": "addr123",
    "addressLine": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "postalCode": "10001",
    "country": "USA"
  },
  "totalAmount": 199.98,
  "status": "pending",
  "orderDate": "2025-11-24T10:00:00Z",
  "items": [
    {
      "productId": "prod123",
      "quantity": 2,
      "price": 99.99
    }
  ]
}
```

#### 7.2 Get User Orders
**Endpoint:** `GET /api/orders`  
**Authentication:** Required  
**Response (200 OK):**
```json
[
  {
    "id": "order123",
    "userId": "user123",
    "totalAmount": 199.98,
    "status": "pending",
    "orderDate": "2025-11-24T10:00:00Z",
    "items": [...]
  }
]
```

#### 7.3 Get Order by ID
**Endpoint:** `GET /api/orders/{orderId}`  
**Authentication:** Required

#### 7.4 Get All Orders (Admin Only)
**Endpoint:** `GET /api/orders/all`  
**Authentication:** Required (ADMIN role)

#### 7.5 Update Order Status (Admin Only)
**Endpoint:** `PUT /api/orders/{orderId}/status`  
**Authentication:** Required (ADMIN role)  
**Request Body:**
```json
{
  "status": "shipped"
}
```
**Status Values:** pending, processing, shipped, delivered, cancelled

**Frontend Implementation Notes:**
- Checkout page with order summary
- Address selection
- Order confirmation page
- Order history page
- Order details page with tracking
- Order status timeline (pending → processing → shipped → delivered)
- Cancel order option (if status is pending)
- Admin order management dashboard
- Order status update functionality (admin)
- Email notification on order status change

---

### 8. PAYMENT APIs (Razorpay Integration)

#### 8.1 Create Razorpay Order
**Endpoint:** `POST /api/payments/razorpay/create-order`  
**Authentication:** Required  
**Request Body:**
```json
{
  "orderId": "order123",
  "amount": 199.98
}
```
**Response (200 OK):**
```json
{
  "razorpayOrderId": "order_MNOPqrstuvwxyz",
  "orderId": "order123",
  "amount": 199.98,
  "currency": "INR",
  "keyId": "rzp_test_xxxxxxxxxxxxx"
}
```

#### 8.2 Verify Razorpay Payment
**Endpoint:** `POST /api/payments/razorpay/verify`  
**Authentication:** Required  
**Request Body:**
```json
{
  "razorpayOrderId": "order_MNOPqrstuvwxyz",
  "razorpayPaymentId": "pay_ABCDefghijklmn",
  "razorpaySignature": "signature_string",
  "orderId": "order123"
}
```
**Response (200 OK):**
```json
{
  "id": "payment123",
  "orderId": "order123",
  "amount": 199.98,
  "paymentMethod": "razorpay",
  "paymentStatus": "completed",
  "transactionId": "pay_ABCDefghijklmn",
  "razorpayOrderId": "order_MNOPqrstuvwxyz",
  "razorpayPaymentId": "pay_ABCDefghijklmn",
  "razorpaySignature": "signature_string",
  "paymentDate": "2025-11-24T10:00:00Z"
}
```

#### 8.3 Get Payment by Order ID
**Endpoint:** `GET /api/payments/order/{orderId}`  
**Authentication:** Required

#### 8.4 Refund Payment (Admin Only)
**Endpoint:** `POST /api/payments/razorpay/refund/{paymentId}?amount=199.98`  
**Authentication:** Required (ADMIN role)

**Frontend Implementation Notes:**
- Add Razorpay Checkout script: `<script src="https://checkout.razorpay.com/v1/checkout.js"></script>`
- Payment button on checkout page
- Razorpay modal integration
- Payment success/failure handling
- Payment verification flow
- Payment history page
- Admin refund functionality
- Test mode indicator

**Razorpay Integration Code Example:**
```javascript
// Step 1: Create order
const createOrder = async () => {
  const response = await fetch('/api/payments/razorpay/create-order', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ orderId: 'order123', amount: 199.98 })
  });
  return await response.json();
};

// Step 2: Open Razorpay checkout
const openRazorpay = (orderData) => {
  const options = {
    key: orderData.keyId,
    amount: orderData.amount * 100, // Convert to paise
    currency: orderData.currency,
    name: 'Your Store Name',
    description: 'Order Payment',
    order_id: orderData.razorpayOrderId,
    handler: function (response) {
      verifyPayment(response);
    },
    prefill: {
      name: 'Customer Name',
      email: 'customer@example.com',
      contact: '9999999999'
    },
    theme: { color: '#3399cc' }
  };
  const razorpay = new window.Razorpay(options);
  razorpay.open();
};

// Step 3: Verify payment
const verifyPayment = async (response) => {
  const verifyResponse = await fetch('/api/payments/razorpay/verify', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      razorpayOrderId: response.razorpay_order_id,
      razorpayPaymentId: response.razorpay_payment_id,
      razorpaySignature: response.razorpay_signature,
      orderId: 'order123'
    })
  });
  
  if (verifyResponse.ok) {
    // Payment successful
    window.location.href = '/order-success';
  } else {
    // Payment failed
    alert('Payment verification failed');
  }
};
```

---

### 9. REVIEW APIs

#### 9.1 Add Review
**Endpoint:** `POST /api/reviews`  
**Authentication:** Required  
**Request Body:**
```json
{
  "productId": "prod123",
  "rating": 5,
  "comment": "Excellent product! Highly recommended."
}
```
**Rating:** 1-5 (integer)  
**Response (201 Created):**
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

#### 9.2 Get Product Reviews
**Endpoint:** `GET /api/reviews/product/{productId}`  
**Authentication:** None  
**Response (200 OK):**
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

#### 9.3 Get User Reviews
**Endpoint:** `GET /api/reviews/user/{userId}`  
**Authentication:** Required

#### 9.4 Update Review
**Endpoint:** `PUT /api/reviews/{reviewId}`  
**Authentication:** Required  
**Request Body:**
```json
{
  "rating": 4,
  "comment": "Good product, but could be better."
}
```

#### 9.5 Delete Review
**Endpoint:** `DELETE /api/reviews/{reviewId}`  
**Authentication:** Required

**Frontend Implementation Notes:**
- Review section on product detail page
- Star rating component (1-5 stars)
- Review form with rating and comment
- Display average rating
- Review list with pagination
- User's own reviews page
- Edit/delete own reviews
- Review submission confirmation
- Prevent multiple reviews per product per user

---

## Data Models & Field Specifications

### User Model
```typescript
interface User {
  id: string;
  email: string;              // Unique, required
  fullName: string;           // Required
  phone: string;              // Required
  role: 'USER' | 'ADMIN';     // Default: USER
  createdAt: string;          // ISO 8601 format
  addresses: Address[];       // Array of addresses
}

interface Address {
  addressId: string;          // Auto-generated UUID
  addressLine: string;        // Required
  city: string;               // Required
  state: string;              // Required
  postalCode: string;         // Required
  country: string;            // Required
  isDefault: boolean;         // Default: false
}
```

### Product Model
```typescript
interface Product {
  id: string;
  name: string;               // Required
  description: string;        // Required
  categoryId: string;         // Required, references Category
  price: number;              // Required, decimal (e.g., 99.99)
  stockQuantity: number;      // Required, integer
  imageUrls: string[];        // Array of image URLs from cloud storage
  size?: 'XS' | 'S' | 'M' | 'L' | 'XL' | 'XXL' | 'XXXL'; // Optional
  color?: string;             // Optional
  createdAt: string;          // ISO 8601 format
}
```

### Category Model
```typescript
interface Category {
  id: string;
  name: string;               // Required, unique
}
```

### Cart Item Model
```typescript
interface CartItem {
  id: string;
  userId: string;             // References User
  productId: string;          // References Product
  quantity: number;           // Required, integer, min: 1
  addedAt: string;            // ISO 8601 format
}
```

### Wishlist Item Model
```typescript
interface WishlistItem {
  id: string;
  userId: string;             // References User
  productId: string;          // References Product
  addedAt: string;            // ISO 8601 format
}
```

### Order Model
```typescript
interface Order {
  id: string;
  userId: string;             // References User
  address: OrderAddress;      // Snapshot of address at order time
  totalAmount: number;        // Required, decimal
  status: 'pending' | 'processing' | 'shipped' | 'delivered' | 'cancelled';
  orderDate: string;          // ISO 8601 format
  items: OrderItem[];         // Array of order items
}

interface OrderAddress {
  addressId: string;
  addressLine: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
}

interface OrderItem {
  productId: string;          // References Product
  quantity: number;           // Integer
  price: number;              // Price at time of order
}
```

### Payment Model
```typescript
interface Payment {
  id: string;
  orderId: string;            // References Order
  amount: number;             // Decimal
  paymentMethod: 'credit_card' | 'debit_card' | 'upi' | 'net_banking' | 'cod' | 'razorpay';
  paymentStatus: 'pending' | 'completed' | 'failed' | 'refunded';
  transactionId?: string;     // External transaction ID
  razorpayOrderId?: string;   // Razorpay specific
  razorpayPaymentId?: string; // Razorpay specific
  razorpaySignature?: string; // Razorpay specific
  paymentDate: string;        // ISO 8601 format
}
```

### Review Model
```typescript
interface Review {
  id: string;
  productId: string;          // References Product
  userId: string;             // References User
  rating: number;             // Integer, 1-5
  comment: string;            // Required
  createdAt: string;          // ISO 8601 format
}
```

---

## Required Frontend Pages & Components

### 1. Authentication Pages
- **Login Page** (`/login`)
  - Email and password fields
  - Remember me checkbox
  - Forgot password link (optional)
  - Register link
  - Form validation
  - Error handling
  
- **Register Page** (`/register`)
  - Full name, email, password, phone fields
  - Password confirmation
  - Password strength indicator
  - Terms and conditions checkbox
  - Form validation
  - Success redirect to login

### 2. Home Page (`/`)
- Hero section with featured products
- Category navigation
- Featured/trending products grid
- Special offers section
- Newsletter signup (optional)

### 3. Product Pages
- **Product Listing Page** (`/products`)
  - Product grid/list view toggle
  - Search bar with real-time search
  - Category filter sidebar
  - Price range filter
  - Sort options (price, name, date)
  - Pagination or infinite scroll
  - Loading states
  - Empty state
  
- **Product Detail Page** (`/products/:id`)
  - Product image gallery (carousel/lightbox)
  - Product name, description, price
  - Size and color selection (if applicable)
  - Stock availability indicator
  - Quantity selector
  - Add to cart button
  - Add to wishlist button
  - Product reviews section
  - Average rating display
  - Related products section

### 4. Cart & Checkout
- **Shopping Cart Page** (`/cart`)
  - Cart items list with product details
  - Quantity increment/decrement
  - Remove item button
  - Subtotal calculation
  - Proceed to checkout button
  - Continue shopping button
  - Empty cart state
  
- **Checkout Page** (`/checkout`)
  - Order summary
  - Address selection/add new address
  - Payment method selection
  - Order total breakdown
  - Place order button
  - Razorpay payment integration

### 5. User Account Pages
- **Profile Page** (`/profile`)
  - User information display
  - Edit profile form
  - Change password option
  
- **Address Book** (`/profile/addresses`)
  - List of saved addresses
  - Add new address form
  - Edit/delete address options
  - Set default address
  
- **Order History** (`/orders`)
  - List of all orders
  - Order status display
  - Order date and total
  - View details button
  
- **Order Details** (`/orders/:id`)
  - Order information
  - Shipping address
  - Order items list
  - Payment details
  - Order status timeline
  - Track order button (optional)
  - Cancel order button (if pending)
  
- **Wishlist Page** (`/wishlist`)
  - Wishlist items grid
  - Move to cart button
  - Remove from wishlist button
  - Empty wishlist state

### 6. Review Pages
- **My Reviews** (`/reviews`)
  - List of user's reviews
  - Edit/delete options
  - Product information

### 7. Admin Pages (Admin Role Only)
- **Admin Dashboard** (`/admin`)
  - Statistics overview
  - Recent orders
  - Low stock alerts
  
- **Product Management** (`/admin/products`)
  - Product list with edit/delete
  - Add new product button
  - Search and filter
  
- **Add/Edit Product** (`/admin/products/new`, `/admin/products/:id/edit`)
  - Product form with all fields
  - Multiple image upload with preview
  - Image management (keep/replace)
  - Form validation
  
- **Category Management** (`/admin/categories`)
  - Category list
  - Add/edit/delete categories
  
- **Order Management** (`/admin/orders`)
  - All orders list
  - Filter by status
  - Update order status
  - View order details
  
- **Payment Management** (`/admin/payments`)
  - Payment list
  - Refund functionality
  - Payment status

### 8. Common Components
- **Header/Navbar**
  - Logo
  - Search bar
  - Navigation links
  - Cart icon with badge
  - Wishlist icon with badge
  - User menu (login/profile/logout)
  
- **Footer**
  - Links (About, Contact, Terms, Privacy)
  - Social media links
  - Copyright information
  
- **Product Card**
  - Product image
  - Product name
  - Price
  - Rating display
  - Add to cart button
  - Wishlist heart icon
  - Stock indicator
  
- **Loading Spinner**
- **Error Boundary**
- **Toast Notifications**
- **Confirmation Modals**
- **Image Gallery/Carousel**
- **Star Rating Component**
- **Breadcrumbs**

---

## State Management Requirements

### Global State (Context/Redux/Zustand)
```typescript
interface AppState {
  // Authentication
  auth: {
    isAuthenticated: boolean;
    user: User | null;
    token: string | null;
  };
  
  // Cart
  cart: {
    items: CartItem[];
    itemCount: number;
    totalAmount: number;
  };
  
  // Wishlist
  wishlist: {
    items: WishlistItem[];
    itemCount: number;
  };
  
  // UI
  ui: {
    isLoading: boolean;
    error: string | null;
    notifications: Notification[];
  };
  
  // Products (optional, can be fetched per page)
  products: {
    list: Product[];
    categories: Category[];
    selectedCategory: string | null;
    searchQuery: string;
  };
}
```

### Actions/Mutations Required
- **Auth:** login, logout, register, updateProfile
- **Cart:** addToCart, removeFromCart, updateQuantity, clearCart, fetchCart
- **Wishlist:** addToWishlist, removeFromWishlist, fetchWishlist
- **Products:** fetchProducts, fetchProductById, searchProducts, filterByCategory
- **Orders:** createOrder, fetchOrders, fetchOrderById
- **Reviews:** addReview, updateReview, deleteReview, fetchReviews

---

## Form Validation Rules

### Registration Form
- **Full Name:** Required, min 2 characters, max 100 characters
- **Email:** Required, valid email format, unique
- **Password:** Required, min 8 characters, must contain uppercase, lowercase, number, special character
- **Phone:** Required, valid phone format (e.g., +1234567890)

### Login Form
- **Email:** Required, valid email format
- **Password:** Required

### Address Form
- **Address Line:** Required, min 5 characters
- **City:** Required, min 2 characters
- **State:** Required, min 2 characters
- **Postal Code:** Required, valid format
- **Country:** Required

### Product Form (Admin)
- **Name:** Required, min 3 characters, max 200 characters
- **Description:** Required, min 10 characters
- **Category:** Required, must be valid category ID
- **Price:** Required, number, min 0.01
- **Stock Quantity:** Required, integer, min 0
- **Size:** Optional, must be one of: XS, S, M, L, XL, XXL, XXXL
- **Color:** Optional, max 50 characters
- **Images:** Optional, max 10MB per file, formats: JPG, PNG, GIF, WebP

### Review Form
- **Rating:** Required, integer, 1-5
- **Comment:** Required, min 10 characters, max 1000 characters

### Order Form
- **Address:** Required, must select or add address
- **Items:** Required, at least 1 item
- **Total Amount:** Required, must match calculated total

---

## Image Handling Guidelines

### Product Image Upload (Admin)
1. **Use FormData API** (NOT JSON)
2. **Multiple file selection** supported
3. **File validation:**
   - Max size: 10MB per file
   - Allowed formats: JPG, JPEG, PNG, GIF, WebP
4. **Image preview** before upload
5. **Remove image** from selection before submit
6. **Backend returns** image URLs automatically

**Example Code:**
```javascript
const handleImageUpload = (e) => {
  const files = Array.from(e.target.files);
  
  // Validate files
  const validFiles = files.filter(file => {
    if (file.size > 10 * 1024 * 1024) {
      alert(`${file.name} is too large (max 10MB)`);
      return false;
    }
    const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
    if (!validTypes.includes(file.type)) {
      alert(`${file.name} is not a valid image format`);
      return false;
    }
    return true;
  });
  
  setSelectedImages(validFiles);
  
  // Create previews
  const previews = validFiles.map(file => URL.createObjectURL(file));
  setImagePreviews(previews);
};

const handleSubmit = async (e) => {
  e.preventDefault();
  
  const formData = new FormData();
  formData.append('name', productName);
  formData.append('description', description);
  formData.append('categoryId', categoryId);
  formData.append('price', price);
  formData.append('stockQuantity', stockQuantity);
  formData.append('size', size);
  formData.append('color', color);
  
  // Append all images
  selectedImages.forEach(image => {
    formData.append('images', image);
  });
  
  const response = await fetch('/api/products', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
      // DO NOT set Content-Type header - browser sets it automatically with boundary
    },
    body: formData
  });
  
  const product = await response.json();
  // product.imageUrls contains the uploaded image URLs
};
```

### Product Image Display
1. **Image gallery/carousel** for multiple images
2. **Thumbnail navigation**
3. **Lightbox/modal** for full-size view
4. **Lazy loading** for performance
5. **Fallback image** if no images available
6. **Responsive images** (different sizes for mobile/desktop)

**Example Code:**
```javascript
const ProductGallery = ({ imageUrls }) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  
  if (!imageUrls || imageUrls.length === 0) {
    return <div className="no-image">No image available</div>;
  }
  
  return (
    <div className="gallery">
      <img 
        src={imageUrls[currentIndex]} 
        alt="Product" 
        className="main-image"
      />
      
      {imageUrls.length > 1 && (
        <div className="thumbnails">
          {imageUrls.map((url, index) => (
            <img
              key={index}
              src={url}
              alt={`Thumbnail ${index + 1}`}
              className={index === currentIndex ? 'active' : ''}
              onClick={() => setCurrentIndex(index)}
            />
          ))}
        </div>
      )}
    </div>
  );
};
```

---

## Error Handling & User Feedback

### HTTP Status Code Handling
- **200 OK:** Success, display success message
- **201 Created:** Resource created, redirect or show confirmation
- **204 No Content:** Deletion successful, update UI
- **400 Bad Request:** Validation error, show field-specific errors
- **401 Unauthorized:** Token expired/invalid, redirect to login
- **403 Forbidden:** Insufficient permissions, show error message
- **404 Not Found:** Resource not found, show 404 page
- **409 Conflict:** Duplicate resource (e.g., email exists), show error
- **500 Internal Server Error:** Server error, show generic error message

### Error Display
- **Toast notifications** for success/error messages
- **Inline field errors** for form validation
- **Error pages** for 404, 500, etc.
- **Loading states** during API calls
- **Retry buttons** for failed requests

### Success Feedback
- **Toast notifications** for actions (added to cart, order placed, etc.)
- **Confirmation modals** for destructive actions (delete, cancel order)
- **Success pages** for order completion, registration
- **Visual feedback** (button loading states, disabled states)

---

## Security Best Practices

### Authentication
1. **Store JWT token** in localStorage or httpOnly cookie
2. **Include token** in Authorization header for protected routes
3. **Auto-logout** on token expiration (401 response)
4. **Redirect to login** for unauthenticated users accessing protected pages
5. **Password validation** on client-side (min 8 chars, complexity)

### Authorization
1. **Role-based routing** (admin pages only for ADMIN role)
2. **Hide admin features** from non-admin users
3. **Verify permissions** before showing edit/delete buttons

### Input Validation
1. **Client-side validation** for all forms
2. **Sanitize user input** to prevent XSS
3. **Validate file uploads** (size, type)
4. **Prevent SQL injection** (backend handles this, but validate on frontend too)

### API Security
1. **Use HTTPS** in production
2. **Never expose sensitive data** in URLs
3. **Implement CSRF protection** if using cookies
4. **Rate limiting** for API calls (optional)

---

## Performance Optimization

### Code Splitting
- Lazy load routes
- Dynamic imports for heavy components
- Separate vendor bundles

### Image Optimization
- Use Next.js Image component or similar
- Lazy load images
- Use appropriate image formats (WebP)
- Responsive images

### API Optimization
- Debounce search input (300ms)
- Cache API responses
- Pagination for large lists
- Infinite scroll for product listings

### State Management
- Avoid unnecessary re-renders
- Memoize expensive computations
- Use React.memo for pure components

---

## Responsive Design Requirements

### Breakpoints
- **Mobile:** < 640px
- **Tablet:** 640px - 1024px
- **Desktop:** > 1024px

### Mobile-First Design
- Stack elements vertically on mobile
- Hamburger menu for navigation
- Touch-friendly buttons (min 44x44px)
- Swipeable image galleries
- Bottom navigation bar (optional)

### Tablet Optimization
- 2-column product grid
- Collapsible sidebar filters
- Responsive tables

### Desktop Features
- Multi-column layouts
- Hover effects
- Dropdown menus
- Sidebar navigation

---

## Accessibility (A11y) Requirements

### Semantic HTML
- Use proper heading hierarchy (h1, h2, h3)
- Use semantic tags (nav, main, article, section)
- Use button for actions, a for links

### ARIA Labels
- Add aria-label for icon buttons
- Use aria-live for dynamic content
- Add role attributes where needed

### Keyboard Navigation
- All interactive elements accessible via keyboard
- Visible focus indicators
- Tab order makes sense

### Screen Reader Support
- Alt text for all images
- Form labels properly associated
- Error messages announced

### Color Contrast
- WCAG AA compliance (4.5:1 for text)
- Don't rely on color alone for information

---

## Testing Requirements

### Unit Tests
- Component rendering
- Form validation logic
- Utility functions
- State management actions

### Integration Tests
- API integration
- Form submission flows
- Authentication flows
- Cart operations

### E2E Tests (Optional)
- Complete user journeys
- Checkout flow
- Order placement
- Admin operations

---

## Environment Variables

Create `.env.local` file:
```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
NEXT_PUBLIC_RAZORPAY_KEY_ID=rzp_test_xxxxxxxxxxxxx
```

**Note:** Prefix with `NEXT_PUBLIC_` for Next.js to expose to browser

---

## Deployment Checklist

### Pre-Deployment
- [ ] Update API_BASE_URL to production URL
- [ ] Update Razorpay keys to live keys
- [ ] Remove console.logs
- [ ] Test all features in production mode
- [ ] Run build and check for errors
- [ ] Optimize images
- [ ] Enable HTTPS
- [ ] Set up error tracking (Sentry, etc.)
- [ ] Configure CORS on backend for production domain

### Production Environment Variables
```env
NEXT_PUBLIC_API_BASE_URL=https://api.yourdomain.com/api
NEXT_PUBLIC_RAZORPAY_KEY_ID=rzp_live_xxxxxxxxxxxxx
```

---

## Additional Features (Optional Enhancements)

### User Experience
- **Product comparison** - Compare multiple products
- **Recently viewed** - Track and display recently viewed products
- **Product recommendations** - Based on browsing/purchase history
- **Live chat support** - Customer support integration
- **Order tracking** - Real-time order tracking
- **Email notifications** - Order confirmations, shipping updates
- **Push notifications** - Browser push notifications

### Advanced Features
- **Multi-currency support** - Display prices in different currencies
- **Multi-language support** - i18n implementation
- **Dark mode** - Theme toggle
- **Advanced search** - Filters, facets, autocomplete
- **Product variants** - Size/color combinations with separate stock
- **Bulk operations** - Admin bulk product updates
- **Analytics dashboard** - Sales, revenue, user analytics
- **Coupon/discount codes** - Promotional codes
- **Gift cards** - Gift card purchase and redemption
- **Subscription products** - Recurring payments

### Social Features
- **Social login** - Google, Facebook OAuth
- **Share products** - Social media sharing
- **Referral program** - Refer friends for rewards
- **User profiles** - Public user profiles with reviews

---

## Sample Project Structure (React/Next.js)

```
frontend/
├── public/
│   ├── images/
│   └── favicon.ico
├── src/
│   ├── app/                    # Next.js 13+ App Router
│   │   ├── (auth)/
│   │   │   ├── login/
│   │   │   └── register/
│   │   ├── (shop)/
│   │   │   ├── products/
│   │   │   ├── cart/
│   │   │   ├── checkout/
│   │   │   └── wishlist/
│   │   ├── admin/
│   │   │   ├── products/
│   │   │   ├── orders/
│   │   │   └── categories/
│   │   ├── profile/
│   │   ├── orders/
│   │   └── layout.tsx
│   ├── components/
│   │   ├── common/
│   │   │   ├── Header.tsx
│   │   │   ├── Footer.tsx
│   │   │   ├── Loading.tsx
│   │   │   └── ErrorBoundary.tsx
│   │   ├── product/
│   │   │   ├── ProductCard.tsx
│   │   │   ├── ProductGallery.tsx
│   │   │   └── ProductFilters.tsx
│   │   ├── cart/
│   │   │   ├── CartItem.tsx
│   │   │   └── CartSummary.tsx
│   │   ├── forms/
│   │   │   ├── LoginForm.tsx
│   │   │   ├── RegisterForm.tsx
│   │   │   └── AddressForm.tsx
│   │   └── ui/
│   │       ├── Button.tsx
│   │       ├── Input.tsx
│   │       ├── Modal.tsx
│   │       └── Toast.tsx
│   ├── lib/
│   │   ├── api.ts              # API client
│   │   ├── auth.ts             # Auth utilities
│   │   └── utils.ts            # Helper functions
│   ├── hooks/
│   │   ├── useAuth.ts
│   │   ├── useCart.ts
│   │   ├── useProducts.ts
│   │   └── useOrders.ts
│   ├── context/
│   │   ├── AuthContext.tsx
│   │   ├── CartContext.tsx
│   │   └── WishlistContext.tsx
│   ├── types/
│   │   ├── user.ts
│   │   ├── product.ts
│   │   ├── order.ts
│   │   └── payment.ts
│   └── styles/
│       └── globals.css
├── .env.local
├── next.config.js
├── tailwind.config.js
├── tsconfig.json
└── package.json
```

---

## API Client Setup Example

```typescript
// lib/api.ts
import axios from 'axios';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - Add auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - Handle errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired, redirect to login
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// API methods
export const api = {
  // Auth
  login: (email: string, password: string) =>
    apiClient.post('/auth/login', { email, password }),
  register: (data: RegisterData) =>
    apiClient.post('/auth/register', data),
  
  // Products
  getProducts: () => apiClient.get('/products'),
  getProductById: (id: string) => apiClient.get(`/products/${id}`),
  searchProducts: (query: string) => apiClient.get(`/products?q=${query}`),
  createProduct: (formData: FormData) =>
    apiClient.post('/products', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),
  updateProduct: (id: string, formData: FormData) =>
    apiClient.put(`/products/${id}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),
  deleteProduct: (id: string) => apiClient.delete(`/products/${id}`),
  
  // Cart
  getCart: () => apiClient.get('/cart'),
  addToCart: (productId: string, quantity: number) =>
    apiClient.post('/cart/add', { productId, quantity }),
  updateCartItem: (cartItemId: string, quantity: number) =>
    apiClient.put(`/cart/${cartItemId}`, { quantity }),
  removeFromCart: (cartItemId: string) =>
    apiClient.delete(`/cart/${cartItemId}`),
  clearCart: () => apiClient.delete('/cart/clear'),
  
  // Wishlist
  getWishlist: () => apiClient.get('/wishlist'),
  addToWishlist: (productId: string) =>
    apiClient.post('/wishlist/add', { productId }),
  removeFromWishlist: (wishlistItemId: string) =>
    apiClient.delete(`/wishlist/${wishlistItemId}`),
  
  // Orders
  createOrder: (data: OrderData) => apiClient.post('/orders', data),
  getOrders: () => apiClient.get('/orders'),
  getOrderById: (id: string) => apiClient.get(`/orders/${id}`),
  
  // Payments
  createRazorpayOrder: (orderId: string, amount: number) =>
    apiClient.post('/payments/razorpay/create-order', { orderId, amount }),
  verifyRazorpayPayment: (data: RazorpayVerificationData) =>
    apiClient.post('/payments/razorpay/verify', data),
  
  // Reviews
  addReview: (productId: string, rating: number, comment: string) =>
    apiClient.post('/reviews', { productId, rating, comment }),
  getProductReviews: (productId: string) =>
    apiClient.get(`/reviews/product/${productId}`),
  updateReview: (reviewId: string, rating: number, comment: string) =>
    apiClient.put(`/reviews/${reviewId}`, { rating, comment }),
  deleteReview: (reviewId: string) =>
    apiClient.delete(`/reviews/${reviewId}`),
  
  // Categories
  getCategories: () => apiClient.get('/categories'),
  createCategory: (name: string) =>
    apiClient.post('/categories', { name }),
  updateCategory: (id: string, name: string) =>
    apiClient.put(`/categories/${id}`, { name }),
  deleteCategory: (id: string) =>
    apiClient.delete(`/categories/${id}`),
  
  // User
  getUserProfile: (userId: string) =>
    apiClient.get(`/users/${userId}`),
  updateUserProfile: (userId: string, data: UpdateUserData) =>
    apiClient.put(`/users/${userId}`, data),
  addAddress: (userId: string, address: AddressData) =>
    apiClient.post(`/users/${userId}/addresses`, address),
  updateAddress: (userId: string, addressId: string, address: AddressData) =>
    apiClient.put(`/users/${userId}/addresses/${addressId}`, address),
  deleteAddress: (userId: string, addressId: string) =>
    apiClient.delete(`/users/${userId}/addresses/${addressId}`),
};

export default apiClient;
```

---

## Complete Workflow Examples

### 1. User Registration & Login Flow
```
1. User visits /register
2. Fills registration form (fullName, email, password, phone)
3. Frontend validates input
4. POST /api/auth/register
5. On success, redirect to /login
6. User fills login form (email, password)
7. POST /api/auth/login
8. Store token in localStorage
9. Store user data in global state
10. Redirect to home page
```

### 2. Product Browsing & Purchase Flow
```
1. User visits home page
2. Clicks on category or searches
3. GET /api/products or /api/products?q=search
4. Displays product grid
5. User clicks on product
6. GET /api/products/{id}
7. Displays product detail page
8. User selects quantity and clicks "Add to Cart"
9. POST /api/cart/add
10. Shows success toast
11. Cart badge updates
12. User clicks cart icon
13. GET /api/cart
14. Displays cart page with items
15. User clicks "Proceed to Checkout"
16. Displays checkout page
17. User selects/adds address
18. User clicks "Place Order"
19. POST /api/orders
20. On success, initiate payment
21. POST /api/payments/razorpay/create-order
22. Open Razorpay checkout modal
23. User completes payment
24. POST /api/payments/razorpay/verify
25. On success, redirect to order success page
26. Display order confirmation
```

### 3. Admin Product Management Flow
```
1. Admin logs in
2. Navigates to /admin/products
3. GET /api/products
4. Displays product list
5. Admin clicks "Add New Product"
6. Displays product form
7. Admin fills form and uploads images
8. Frontend validates input
9. Creates FormData with all fields and images
10. POST /api/products (multipart/form-data)
11. Backend uploads images to cloud storage
12. Backend returns product with imageUrls
13. Redirect to product list
14. Shows success toast
```

### 4. Review Submission Flow
```
1. User views product detail page
2. Scrolls to review section
3. GET /api/reviews/product/{productId}
4. Displays existing reviews
5. User clicks "Write a Review"
6. Displays review form
7. User selects rating (1-5 stars) and writes comment
8. POST /api/reviews
9. On success, refresh reviews
10. Shows success toast
```

### 5. Wishlist Management Flow
```
1. User clicks heart icon on product card
2. POST /api/wishlist/add
3. Heart icon fills (visual feedback)
4. Wishlist badge updates
5. Backend schedules reminder email (60 minutes)
6. User navigates to /wishlist
7. GET /api/wishlist
8. Displays wishlist items
9. User clicks "Move to Cart"
10. POST /api/cart/add
11. DELETE /api/wishlist/{wishlistItemId}
12. Updates both cart and wishlist
```

---

## Email Reminder System (Automatic)

### Cart Reminder
- **Trigger:** User adds product to cart
- **Delay:** 30 minutes (configurable)
- **Email Content:**
  - Product name and price
  - Stock availability alert
  - Call-to-action to complete purchase
- **Frontend Action:** None required (automatic backend process)

### Wishlist Reminder
- **Trigger:** User adds product to wishlist
- **Delay:** 60 minutes (configurable)
- **Email Content:**
  - Product name and price
  - Stock availability
  - Encouragement to purchase
- **Frontend Action:** None required (automatic backend process)

**Note:** No frontend implementation needed for email reminders. The backend automatically handles scheduling and sending.

---

## Key Implementation Notes

### 1. Authentication Token Management
```typescript
// Store token after login
localStorage.setItem('token', response.data.token);
localStorage.setItem('user', JSON.stringify(response.data.user));

// Retrieve token for API calls
const token = localStorage.getItem('token');

// Clear on logout
localStorage.removeItem('token');
localStorage.removeItem('user');
```

### 2. Protected Routes
```typescript
// Example: Protected route wrapper
const ProtectedRoute = ({ children, adminOnly = false }) => {
  const { isAuthenticated, user } = useAuth();
  
  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }
  
  if (adminOnly && user?.role !== 'ADMIN') {
    return <Navigate to="/" />;
  }
  
  return children;
};

// Usage
<Route path="/profile" element={
  <ProtectedRoute>
    <ProfilePage />
  </ProtectedRoute>
} />

<Route path="/admin" element={
  <ProtectedRoute adminOnly>
    <AdminDashboard />
  </ProtectedRoute>
} />
```

### 3. Cart Total Calculation
```typescript
// Cart items only contain productId and quantity
// Need to fetch product details to calculate total
const calculateCartTotal = async (cartItems: CartItem[]) => {
  let total = 0;
  
  for (const item of cartItems) {
    const product = await api.getProductById(item.productId);
    total += product.data.price * item.quantity;
  }
  
  return total;
};

// Or fetch all products once and calculate
const cartWithDetails = await Promise.all(
  cartItems.map(async (item) => {
    const product = await api.getProductById(item.productId);
    return {
      ...item,
      product: product.data,
      subtotal: product.data.price * item.quantity
    };
  })
);

const total = cartWithDetails.reduce((sum, item) => sum + item.subtotal, 0);
```

### 4. Image Upload Best Practices
```typescript
// DO: Use FormData for image uploads
const formData = new FormData();
formData.append('name', 'Product Name');
formData.append('images', imageFile);

// DON'T: Try to send images as JSON
// This will NOT work:
const data = {
  name: 'Product Name',
  images: [imageFile] // ❌ Wrong
};
```

### 5. Search Debouncing
```typescript
import { useState, useEffect } from 'react';

const SearchBar = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedTerm, setDebouncedTerm] = useState('');
  
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedTerm(searchTerm);
    }, 300);
    
    return () => clearTimeout(timer);
  }, [searchTerm]);
  
  useEffect(() => {
    if (debouncedTerm) {
      // Perform search
      api.searchProducts(debouncedTerm);
    }
  }, [debouncedTerm]);
  
  return (
    <input
      type="text"
      value={searchTerm}
      onChange={(e) => setSearchTerm(e.target.value)}
      placeholder="Search products..."
    />
  );
};
```

### 6. Razorpay Integration
```typescript
// Add script to HTML head
<script src="https://checkout.razorpay.com/v1/checkout.js"></script>

// Payment handler
const handlePayment = async (orderId: string, amount: number) => {
  try {
    // Step 1: Create Razorpay order
    const { data } = await api.createRazorpayOrder(orderId, amount);
    
    // Step 2: Open Razorpay checkout
    const options = {
      key: data.keyId,
      amount: data.amount * 100, // Convert to paise
      currency: data.currency,
      name: 'Your Store Name',
      description: 'Order Payment',
      order_id: data.razorpayOrderId,
      handler: async function (response: any) {
        // Step 3: Verify payment
        try {
          await api.verifyRazorpayPayment({
            razorpayOrderId: response.razorpay_order_id,
            razorpayPaymentId: response.razorpay_payment_id,
            razorpaySignature: response.razorpay_signature,
            orderId: orderId
          });
          
          // Payment successful
          router.push('/order-success');
        } catch (error) {
          alert('Payment verification failed');
        }
      },
      prefill: {
        name: user.fullName,
        email: user.email,
        contact: user.phone
      },
      theme: {
        color: '#3399cc'
      }
    };
    
    const razorpay = new (window as any).Razorpay(options);
    razorpay.open();
  } catch (error) {
    console.error('Payment error:', error);
    alert('Payment failed. Please try again.');
  }
};
```

---

## Common Pitfalls to Avoid

### 1. ❌ Sending Images as JSON
```javascript
// WRONG
const data = {
  name: 'Product',
  images: [imageFile]
};
fetch('/api/products', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(data) // ❌ Won't work
});

// CORRECT
const formData = new FormData();
formData.append('name', 'Product');
formData.append('images', imageFile);
fetch('/api/products', {
  method: 'POST',
  body: formData // ✅ Correct
});
```

### 2. ❌ Not Handling Token Expiration
```javascript
// WRONG - No error handling
const response = await api.getProducts();

// CORRECT - Handle 401 errors
try {
  const response = await api.getProducts();
} catch (error) {
  if (error.response?.status === 401) {
    // Token expired, redirect to login
    localStorage.removeItem('token');
    router.push('/login');
  }
}
```

### 3. ❌ Not Validating Forms
```javascript
// WRONG - No validation
const handleSubmit = () => {
  api.register(formData);
};

// CORRECT - Validate before submit
const handleSubmit = () => {
  if (!formData.email || !isValidEmail(formData.email)) {
    setError('Invalid email');
    return;
  }
  if (formData.password.length < 8) {
    setError('Password must be at least 8 characters');
    return;
  }
  api.register(formData);
};
```

### 4. ❌ Not Showing Loading States
```javascript
// WRONG - No loading indicator
const fetchProducts = async () => {
  const response = await api.getProducts();
  setProducts(response.data);
};

// CORRECT - Show loading state
const fetchProducts = async () => {
  setLoading(true);
  try {
    const response = await api.getProducts();
    setProducts(response.data);
  } catch (error) {
    setError(error.message);
  } finally {
    setLoading(false);
  }
};
```

### 5. ❌ Not Calculating Cart Total Correctly
```javascript
// WRONG - Cart items don't have price
const total = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);

// CORRECT - Fetch product details first
const cartWithProducts = await Promise.all(
  cartItems.map(async (item) => {
    const product = await api.getProductById(item.productId);
    return { ...item, price: product.data.price };
  })
);
const total = cartWithProducts.reduce((sum, item) => sum + item.price * item.quantity, 0);
```

---

## Final Checklist

### Core Features
- [ ] User registration and login
- [ ] JWT token storage and management
- [ ] Protected routes (user and admin)
- [ ] Product listing with search and filters
- [ ] Product detail page with image gallery
- [ ] Add to cart functionality
- [ ] Shopping cart page with quantity management
- [ ] Wishlist functionality
- [ ] Checkout process with address selection
- [ ] Razorpay payment integration
- [ ] Order placement and confirmation
- [ ] Order history and details
- [ ] Product reviews (add, edit, delete)
- [ ] User profile management
- [ ] Address book management

### Admin Features
- [ ] Admin dashboard
- [ ] Product management (CRUD)
- [ ] Multiple image upload for products
- [ ] Category management (CRUD)
- [ ] Order management and status updates
- [ ] Payment management and refunds

### UI/UX
- [ ] Responsive design (mobile, tablet, desktop)
- [ ] Loading states for all async operations
- [ ] Error handling and user feedback
- [ ] Toast notifications for actions
- [ ] Empty states (empty cart, no products, etc.)
- [ ] Form validation with error messages
- [ ] Confirmation modals for destructive actions
- [ ] Breadcrumb navigation
- [ ] Cart and wishlist badges with counts

### Performance
- [ ] Image lazy loading
- [ ] Code splitting and lazy loading routes
- [ ] Search debouncing
- [ ] API response caching (optional)
- [ ] Optimized images (WebP, responsive sizes)

### Security
- [ ] Input validation and sanitization
- [ ] XSS prevention
- [ ] CSRF protection (if using cookies)
- [ ] Secure token storage
- [ ] Role-based access control

### Accessibility
- [ ] Semantic HTML
- [ ] ARIA labels for interactive elements
- [ ] Keyboard navigation support
- [ ] Alt text for images
- [ ] Color contrast compliance

### Testing
- [ ] Unit tests for components
- [ ] Integration tests for API calls
- [ ] E2E tests for critical flows (optional)

### Documentation
- [ ] README with setup instructions
- [ ] Environment variables documentation
- [ ] API integration documentation
- [ ] Component documentation (optional)

---

## Quick Start Commands

### React/Next.js
```bash
# Create Next.js app
npx create-next-app@latest frontend --typescript --tailwind --app

# Install dependencies
cd frontend
npm install axios react-hook-form zod @hookform/resolvers

# Add Razorpay types
npm install --save-dev @types/razorpay

# Run development server
npm run dev
```

### Vue/Nuxt
```bash
# Create Nuxt app
npx nuxi@latest init frontend

# Install dependencies
cd frontend
npm install axios pinia

# Run development server
npm run dev
```

### Angular
```bash
# Create Angular app
ng new frontend

# Install dependencies
cd frontend
npm install axios

# Run development server
ng serve
```

---

## Support & Resources

### Backend API Documentation
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Endpoints Guide: See `API_ENDPOINTS_GUIDE.md`
- Razorpay Integration: See `RAZORPAY_INTEGRATION_GUIDE.md`

### External Documentation
- Razorpay Docs: https://razorpay.com/docs/
- Razorpay Test Cards: https://razorpay.com/docs/payments/payments/test-card-details/
- Next.js Docs: https://nextjs.org/docs
- React Docs: https://react.dev
- Tailwind CSS: https://tailwindcss.com/docs

---

## Summary

This prompt provides a complete specification for building a full-featured e-commerce frontend that integrates seamlessly with your Spring Boot backend. The frontend should include:

1. **User Features:** Registration, login, profile management, product browsing, cart, wishlist, checkout, payment, orders, and reviews
2. **Admin Features:** Product management with image uploads, category management, order management, and payment management
3. **Payment Integration:** Complete Razorpay integration with order creation, payment processing, and verification
4. **Responsive Design:** Mobile-first approach with support for all device sizes
5. **Security:** JWT authentication, role-based access control, input validation
6. **Performance:** Optimized images, lazy loading, code splitting, debouncing
7. **Accessibility:** WCAG compliance, keyboard navigation, screen reader support

All API endpoints, data models, validation rules, and implementation examples are provided above. Follow the structure, workflows, and best practices outlined to create a production-ready e-commerce application.

**Note:** The backend automatically handles email reminders for cart and wishlist items. No frontend implementation is required for this feature.

---

**Good luck with your frontend development! 🚀**
