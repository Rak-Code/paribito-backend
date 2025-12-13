# Invoice Generation Flow Diagram

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         INVOICE SYSTEM                              │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────┐
│    ADMIN     │
│   Updates    │
│   Order to   │
│  "delivered" │
└──────┬───────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────────┐
│                    OrderServiceImpl                              │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  updateOrderStatus(orderId, "delivered")                   │ │
│  │  ├─ Update order status in MongoDB                         │ │
│  │  └─ Trigger: generateAndSendInvoice() [ASYNC]             │ │
│  └────────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────────┐
│                    InvoiceServiceImpl                            │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  generateInvoice(order, user)                              │ │
│  │  ├─ Generate unique invoice number                         │ │
│  │  │  Format: INV-YYYYMMDDHHMMSS-XXXX                        │ │
│  │  ├─ Calculate amounts:                                     │ │
│  │  │  • Subtotal = Total / 1.18                              │ │
│  │  │  • Tax = Total - Subtotal (18% GST)                     │ │
│  │  ├─ Load JRXML template                                    │ │
│  │  ├─ Compile JasperReport                                   │ │
│  │  ├─ Fill report with data                                  │ │
│  │  ├─ Export to PDF (ByteArrayOutputStream)                  │ │
│  │  └─ Upload PDF to R2 storage                               │ │
│  └────────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────┘
       │
       ├──────────────────┬──────────────────┐
       ▼                  ▼                  ▼
┌─────────────┐   ┌──────────────┐   ┌─────────────┐
│  MongoDB    │   │  R2 Storage  │   │ EmailService│
│             │   │              │   │             │
│ Save Invoice│   │  Upload PDF  │   │ Send Emails │
│  Metadata   │   │              │   │             │
└─────────────┘   └──────────────┘   └──────┬──────┘
                                             │
                         ┌───────────────────┴───────────────────┐
                         ▼                                       ▼
                  ┌─────────────┐                        ┌─────────────┐
                  │  CUSTOMER   │                        │    ADMIN    │
                  │             │                        │             │
                  │ Receives    │                        │ Receives    │
                  │ Email with  │                        │ Email with  │
                  │ Invoice PDF │                        │ Invoice PDF │
                  └─────────────┘                        └─────────────┘
```

## Detailed Flow

### Phase 1: Order Status Update
```
Admin → PUT /api/orders/{orderId}/status?status=delivered
         ↓
    OrderController
         ↓
    OrderServiceImpl.updateOrderStatus()
         ↓
    Update order.status = "delivered" in MongoDB
         ↓
    Trigger async: generateAndSendInvoice()
```

### Phase 2: Invoice Generation (Async)
```
InvoiceServiceImpl.generateInvoice()
    ↓
1. Generate Invoice Number
   INV-20251213123456-1234
    ↓
2. Calculate Amounts
   Total: ₹1180.00
   Subtotal: ₹1000.00 (Total / 1.18)
   Tax: ₹180.00 (18% GST)
    ↓
3. Load JRXML Template
   invoice_template.jrxml
    ↓
4. Prepare Data
   • Parameters: invoice details, customer info
   • DataSource: order items list
    ↓
5. Compile & Fill Report
   JasperCompileManager.compileReport()
   JasperFillManager.fillReport()
    ↓
6. Export to PDF
   JasperExportManager.exportReportToPdfStream()
    ↓
7. Upload to R2
   StorageService.uploadFile()
   → Returns: https://bucket.r2.dev/invoices/INV-xxx.pdf
    ↓
8. Save to MongoDB
   Invoice entity with metadata
```

### Phase 3: Email Delivery (Async)
```
EmailServiceImpl.sendInvoiceToCustomer()
    ↓
1. Download PDF from R2
   StorageService.downloadFile()
    ↓
2. Create MimeMessage
   • To: customer@example.com
   • Subject: Invoice for Order #ORDER_ID
   • Body: Invoice details
   • Attachment: Invoice PDF
    ↓
