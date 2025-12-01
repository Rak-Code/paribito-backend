# Email Integration Guide

## Overview
SMTP-based email functionality has been integrated into the e-commerce application. When a customer places an order, two emails are automatically sent:
1. **Order confirmation email** to the customer
2. **Order notification email** to the admin

## Configuration

### Environment Variables (.env)
```properties
EMAIL_USER=aditaenterpriseindia@gmail.com
EMAIL_PASS=rbwlohwdgoadgget
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
FROM_EMAIL=aditaenterpriseindia@gmail.com
ADMIN_EMAIL=aditaenterpriseindia@gmail.com
```

### Application Properties
Email configuration is loaded from environment variables in `application.properties`:
- SMTP host, port, username, and password
- Authentication and TLS settings
- From and admin email addresses

## Features

### 1. Customer Order Confirmation Email
Sent to the customer's registered email address containing:
- Order ID and date
- Order status
- Total amount
- List of items ordered (Product ID, quantity, price)
- Shipping address
- Thank you message

### 2. Admin Order Notification Email
Sent to the admin email address containing:
- Order ID and date
- Order status
- Total amount
- Customer details (name, email, phone, user ID)
- List of items ordered
- Shipping address

## Implementation Details

### Files Created/Modified

1. **pom.xml** - Added Spring Boot Mail Starter dependency
2. **application.properties** - Added email configuration
3. **EmailService.java** - Email service interface
4. **EmailServiceImpl.java** - Email service implementation with async support
5. **AsyncConfig.java** - Enables asynchronous email sending
6. **OrderServiceImpl.java** - Integrated email sending in order creation

### Asynchronous Email Sending
Emails are sent asynchronously using `@Async` annotation to avoid blocking the order creation process. If email sending fails, the order creation will still succeed.

## Testing

### Prerequisites
1. Ensure Gmail account allows "Less secure app access" or use an App Password
2. Verify SMTP credentials in `.env` file
3. Ensure the application can connect to smtp.gmail.com on port 587

### Test Order Creation
1. Create an order via the API endpoint
2. Check the customer's email inbox for order confirmation
3. Check the admin email inbox (aditaenterpriseindia@gmail.com) for order notification
4. Check application logs for email sending status

### Sample API Request
```bash
POST /api/orders
Content-Type: application/json

{
  "userId": "user123",
  "address": {
    "addressLine": "123 Main Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "postalCode": "400001",
    "country": "India"
  },
  "totalAmount": 1500.00,
  "items": [
    {
      "productId": "prod123",
      "quantity": 2,
      "price": 750.00
    }
  ]
}
```

## Troubleshooting

### Email Not Sending
1. Check SMTP credentials in `.env` file
2. Verify Gmail account settings (App Password or Less secure apps)
3. Check application logs for error messages
4. Verify network connectivity to smtp.gmail.com:587

### Gmail Security
If using Gmail, you may need to:
- Enable 2-factor authentication
- Generate an App Password specifically for this application
- Use the App Password instead of your regular password

### Logs
Check application logs for:
- "Order confirmation email sent to customer: {email}"
- "Order notification email sent to admin: {email}"
- Any error messages related to email sending

## Future Enhancements
- HTML email templates with better formatting
- Email templates for order status updates
- Email notifications for order cancellation
- Attachment support (invoices, receipts)
- Email queue for retry mechanism
