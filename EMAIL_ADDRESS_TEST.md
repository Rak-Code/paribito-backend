# Quick Test Guide - Email with Address

## Prerequisites
1. Application running
2. Email configuration working (check `application.properties`)
3. User with saved addresses

## Test Scenario 1: Cart Reminder with Address

### Step 1: Create User with Address
```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "password123",
  "fullName": "Test User",
  "phone": "9876543210"
}
```

### Step 2: Add Address to User
```bash
POST http://localhost:8080/api/addresses
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "addressLine": "123 Test Street, Apartment 4B",
  "city": "Mumbai",
  "state": "Maharashtra",
  "postalCode": "400001",
  "country": "India",
  "isDefault": true
}
```

### Step 3: Add Product to Cart
```bash
POST http://localhost:8080/api/cart
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "productId": "<product-id>",
  "quantity": 1
}
```

### Step 4: Wait for Reminder
- Wait 3 minutes (based on `reminder.cart.delay-minutes=3`)
- Check email inbox
- Verify address appears in email

### Expected Email Content
```
Dear Test User,

We noticed you left something in your cart!

Don't miss out on this amazing product:

=====================================
Product: [Product Name]
Price: ₹[Price]
=====================================

Delivery Address:
-------------------------------------
123 Test Street, Apartment 4B
Mumbai, Maharashtra
400001, India

Complete your purchase now before it's gone!
...
```

## Test Scenario 2: Wishlist Reminder with Address

### Step 1: Add Product to Wishlist
```bash
POST http://localhost:8080/api/wishlist
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "productId": "<product-id>"
}
```

### Step 2: Wait for Reminder
- Wait 3 minutes (based on `reminder.wishlist.delay-minutes=3`)
- Check email inbox
- Verify address appears in email

### Expected Email Content
```
Dear Test User,

Your wishlist item is calling you! 💝

Remember this product you loved?

=====================================
Product: [Product Name]
Price: ₹[Price]
✅ In Stock - Available Now!
=====================================

Delivery Address:
-------------------------------------
123 Test Street, Apartment 4B
Mumbai, Maharashtra
400001, India

Why wait? Turn your wish into reality today!
...
```

## Test Scenario 3: User with Multiple Addresses

### Step 1: Add Multiple Addresses
```bash
# Add first address (not default)
POST http://localhost:8080/api/addresses
{
  "addressLine": "456 Old Street",
  "city": "Delhi",
  "state": "Delhi",
  "postalCode": "110001",
  "country": "India",
  "isDefault": false
}

# Add second address (default)
POST http://localhost:8080/api/addresses
{
  "addressLine": "789 New Avenue",
  "city": "Bangalore",
  "state": "Karnataka",
  "postalCode": "560001",
  "country": "India",
  "isDefault": true
}
```

### Step 2: Trigger Email
- Add item to cart or wishlist
- Wait for reminder

### Expected Result
- Email should show the **default address** (789 New Avenue)
- Not the first address added

## Test Scenario 4: User with No Address

### Step 1: Create User without Address
- Register new user
- Don't add any addresses

### Step 2: Trigger Email
- Add item to cart or wishlist
- Wait for reminder

### Expected Result
- Email is sent successfully
- No "Delivery Address" section appears
- No errors occur

## Verification Checklist

- [ ] Cart reminder includes address
- [ ] Wishlist reminder includes address
- [ ] Default address is used when available
- [ ] First address is used when no default
- [ ] Email works without address (no errors)
- [ ] Address format is correct (line, city, state, postal, country)
- [ ] Order confirmation still works (unchanged)

## Troubleshooting

### Address Not Showing in Email
1. Check user has addresses: `GET /api/addresses`
2. Verify address data is complete
3. Check application logs for errors

### Wrong Address Showing
1. Verify which address is marked as default
2. Check address order in database
3. Update default address if needed

### Email Not Sending
1. Check email configuration in `application.properties`
2. Verify SMTP credentials
3. Check application logs for email errors
4. Test with order confirmation email first

## Manual Email Test

If you want to test immediately without waiting:

1. Temporarily reduce reminder delay in `application.properties`:
```properties
reminder.cart.delay-minutes=1
reminder.wishlist.delay-minutes=1
```

2. Restart application
3. Add items and wait 1 minute
4. Check email

## Log Monitoring

Watch logs for email sending:
```bash
tail -f logs/ecommerce-app.log | grep -i email
```

Expected log messages:
```
Cart reminder email sent to: test@example.com
Wishlist reminder email sent to: test@example.com
```

## Success Criteria

✅ All reminder emails include user's delivery address  
✅ Default address is prioritized  
✅ Emails work gracefully without addresses  
✅ No errors in application logs  
✅ Email format is clean and readable
