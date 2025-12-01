# Email Reminder System - Quick Reference Card

## 🚀 Quick Start

### 1. Configuration (Already Done)
```properties
# In application.properties
reminder.cart.delay-minutes=30
reminder.wishlist.delay-minutes=60
```

### 2. Start Application
```bash
mvn spring-boot:run
```

### 3. It Works Automatically!
- Add to cart → Email in 30 minutes
- Add to wishlist → Email in 60 minutes

---

## 📋 What Happens Automatically

| Action | Trigger | Delay | Email Subject |
|--------|---------|-------|---------------|
| Add to Cart | `POST /api/cart/add` | 30 min | "Don't Forget Your Cart!" |
| Add to Wishlist | `POST /api/wishlist/add` | 60 min | "Your Wishlist Item is Waiting!" |

---

## 🧪 Quick Test (2 Minutes)

### Step 1: Change Config
```properties
reminder.cart.delay-minutes=1
reminder.wishlist.delay-minutes=2
```

### Step 2: Add Product
```bash
POST /api/cart/add
{
  "productId": "product123",
  "quantity": 1
}
```

### Step 3: Wait & Check Email
- Wait 1 minute
- Check inbox for cart reminder

---

## 📊 Monitor

### Check Database
```javascript
// See all reminders
db.reminder_schedules.find().pretty()

// See pending
db.reminder_schedules.find({ status: "PENDING" })

// See sent
db.reminder_schedules.find({ status: "SENT" })
```

### Check Logs
```bash
# Look for these messages
[INFO] Scheduled CART reminder for user: user123 in 30 minutes
[INFO] Processing 5 pending reminders
[INFO] Cart reminder email sent to: customer@example.com
```

---

## 🎯 Key Features

✅ **Automatic** - No manual intervention needed
✅ **Smart** - Prevents duplicate reminders
✅ **Fast** - Async email sending
✅ **Reliable** - Runs every minute
✅ **Configurable** - Easy to adjust timing
✅ **Informative** - Includes stock alerts

---

## 📧 Email Content

### Cart Reminder
```
Subject: Don't Forget Your Cart! Complete Your Purchase

Dear [Name],
We noticed you left something in your cart!

Product: [Product Name]
Price: ₹[Price]
⚠️ Only [X] left in stock!

Complete your purchase now!
```

### Wishlist Reminder
```
Subject: Your Wishlist Item is Waiting! Buy Now

Dear [Name],
Your wishlist item is calling you! 💝

Product: [Product Name]
Price: ₹[Price]
✅ In Stock - Available Now!

Turn your wish into reality today!
```

---

## 🔧 Troubleshooting

| Problem | Solution |
|---------|----------|
| No emails | Check email config in .env |
| Wrong timing | Check delay in properties |
| Not scheduling | Verify @EnableScheduling |
| Duplicates | Check database for existing reminders |

---

## 📁 Files Created

**New Files:**
- `ReminderSchedule.java` - Entity
- `ReminderScheduleRepository.java` - Repository
- `ReminderSchedulerService.java` - Interface
- `ReminderSchedulerServiceImpl.java` - Implementation

**Modified Files:**
- `EmailService.java` - Added methods
- `EmailServiceImpl.java` - Added templates
- `CartServiceImpl.java` - Added scheduling
- `WishlistServiceImpl.java` - Added scheduling
- `AsyncConfig.java` - Enabled scheduling
- `application.properties` - Added config

**Documentation:**
- `EMAIL_REMINDER_GUIDE.md` - Full guide
- `EMAIL_REMINDER_QUICK_TEST.md` - Test guide
- `EMAIL_REMINDER_IMPLEMENTATION_SUMMARY.md` - Summary
- `EMAIL_REMINDER_QUICK_REFERENCE.md` - This card

---

## 🎉 That's It!

The system is ready to use. Just start your application and it will automatically send reminder emails to customers who add items to their cart or wishlist.

**No additional setup required!**
