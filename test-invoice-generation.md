# Invoice Generation Testing Guide

## Prerequisites
1. Application is running
2. You have admin credentials
3. At least one order exists in the system

## Test Steps

### Step 1: Create a Test Order (if needed)
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "USER_ID",
    "address": {
      "addressId": "ADDRESS_ID",
      "addressLine": "123 Test Street",
      "city": "Mumbai",
      "state": "Maharashtra",
      "postalCode": "400001",
      "country": "India"
    },
    "totalAmount": 1180.00,
    "items": [
      {
        "productId": "PRODUCT_ID",
        "quantity": 2,
        "price": 500.00
      }
    ]
  }'
```

### Step 2: Update Order Status to Delivered
This will trigger automatic invoice generation and email delivery.

```bash
curl -X PUT "http://localhost:8080/api/orders/ORDER_ID/status?status=delivered" \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

**Expected Result:**
- Order status updated to "delivered"
- Invoice generated automatically (async)
- Email sent to customer with invoice PDF
- Email sent to admin with invoice PDF

### Step 3: Verify Invoice Creation
```bash
curl -X GET http://localhost:8080/api/invoices/order/ORDER_ID \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Expected Response:**
```json
{
  "id": "INVOICE_ID",
  "invoiceNumber": "INV-20251213123456-1234",
  "orderId": "ORDER_ID",
  "userId": "USER_ID",
  "customerName": "John Doe",
  "customerEmail": "customer@example.com",
  "totalAmount": 1180.00,
  "taxAmount": 180.00,
  "subtotal": 1000.00,
  "invoiceDate": "2025-12-13T12:34:56",
  "generatedAt": "2025-12-13T12:35:00",
  "pdfPath": "https://your-bucket.r2.dev/invoices/INV-20251213123456-1234.pdf",
  "emailedToCustomer": false,
  "emailedToAdmin": false
}
```

### Step 4: Download Invoice PDF
```bash
curl -X GET http://localhost:8080/api/invoices/INVOICE_ID/download \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --output invoice.pdf
```

**Expected Result:**
- PDF file downloaded successfully
- Open `invoice.pdf` to verify content

### Step 5: Get All Invoices (Admin Only)
```bash
curl -X GET http://localhost:8080/api/invoices \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

**Expected Response:**
```json
[
  {
    "id": "INVOICE_ID",
    "invoiceNumber": "INV-20251213123456-1234",
    ...
  }
]
```

### Step 6: Download All Invoices as ZIP (Admin Only)
```bash
curl -X GET http://localhost:8080/api/invoices/download-all \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  --output all-invoices.zip
```

**Expected Result:**
- ZIP file downloaded successfully
- Extract and verify all invoice PDFs are present

## Verification Checklist

- [ ] Invoice generated automatically when order status changed to "delivered"
- [ ] Invoice number is unique and follows format: INV-YYYYMMDDHHMMSS-XXXX
- [ ] Invoice PDF contains all order details
- [ ] Invoice PDF has proper formatting (borders, alignment, etc.)
- [ ] Subtotal, tax (18% GST), and total are calculated correctly
- [ ] Customer received email with invoice PDF attachment
- [ ] Admin received email with invoice PDF attachment
- [ ] Invoice is stored in R2 storage
- [ ] Invoice metadata is saved in MongoDB
- [ ] Individual invoice download works
- [ ] All invoices ZIP download works (admin only)
- [ ] Invoice can be retrieved by order ID
- [ ] Invoice can be retrieved by invoice ID

## Email Verification

### Customer Email
- **Subject:** Invoice for Order #ORDER_ID
- **Body:** Contains invoice details
- **Attachment:** Invoice PDF

### Admin Email
- **Subject:** Invoice Generated - Order #ORDER_ID
- **Body:** Contains order and customer details
- **Attachment:** Invoice PDF

## Troubleshooting

### Invoice Not Generated
1. Check application logs for errors
2. Verify order status was updated to "delivered"
3. Check async task execution
4. Verify R2 storage credentials

### Email Not Received
1. Check spam/junk folder
2. Verify SMTP configuration in application.properties
3. Check email service logs
4. Verify email addresses are correct

### PDF Download Fails
1. Verify R2 storage is accessible
2. Check invoice pdfPath in database
3. Verify R2 credentials and bucket name
4. Check network connectivity to R2

### Invoice Template Issues
1. Verify invoice_template.jrxml exists in resources
2. Check JasperReports dependency in pom.xml
3. Verify template compilation logs

## Sample Invoice Content

```
                        INVOICE
                Adita Enterprise India

Invoice #: INV-20251213123456-1234        Date: 13-12-2025
                                          Order ID: ORDER_ID

Bill To:
John Doe
customer@example.com
123 Test Street, Mumbai, Maharashtra 400001, India

┌──────────────────────┬──────────┬──────────┬──────────┐
│ Product ID           │ Quantity │ Price    │ Total    │
├──────────────────────┼──────────┼──────────┼──────────┤
│ PRODUCT_ID           │    2     │ ₹500.00  │ ₹1000.00 │
└──────────────────────┴──────────┴──────────┴──────────┘

                                    Subtotal: ₹1000.00
                                    Tax (GST): ₹180.00
                                    ─────────────────────
                                    Total: ₹1180.00

              Thank you for your business!
```

## Notes

- Invoice generation is asynchronous and may take a few seconds
- Emails are sent asynchronously to avoid blocking
- Duplicate invoices are prevented (one invoice per order)
- Invoice numbers are unique and auto-generated
- Tax rate is configurable in application.properties (default: 18%)
