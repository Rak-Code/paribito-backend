# Email Reminder System - Implementation Guide

## Overview

This system automatically sends reminder emails to customers who have added products to their cart or wishlist but haven't completed the purchase. The reminders are sent after a configurable delay (default: 30 minutes for cart, 60 minutes for wishlist).

## Features

✅ **Automatic Cart Reminders** - Sends emails when items remain in cart
✅ **Automatic Wishlist Reminders** - Sends emails for wishlist items
✅ **Configurable Delays** - Customize reminder timing via properties
✅ **Smart Scheduling** - Prevents duplicate reminders
✅ **Stock Alerts** - Highlights low stock in reminder emails
✅ **Async Processing** - Non-blocking email sending
✅ **Scheduled Processing** - Runs every minute to check pending reminders

---

## Files Created

### 1. Entity
- `src/main/java/com/ecommerce/project/entity/ReminderSchedule.java`
  - Stores scheduled reminders in MongoDB
  - Tracks status (PENDING, SENT, CANCELLED)
  - Supports CART and WISHLIST types

### 2. Repository
- `src/main/java/com/ecommerce/project/repository/ReminderScheduleRepository.java`
  - MongoDB repository for reminder operations
  - Query methods for finding pending reminders

### 3. Service Interface & Implementation
- `src/main/java/com/ecommerce/project/service/ReminderSchedulerService.java`
- `src/main/java/com/ecommerce/project/service/ReminderSchedulerServiceImpl.java`
  - Schedules cart and wishlist reminders
  - Processes pending reminders every minute
  - Cancels reminders when needed

### 4. Updated Files
- `src/main/java/com/ecommerce/project/service/EmailService.java` - Added reminder methods
- `src/main/java/com/ecommerce/project/service/EmailServiceImpl.java` - Implemented reminder emails
- `src/main/java/com/ecommerce/project/service/CartServiceImpl.java` - Schedules reminders on add
- `src/main/java/com/ecommerce/project/service/WishlistServiceImpl.java` - Schedules reminders on add
- `src/main/java/com/ecommerce/project/config/AsyncConfig.java` - Enabled scheduling
- `src/main/resources/application.properties` - Added reminder configuration

---

## Configuration

### Application Properties

```properties
# Email Reminder Configuration
reminder.cart.delay-minutes=30        # Cart reminder delay (default: 30 minutes)
reminder.wishlist.delay-minutes=60    # Wishlist reminder delay (default: 60 minutes)
```

### Customization Options

You can customize the delay times by modifying `application.properties`:

```properties
# Send cart reminders after 15 minutes
reminder.cart.delay-minutes=15

# Send wishlist reminders after 2 hours (120 minutes)
reminder.wishlist.delay-minutes=120
```

---

## How It Works

### 1. User Adds Item to Cart/Wishlist

When a user adds a product:
```
User adds product → CartService/WishlistService → Schedule reminder
```

### 2. Reminder Scheduling

```java
// Cart reminder scheduled for 30 minutes later
reminderSchedulerService.scheduleCartReminder(userId, productId, 30);

// Wishlist reminder scheduled for 60 minutes later
reminderSchedulerService.scheduleWishlistReminder(userId, productId, 60);
```

### 3. Automatic Processing

The scheduler runs every minute:
```
Every 1 minute → Check pending reminders → Send emails → Update status
```

### 4. Email Sent

Reminder emails include:
- Product name and price
- Stock availability alerts
- Personalized message
- Call-to-action to complete purchase

---

## Email Templates

### Cart Reminder Email

```
Subject: Don't Forget Your Cart! Complete Your Purchase

Dear [Customer Name],

We noticed you left something in your cart!

Don't miss out on this amazing product:

=====================================
Product: [Product Name]
Price: ₹[Price]
⚠️ Only [X] left in stock!
=====================================

Complete your purchase now before it's gone!
```

### Wishlist Reminder Email

