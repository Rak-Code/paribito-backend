# Quick Test Guide - Email Reminder System

## Quick Setup for Testing

### 1. Adjust Timing for Fast Testing

Edit `src/main/resources/application.properties`:

```properties
# Set to 1 minute for cart reminders (instead of 30)
reminder.cart.delay-minutes=1

# Set to 2 minutes for wishlist reminders (instead of 60)
reminder.wishlist.delay-minutes=2
```

### 2. Start the Application

```bash
mvn spring-boot:run
```

---

## Test Cart Reminder

### Step 1: Add Product to Cart

```bash
POST http://localhost:8080/api/cart/add
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "productId": "YOUR_PRODUCT_ID",
  "quantity": 1
}
```

### Step 2: Wait 1 Minute

The scheduler runs every minute and will process the reminder.

### Step 3: Check Email

You should receive an email like:

```
Subject: Don't Forget Your Cart! Complete Your Purchase

Dear [Your Name],

We noticed you left something in your cart!

Don't miss out on this amazing product:

=====================================
Product: [Product Name]
Price: ₹[Price]
=====================================

Complete your purchase now before it's gone!
```

---

## Test Wishlist Reminder

### Step 1: Add Product to Wishlist

```bash
POST http://localhost:8080/api/wishlist/add
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "productId": "YOUR_PRODUCT_ID"
}
```

### Step 2: Wait 2 Minutes

The scheduler will process the reminder after 2 minutes.

### Step 3: Check Email

You should receive an email like:

```
Subject: Your Wishlist Item is Waiting! Buy Now

Dear [Your Name],

Your wishlist item is calling you! 💝

Remember this product you loved?

=====================================
Product: [Product Name]
Price: ₹[Price]
✅ In Stock - Available Now!
=====================================

Why wait? Turn your wish into reality today!
```

---

## Verify in Database

### Check Scheduled Reminders

```javascript
// MongoDB query
db.reminder_schedules.find().pretty()

// Expected output:
{
  "_id": ObjectId("..."),
  "userId": "user123",
  "productId": "product456",
  "type": "CART",
  "status": "PENDING",  // or "SENT" after processing
  "scheduledAt": ISODate("2025-11-24T15:31:00Z"),
  "sentAt": null,       // or timestamp after sending
  "createdAt": ISODate("2025-11-24T15:30:00Z")
}
```

### Check Pending Reminders

```javascript
db.reminder_schedules.find({ status: "PENDING" })
```

### Check Sent Reminders

```javascript
db.reminder_schedules.find({ status: "SENT" })
```

---

## Check Logs

Watch the application logs for:

```
[INFO] Scheduled CART reminder for user: user123 in 1 minutes
[INFO] Processing 1 pending reminders
[INFO] Cart reminder email sent to: customer@example.com
```

---

## Troubleshooting

### No Email Received?

1. **Check Email Configuration**
   ```properties
   spring.mail.username=${EMAIL_USER}
   spring.mail.password=${EMAIL_PASS}
   ```

2. **Check Spam Folder**
   - Reminder emails might be filtered as promotional

3. **Check Logs for Errors**
   ```bash
   # Look for email sending errors
   grep "Failed to send" logs/application.log
   ```

4. **Verify Scheduler is Running**
   ```bash
   # Should see this every minute
   [INFO] Processing X pending reminders
   ```

### Reminder Not Scheduled?

1. **Check if @EnableScheduling is present**
   - Should be in `AsyncConfig.java`

2. **Verify Service Injection**
   - Check `CartServiceImpl` and `WishlistServiceImpl` have `ReminderSchedulerService`

3. **Check Database Connection**
   - Ensure MongoDB is running

---

## Reset for Production

After testing, restore production timing:

```properties
# Production settings
reminder.cart.delay-minutes=30
reminder.wishlist.delay-minutes=60
```

Restart the application:

```bash
mvn spring-boot:run
```

---

## API Endpoints Reference

### Cart Operations
- `POST /api/cart/add` - Add to cart (triggers reminder)
- `GET /api/cart` - View cart
- `DELETE /api/cart/{itemId}` - Remove from cart

### Wishlist Operations
- `POST /api/wishlist/add` - Add to wishlist (triggers reminder)
- `GET /api/wishlist` - View wishlist
- `DELETE /api/wishlist/{itemId}` - Remove from wishlist

---

## Expected Behavior

✅ **Cart Reminder:** Sent 30 minutes after adding to cart
✅ **Wishlist Reminder:** Sent 60 minutes after adding to wishlist
✅ **No Duplicates:** Only one reminder per user/product/type
✅ **Stock Alerts:** Shows low stock warnings in emails
✅ **Async Processing:** Doesn't block API responses
✅ **Automatic:** Runs every minute without manual intervention

---

## Success Indicators

1. ✅ Reminder scheduled in database with PENDING status
2. ✅ Scheduler processes reminder after delay
3. ✅ Email sent successfully
4. ✅ Reminder status updated to SENT
5. ✅ User receives email with product details
