# 📄 Invoice Generation System

## Overview

A complete, production-ready invoice generation and email delivery system for your e-commerce platform. Automatically generates professional PDF invoices when orders are delivered and emails them to customers and admins.

## ✨ Key Features

- 🤖 **Automatic Generation**: Invoices created automatically when order status changes to "delivered"
- 📧 **Email Delivery**: Invoices sent to both customer and admin with PDF attachments
- ☁️ **Cloud Storage**: All invoices stored in Cloudflare R2 (S3-compatible)
- 📊 **Professional PDFs**: Clean, branded invoices using JasperReports
- 🎨 **Company Logo**: Your logo displayed on every invoice
- 🔐 **Secure**: Role-based access control with JWT authentication
- ⚡ **Async Processing**: Non-blocking invoice generation and email delivery
- 📦 **Bulk Download**: Admin can download all invoices as a ZIP file
- 🎯 **RESTful API**: Complete API for invoice management

## 🚀 Quick Start

### 1. Configure Environment
```bash
# Edit .env file
EMAIL_USER=your_email@gmail.com
EMAIL_PASS=your_app_password
ADMIN_EMAIL=admin@example.com
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

### 3. Test
```bash
# Mark order as delivered
curl -X PUT "http://localhost:8080/api/orders/{orderId}/status?status=delivered" \
  -H "Authorization: Bearer ADMIN_TOKEN"

# Download invoice
curl -X GET "http://localhost:8080/api/invoices/{invoiceId}/download" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --output invoice.pdf
```

## 📚 Documentation

| Document | Description |
|----------|-------------|
| [INVOICE_QUICK_START.md](INVOICE_QUICK_START.md) | 5-minute setup guide |
| [INVOICE_FEATURE.md](INVOICE_FEATURE.md) | Complete feature documentation |
| [INVOICE_IMPLEMENTATION_SUMMARY.md](INVOICE_IMPLEMENTATION_SUMMARY.md) | Implementation details |
| [test-invoice-generation.md](test-invoice-generation.md) | Testing guide |
| [INVOICE_FLOW_DIAGRAM.md](INVOICE_FLOW_DIAGRAM.md) | Visual flow diagrams |
| [INVOICE_DEPLOYMENT_CHECKLIST.md](INVOICE_DEPLOYMENT_CHECKLIST.md) | Deployment checklist |
| [INVOICE_LOGO_CUSTOMIZATION.md](INVOICE_LOGO_CUSTOMIZATION.md) | Logo customization guide |

## 🎯 API Endpoints

### User Endpoints
```
GET  /api/invoices/order/{orderId}      - Get invoice by order ID
GET  /api/invoices/{invoiceId}          - Get invoice by ID
GET  /api/invoices/{invoiceId}/download - Download invoice PDF
```

### Admin Endpoints
```
GET  /api/invoices                      - Get all invoices
GET  /api/invoices/download-all         - Download all invoices (ZIP)
```

## 🔄 How It Works

```
1. Admin marks order as "delivered"
   ↓
2. System generates invoice automatically
   ↓
3. Invoice PDF created using JasperReports
   ↓
4. PDF uploaded to R2 cloud storage
   ↓
5. Email sent to customer with PDF
   ↓
6. Email sent to admin with PDF
   ↓
7. Invoice available for download via API
```

## 📧 Email Templates

### Customer Email
- **Subject**: Invoice for Order #ORDER_ID
- **Content**: Invoice details and thank you message
- **Attachment**: Professional PDF invoice

### Admin Email
- **Subject**: Invoice Generated - Order #ORDER_ID
- **Content**: Order and customer details
- **Attachment**: Professional PDF invoice

## 🎨 Invoice Format

```
                    [COMPANY LOGO]
                    
                        INVOICE
                Adita Enterprise India

Invoice #: INV-20251213123456-1234        Date: 13-12-2025
                                          Order ID: ORD123

Bill To:
John Doe
customer@example.com
123 Test Street, Mumbai, Maharashtra 400001, India

┌──────────────────────┬──────────┬──────────┬──────────┐
│ Product ID           │ Quantity │ Price    │ Total    │
├──────────────────────┼──────────┼──────────┼──────────┤
│ PROD123              │    2     │ ₹500.00  │ ₹1000.00 │
└──────────────────────┴──────────┴──────────┴──────────┘

                                    Subtotal: ₹1000.00
                                    Tax (GST): ₹180.00
                                    ─────────────────────
                                    Total: ₹1180.00

              Thank you for your business!