```
Subject: Your Wishlist Item is Waiting! Buy Now

Dear [Customer Name],

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

## API Behavior

### Adding to Cart
```http
POST /api/cart/add
```
**Behavior:** Automatically schedules a reminder email after 30 minutes

### Adding to Wishlist
```http
POST /api/wishlist/add
```
**Behavior:** Automatically schedules a reminder email after 60 minutes

### Removing from Cart/Wishlist
**Note:** Currently, reminders are NOT automatically cancelled when items are removed. This is intentional - the reminder serves as a gentle nudge even if the user removed the item.

---

## Database Schema

### ReminderSchedule Collection

```javascript
{
  "_id": "ObjectId",
  "userId": "user123",
  "productId": "product456",
  "type": "CART",              // or "WISHLIST"
  "status": "PENDING",         // PENDING, SENT, CANCELLED
  "scheduledAt": "2025-11-24T15:30:00",
  "sentAt": null,
  "createdAt": "2025-11-24T15:00:00"
}
```

---

## Testing

### 1. Test Cart Reminder

```bash
# 1. Add product to cart
POST http://localhost:8080/api/cart/add
{
  "productId": "product123",
  "quantity": 1
}

# 2. Wait 30 minutes (or change config to 1 minute for testing)
# 3. Check email inbox for reminder
```

### 2. Test Wishlist Reminder

```bash
# 1. Add product to wishlist
POST http://localhost:8080/api/wishlist/add
{
  "productId": "product123"
}

# 2. Wait 60 minutes (or change config to 2 minutes for testing)
# 3. Check email inbox for reminder
```

### 3. Quick Testing (Modify Config)

For faster testing, temporarily change delays:

```properties
# Test with 1 minute delays
reminder.cart.delay-minutes=1
reminder.wishlist.delay-minutes=2
```

---

## Monitoring & Logs

### Check Logs

```bash
# View reminder scheduling
[INFO] Scheduled CART reminder for user: user123 in 30 minutes

# View reminder processing
[INFO] Processing 5 pending reminders

# View email sending
[INFO] Cart reminder email sent to: customer@example.com
```

### MongoDB Queries

```javascript
// Check pending reminders
db.reminder_schedules.find({ status: "PENDING" })

// Check sent reminders
db.reminder_schedules.find({ status: "SENT" })

// Check reminders for specific user
db.reminder_schedules.find({ userId: "user123" })
```

---

## Advanced Features

### Prevent Duplicate Reminders

The system automatically prevents duplicate reminders:
- Checks if a pending reminder already exists
- Only creates new reminder if none exists

### Smart Stock Alerts

Emails include stock information:
- **Low Stock:** "⚠️ Only 3 left in stock!"
- **In Stock:** "✅ In Stock - Available Now!"
- **Out of Stock:** "❌ Currently Out of Stock"

### Async Email Sending

Emails are sent asynchronously:
- Non-blocking operations
- Doesn't slow down API responses
- Handles failures gracefully

---

## Troubleshooting

### Reminders Not Sending

1. **Check Email Configuration**
   ```properties
   spring.mail.host=smtp.gmail.com
   spring.mail.username=${EMAIL_USER}
   spring.mail.password=${EMAIL_PASS}
   ```

2. **Verify Scheduling is Enabled**
   ```java
   @EnableScheduling  // Should be in AsyncConfig.java
   ```

3. **Check Logs**
   ```bash
   # Look for errors in logs
   logging.level.com.ecommerce.project=DEBUG
   ```

### Reminders Sending Too Early/Late

Adjust delay configuration:
```properties
reminder.cart.delay-minutes=30
reminder.wishlist.delay-minutes=60
```

### Multiple Reminders for Same Product

This shouldn't happen due to duplicate prevention. If it does:
- Check database for duplicate entries
- Verify the `findByUserIdAndProductIdAndTypeAndStatus` query

---

## Future Enhancements

Potential improvements:

1. **Cancel on Purchase** - Cancel reminders when order is placed
2. **Cancel on Removal** - Cancel reminders when item is removed
3. **Multiple Reminders** - Send follow-up reminders (e.g., 1 hour, 24 hours)
4. **Personalized Timing** - Learn user behavior and optimize timing
5. **A/B Testing** - Test different email content and timing
6. **Rich HTML Emails** - Use HTML templates instead of plain text
7. **Discount Codes** - Include special offers in reminder emails
8. **Analytics** - Track open rates and conversion rates

---

## Summary

The email reminder system is now fully integrated and will:

✅ Automatically send cart reminders after 30 minutes
✅ Automatically send wishlist reminders after 60 minutes
✅ Process reminders every minute
✅ Include product details and stock alerts
✅ Prevent duplicate reminders
✅ Log all activities for monitoring

**No additional setup required** - The system works automatically once the application starts!
