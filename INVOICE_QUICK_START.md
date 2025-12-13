# Invoice System - Quick Start Guide

## 🚀 Quick Setup (5 Minutes)

### 1. Environment Variables
Ensure these are set in your `.env` file:
```properties
# Email (Required)
EMAIL_USER=your_email@gmail.com
EMAIL_PASS=your_app_password
FROM_EMAIL=your_email@gmail.com
ADMIN_EMAIL=admin@example.com

# R2 Storage (Required)
R2_ACCOUNT_ID=your_account_id
R2_ACCESS_KEY_ID=your_access_key
R2_SECRET_ACCESS_KEY=your_secret_key
R2_BUCKET_NAME=your_bucket
R2_PUBLIC_URL=https://your-bucket.r2.dev
```

### 2. Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

### 3. Test Invoice Generation
```bash
# Update order to delivered (triggers invoice generation)
curl -X PUT "http://localhost:8080/api/orders/{orderId}/status?status=delivered" \
  -H "Authorization: Bearer ADMIN_TOKEN"

# Wait 5-10 seconds for async processing

# Get invoice
curl -X GET "http://localhost:8080/api/invoices/order/{orderId}" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Download invoice PDF
curl -X GET "http://localhost:8080/api/invoices/{invoiceId}/download" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --output invoice.pdf
```

## 📋 What Happens Automatically

When you mark an order as **"delivered"**:

1. ✅ Invoice PDF is generated
2. ✅ Stored in R2 cloud storage
3. ✅ Email sent to customer with PDF
4. ✅ Email sent to admin with PDF
5. ✅ Available for download via API

## 🎯 API Endpoints

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/api/invoices` | GET | Admin | Get all invoices |
| `/api/invoices/{id}` | GET | User | Get invoice by ID |
| `/api/invoices/order/{orderId}` | GET | User | Get invoice by order ID |
| `/api/invoices/{id}/download` | GET | User | Download invoice PDF |
| `/api/invoices/download-all` | GET | Admin | Download all invoices (ZIP) |

## 📧 Email Format

### Customer Email
- **Subject**: Invoice for Order #ORDER_ID
- **Attachment**: Invoice PDF
- **Content**: Invoice details and thank you message

### Admin Email
- **Subject**: Invoice Generated - Order #ORDER_ID
- **Attachment**: Invoice PDF
- **Content**: Order and customer details

## 🎨 Invoice Content

- **Company logo** (customizable at `src/main/resources/images/logo.png`)
- Company name: Adita Enterprise India
- Invoice number: INV-YYYYMMDDHHMMSS-XXXX
- Order details
- Customer information
- Itemized products
- Subtotal, Tax (18% GST), Total
- Professional formatting

## ⚙️ Configuration

### Tax Rate (Optional)
Edit `application.properties`:
```properties
invoice.tax.rate=0.18  # 18% GST (default)
```

## 🐛 Troubleshooting

### Invoice not generated?
- Check logs: `logs/ecommerce-app.log`
- Verify order status is "delivered"
- Check R2 credentials

### Email not received?
- Check spam folder
- Verify SMTP settings
- Check email logs

### PDF download fails?
- Verify R2 storage is accessible
- Check invoice pdfPath in database

## 📚 Full Documentation

- **Feature Guide**: `INVOICE_FEATURE.md`
- **Testing Guide**: `test-invoice-generation.md`
- **Implementation Summary**: `INVOICE_IMPLEMENTATION_SUMMARY.md`

## ✅ Verification Checklist

- [ ] Environment variables configured
- [ ] Application builds successfully
- [ ] Order can be marked as delivered
- [ ] Invoice is generated automatically
- [ ] Customer receives email with PDF
- [ ] Admin receives email with PDF
- [ ] Invoice can be downloaded via API
- [ ] Invoice PDF is properly formatted

## 🎉 That's It!

Your invoice system is ready to use. Just mark orders as "delivered" and everything happens automatically!

For detailed information, see the full documentation files.