```

## 🛠️ Technology Stack

- **PDF Generation**: JasperReports 6.21.3
- **Email**: Spring Mail with SMTP
- **Storage**: Cloudflare R2 (S3-compatible)
- **Database**: MongoDB
- **Framework**: Spring Boot 3.5.8
- **Authentication**: JWT

## 📦 Files Structure

```
src/main/java/com/ecommerce/project/
├── entity/
│   └── Invoice.java                    # Invoice entity
├── repository/
│   └── InvoiceRepository.java          # Invoice data access
├── service/
│   ├── InvoiceService.java             # Invoice service interface
│   ├── InvoiceServiceImpl.java         # Invoice service implementation
│   ├── StorageService.java             # Storage service interface
│   ├── StorageServiceImpl.java         # Storage service implementation
│   ├── EmailService.java               # Updated with invoice methods
│   ├── EmailServiceImpl.java           # Updated with invoice emails
│   └── OrderServiceImpl.java           # Updated to trigger invoices
├── controller/
│   └── InvoiceController.java          # Invoice REST API
└── dto/
    └── InvoiceResponseDTO.java         # Invoice response DTO

src/main/resources/
└── invoice_template.jrxml              # JasperReports template
```

## ⚙️ Configuration

### Tax Rate
```properties
# application.properties
invoice.tax.rate=0.18  # 18% GST
```

### Email Settings
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USER}
spring.mail.password=${EMAIL_PASS}
email.from=${FROM_EMAIL}
email.admin=${ADMIN_EMAIL}
```

### R2 Storage
```properties
r2.account.id=${R2_ACCOUNT_ID}
r2.access.key.id=${R2_ACCESS_KEY_ID}
r2.secret.access.key=${R2_SECRET_ACCESS_KEY}
r2.bucket.name=${R2_BUCKET_NAME}
r2.public.url=${R2_PUBLIC_URL}
```

## 🧪 Testing

### Manual Testing
See [test-invoice-generation.md](test-invoice-generation.md) for detailed testing steps.

### Quick Test
```bash
# 1. Create order
# 2. Mark as delivered
# 3. Check email
# 4. Download invoice
```

## 🐛 Troubleshooting

### Invoice Not Generated
- Check logs: `logs/ecommerce-app.log`
- Verify order status is "delivered"
- Check R2 credentials

### Email Not Received
- Check spam folder
- Verify SMTP settings
- Check email logs

### PDF Download Fails
- Verify R2 storage is accessible
- Check invoice pdfPath in database

## 📊 Monitoring

### Key Metrics
- Invoice generation success rate
- Email delivery success rate
- Average generation time
- Storage usage

### Log Messages
```
✓ "Invoice generated successfully: INV-xxx"
✓ "Invoice email sent to customer: xxx"
✓ "Invoice email sent to admin: xxx"
✗ "Failed to generate invoice for order: xxx"
```

## 🔒 Security

- ✅ JWT authentication required
- ✅ Role-based access control
- ✅ Admin-only endpoints protected
- ✅ Users can only access their own invoices
- ✅ Secure credential storage

## 🚀 Deployment

See [INVOICE_DEPLOYMENT_CHECKLIST.md](INVOICE_DEPLOYMENT_CHECKLIST.md) for complete deployment guide.

### Quick Deploy
```bash
# Build
mvn clean package -DskipTests

# Deploy
java -jar target/project-*.jar
```

## 📈 Performance

- **Async Processing**: Invoice generation doesn't block order updates
- **Cloud Storage**: Scalable R2 storage
- **Efficient PDFs**: Lightweight JasperReports templates
- **Email Queue**: Async email delivery

## 🎯 Use Cases

1. **Order Completion**: Automatic invoice on delivery
2. **Customer Records**: Customers receive invoices via email
3. **Admin Management**: Admins can download all invoices
4. **Accounting**: Professional invoices for bookkeeping
5. **Tax Compliance**: GST calculations included

## 🔄 Future Enhancements

- [ ] Custom invoice templates per business
- [ ] Multi-currency support
- [ ] Invoice versioning
- [ ] Credit notes and refunds
- [ ] Bulk invoice generation
- [ ] Invoice preview
- [ ] Custom tax rates per region
- [ ] Invoice numbering customization

## 📞 Support

For issues or questions:
1. Check documentation files
2. Review logs
3. Contact development team

## 📝 License

Part of the Ecommerce Backend Project

## 👥 Contributors

Developed for Adita Enterprise India

---

**Version**: 1.0.0  
**Last Updated**: December 13, 2025  
**Status**: ✅ Production Ready

## Quick Links

- [Quick Start Guide](INVOICE_QUICK_START.md)
- [Feature Documentation](INVOICE_FEATURE.md)
- [Testing Guide](test-invoice-generation.md)
- [Deployment Checklist](INVOICE_DEPLOYMENT_CHECKLIST.md)
- [Flow Diagrams](INVOICE_FLOW_DIAGRAM.md)

---

**Ready to use!** Just configure your environment variables and start generating invoices automatically. 🎉
