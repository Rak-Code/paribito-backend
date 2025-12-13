# Invoice Generation Feature

## Overview
Automated invoice generation and email delivery system for e-commerce orders using JasperReports.

## Features

### 1. Automatic Invoice Generation
- Invoices are automatically generated when order status changes to **"delivered"**
- Uses lightweight JasperReports (JRXML) template
- Generates professional PDF invoices with all order details

### 2. Email Delivery
- **Customer Email**: Invoice PDF is automatically sent to customer's email
- **Admin Email**: Invoice PDF is automatically sent to admin's email
- Emails are sent asynchronously to avoid blocking order processing

### 3. Invoice Storage
- All invoices are stored in Cloudflare R2 (S3-compatible storage)
- Invoices are accessible via public URLs
- Metadata stored in MongoDB for quick retrieval

### 4. Admin Endpoints

#### Get All Invoices (Admin Only)
```
GET /api/invoices
Authorization: Bearer <admin-token>
```

#### Get Invoice by ID
```
GET /api/invoices/{invoiceId}
Authorization: Bearer <token>
```

#### Get Invoice by Order ID
```
GET /api/invoices/order/{orderId}
Authorization: Bearer <token>
```

#### Download Individual Invoice
```
GET /api/invoices/{invoiceId}/download
Authorization: Bearer <token>
```
Returns: PDF file

#### Download All Invoices (Admin Only)
```
GET /api/invoices/download-all
Authorization: Bearer <admin-token>
```
Returns: ZIP file containing all invoices

## Invoice Template

The invoice includes:
- **Company logo** (from resources/images/logo.png)
- Company name (Adita Enterprise India)
- Invoice number (auto-generated: INV-YYYYMMDDHHMMSS-XXXX)
- Invoice date
- Order ID
- Customer details (name, email, address)
- Itemized list of products with quantities and prices
- Subtotal
- Tax amount (18% GST)
- Total amount
- Professional formatting with borders and styling

## Technical Details

### Dependencies
- **JasperReports 6.21.3**: Lightweight PDF generation
- **Spring Mail**: Email delivery with attachments
- **AWS S3 SDK**: R2 storage integration

### Database Schema
```java
Invoice {
    id: String
    invoiceNumber: String (unique)
    orderId: String (indexed)
    userId: String (indexed)
    customerName: String
    customerEmail: String
    totalAmount: Double
    taxAmount: Double
    subtotal: Double
    invoiceDate: LocalDateTime
    generatedAt: LocalDateTime
    pdfPath: String (R2 URL)
    emailedToCustomer: Boolean
    emailedToAdmin: Boolean
}
```

### Workflow

1. **Order Delivered**
   - Admin updates order status to "delivered"
   - Triggers async invoice generation

2. **Invoice Generation**
   - Generates unique invoice number
   - Calculates subtotal and tax (18% GST)
   - Compiles JRXML template with order data
   - Generates PDF

3. **Storage**
   - Uploads PDF to R2 storage
   - Saves invoice metadata to MongoDB

4. **Email Delivery**
   - Downloads PDF from R2
   - Sends email to customer with PDF attachment
   - Sends email to admin with PDF attachment

## Configuration

### Environment Variables
```properties
# Email Configuration
EMAIL_USER=your-email@gmail.com
EMAIL_PASS=your-app-password
FROM_EMAIL=aditaenterpriseindia@gmail.com
ADMIN_EMAIL=admin@example.com

# R2 Storage
R2_ACCOUNT_ID=your-account-id
R2_ACCESS_KEY_ID=your-access-key
R2_SECRET_ACCESS_KEY=your-secret-key
R2_BUCKET_NAME=your-bucket-name
R2_PUBLIC_URL=https://your-bucket.r2.dev
```

### Tax Configuration
Edit `application.properties`:
```properties
invoice.tax.rate=0.18  # 18% GST
```

## Testing

### 1. Create an Order
```bash
POST /api/orders
```

### 2. Update Order Status to Delivered
```bash
PUT /api/orders/{orderId}/status?status=delivered
```

### 3. Check Invoice Generation
```bash
GET /api/invoices/order/{orderId}
```

### 4. Download Invoice
```bash
GET /api/invoices/{invoiceId}/download
```

## Email Templates

### Customer Email
- Subject: "Invoice for Order #ORDER_ID"
- Body: Order details with invoice attachment
- Attachment: Invoice PDF

### Admin Email
- Subject: "Invoice Generated - Order #ORDER_ID"
- Body: Order and customer details with invoice attachment
- Attachment: Invoice PDF

## Error Handling

- Invoice generation failures are logged but don't block order status updates
- Email delivery failures are logged but don't block invoice generation
- Duplicate invoice prevention (checks if invoice already exists for order)

## Future Enhancements

1. Custom invoice templates per business
2. Multi-currency support
3. Invoice versioning
4. Credit notes and refund invoices
5. Bulk invoice generation
6. Invoice preview before generation
7. Custom tax rates per region
8. Invoice numbering customization

## Support

For issues or questions, contact the development team.