3. Send Email
   JavaMailSender.send()

EmailServiceImpl.sendInvoiceToAdmin()
    ↓
1. Download PDF from R2
    ↓
2. Create MimeMessage
   • To: admin@example.com
   • Subject: Invoice Generated - Order #ORDER_ID
   • Body: Order & customer details
   • Attachment: Invoice PDF
    ↓
3. Send Email
```

## Data Flow

### Input
```json
{
  "orderId": "ORDER123",
  "status": "delivered"
}
```

### Processing
```
Order → Invoice Generation → PDF Creation → R2 Upload → Email Delivery
```

### Output
```json
{
  "id": "INVOICE_ID",
  "invoiceNumber": "INV-20251213123456-1234",
  "orderId": "ORDER123",
  "userId": "USER123",
  "customerName": "John Doe",
  "customerEmail": "customer@example.com",
  "totalAmount": 1180.00,
  "taxAmount": 180.00,
  "subtotal": 1000.00,
  "invoiceDate": "2025-12-13T12:34:56",
  "generatedAt": "2025-12-13T12:35:00",
  "pdfPath": "https://bucket.r2.dev/invoices/INV-xxx.pdf",
  "emailedToCustomer": false,
  "emailedToAdmin": false
}
```

## Component Interaction

```
┌─────────────────┐
│ InvoiceController│
│  (REST API)     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐      ┌──────────────┐
│ InvoiceService  │─────▶│ JasperReports│
│                 │      │  (PDF Gen)   │
└────────┬────────┘      └──────────────┘
         │
         ├──────────────┬──────────────┬──────────────┐
         ▼              ▼              ▼              ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ OrderService │ │StorageService│ │ EmailService │ │InvoiceRepo   │
│              │ │   (R2/S3)    │ │   (SMTP)     │ │  (MongoDB)   │
└──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘
```

## Async Processing

```
Main Thread                    Async Thread
    │                               │
    │ Update Order Status           │
    │────────────────────▶          │
    │                               │
    │ Return Response               │
    │◀────────────────────          │
    │                               │
    │                          Generate Invoice
    │                               │
    │                          Upload to R2
    │                               │
    │                          Send Emails
    │                               │
    │                          Complete ✓
```

## Error Handling Flow

```
Try: Generate Invoice
    ├─ Success → Continue
    └─ Error → Log & Continue (Don't block order update)

Try: Upload to R2
    ├─ Success → Continue
    └─ Error → Log & Throw (Invoice generation fails)

Try: Send Email to Customer
    ├─ Success → Log success
    └─ Error → Log error (Don't block)

Try: Send Email to Admin
    ├─ Success → Log success
    └─ Error → Log error (Don't block)
```

## Storage Structure

### MongoDB Collections
```
orders/
  └─ {orderId}
      ├─ id
      ├─ userId
      ├─ status: "delivered"
      └─ ...

invoices/
  └─ {invoiceId}
      ├─ invoiceNumber
      ├─ orderId (indexed)
      ├─ userId (indexed)
      ├─ pdfPath
      └─ ...
```

### R2 Storage
```
bucket/
  └─ invoices/
      ├─ INV-20251213123456-1234.pdf
      ├─ INV-20251213123457-5678.pdf
      └─ ...
```

## Timeline

```
T+0s    Admin updates order to "delivered"
T+0.1s  Order status saved to MongoDB
T+0.2s  Async invoice generation triggered
T+0.5s  Invoice PDF generated
T+1s    PDF uploaded to R2
T+1.2s  Invoice metadata saved to MongoDB
T+1.5s  Email sent to customer
T+2s    Email sent to admin
T+2.5s  Process complete ✓
```

## Security Flow

```
Request → JWT Validation → Role Check → Execute
                              │
                              ├─ User: Can access own invoices
                              └─ Admin: Can access all invoices
```

This visual representation helps understand how all components work together to automatically generate and deliver invoices when orders are marked as delivered.
