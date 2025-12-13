# Invoice System - Deployment Checklist

## Pre-Deployment Checklist

### 1. Environment Configuration ✓

#### Email Configuration
- [ ] `EMAIL_USER` - Gmail account configured
- [ ] `EMAIL_PASS` - App-specific password generated
- [ ] `FROM_EMAIL` - Sender email address set
- [ ] `ADMIN_EMAIL` - Admin email address set
- [ ] `SMTP_HOST` - SMTP server configured (smtp.gmail.com)
- [ ] `SMTP_PORT` - SMTP port set (587)
- [ ] Gmail "Less secure app access" or "App Password" enabled

#### R2 Storage Configuration
- [ ] `R2_ACCOUNT_ID` - Cloudflare R2 account ID
- [ ] `R2_ACCESS_KEY_ID` - R2 access key
- [ ] `R2_SECRET_ACCESS_KEY` - R2 secret key
- [ ] `R2_BUCKET_NAME` - Bucket created and name set
- [ ] `R2_PUBLIC_URL` - Public URL configured
- [ ] R2 bucket has public read access for invoices folder
- [ ] R2 CORS configured if needed

#### Application Configuration
- [ ] `invoice.tax.rate` - Tax rate configured (default: 0.18)
- [ ] Async execution enabled (`@EnableAsync` in AsyncConfig)
- [ ] MongoDB connection configured
- [ ] JWT authentication working

### 2. Dependencies ✓

- [ ] JasperReports 6.21.3 added to pom.xml
- [ ] Spring Mail starter included
- [ ] AWS S3 SDK included (for R2)
- [ ] All dependencies downloaded (`mvn clean install`)

### 3. Files & Resources ✓

- [ ] `invoice_template.jrxml` exists in `src/main/resources/`
- [ ] Invoice entity created
- [ ] InvoiceRepository created
- [ ] InvoiceService & InvoiceServiceImpl created
- [ ] StorageService & StorageServiceImpl created
- [ ] InvoiceController created
- [ ] EmailService updated with invoice methods
- [ ] OrderService updated to trigger invoice generation

### 4. Database ✓

- [ ] MongoDB connection working
- [ ] `invoices` collection will be auto-created
- [ ] Indexes on `orderId` and `userId` will be auto-created
- [ ] Unique index on `invoiceNumber` will be auto-created

### 5. Build & Compile ✓

```bash
# Clean and compile
mvn clean compile

# Run tests (if any)
mvn test

# Package application
mvn clean package -DskipTests

# Verify JAR created
ls target/*.jar
```

- [ ] Project compiles without errors
- [ ] No compilation warnings (except unchecked operations)
- [ ] JAR file created successfully

## Deployment Steps

### Step 1: Environment Setup

1. **Production Environment Variables**
```bash
# Copy .env.example to .env
cp .env.example .env

# Edit .env with production values
nano .env
```

2. **Verify Configuration**
```bash
# Check all required variables are set
grep -E "EMAIL_|R2_|ADMIN_EMAIL" .env
```

### Step 2: Build Application

```bash
# Clean build
mvn clean package -DskipTests

# Verify build
ls -lh target/project-*.jar
```

### Step 3: Deploy to Server

#### Option A: Railway/Heroku
```bash
# Push to Git
git add .
git commit -m "Add invoice generation feature"
git push origin main

# Set environment variables in Railway/Heroku dashboard
# Deploy will happen automatically
```

#### Option B: Docker
```bash
# Build Docker image
docker build -t ecommerce-app .

# Run container
docker run -d \
  --name ecommerce-app \
  --env-file .env \
  -p 8080:8080 \
  ecommerce-app
```

#### Option C: Traditional Server
```bash
# Copy JAR to server
scp target/project-*.jar user@server:/opt/app/

# SSH to server
ssh user@server

# Run application
cd /opt/app
java -jar project-*.jar
```

### Step 4: Verify Deployment

1. **Check Application Health**
```bash
curl http://your-domain:8080/actuator/health
```

2. **Check Swagger UI**
```
http://your-domain:8080/swagger-ui.html
```

3. **Verify Invoice Endpoints**
```bash
# Get all invoices (admin)
curl -X GET http://your-domain:8080/api/invoices \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

## Post-Deployment Testing

### Test 1: Create Order
```bash
curl -X POST http://your-domain:8080/api/orders \
  -H "Authorization: Bearer USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{...order data...}'
```

### Test 2: Mark Order as Delivered
```bash
curl -X PUT "http://your-domain:8080/api/orders/{orderId}/status?status=delivered" \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

### Test 3: Wait & Verify
```bash
# Wait 10 seconds for async processing
sleep 10

# Check invoice created
curl -X GET "http://your-domain:8080/api/invoices/order/{orderId}" \
  -H "Authorization: Bearer USER_TOKEN"
```

### Test 4: Download Invoice
```bash
curl -X GET "http://your-domain:8080/api/invoices/{invoiceId}/download" \
  -H "Authorization: Bearer USER_TOKEN" \
  --output test-invoice.pdf

# Verify PDF
file test-invoice.pdf
# Should output: test-invoice.pdf: PDF document
```

### Test 5: Check Emails
- [ ] Customer received email with invoice PDF
- [ ] Admin received email with invoice PDF
- [ ] PDF attachments are valid and readable
- [ ] Email content is correct

### Test 6: R2 Storage
- [ ] Invoice PDF uploaded to R2
- [ ] PDF accessible via public URL
- [ ] PDF downloads correctly from R2

### Test 7: Admin Features
```bash
# Get all invoices
curl -X GET http://your-domain:8080/api/invoices \
  -H "Authorization: Bearer ADMIN_TOKEN"

# Download all invoices
curl -X GET http://your-domain:8080/api/invoices/download-all \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  --output all-invoices.zip

# Verify ZIP
unzip -l all-invoices.zip
```

## Monitoring & Logs

### Check Application Logs
```bash
# View logs
tail -f logs/ecommerce-app.log

# Search for invoice-related logs
grep -i "invoice" logs/ecommerce-app.log

# Check for errors
grep -i "error" logs/ecommerce-app.log | grep -i "invoice"
```

### Monitor Key Metrics
- [ ] Invoice generation success rate
- [ ] Email delivery success rate
- [ ] R2 upload success rate
- [ ] Average invoice generation time
- [ ] Async task queue size

### Log Messages to Watch For
```
✓ "Invoice generated successfully: INV-xxx"
✓ "Invoice email sent to customer: customer@example.com"
✓ "Invoice email sent to admin: admin@example.com"
✓ "File uploaded successfully: https://..."

✗ "Failed to generate invoice for order: xxx"
✗ "Failed to send invoice email to customer: xxx"
✗ "Failed to upload file: xxx"
```

## Rollback Plan

If issues occur:

### Step 1: Identify Issue
```bash
# Check logs
tail -100 logs/ecommerce-app.log

# Check database
mongo
> use ecommerce_db
> db.invoices.find().limit(5)
```

### Step 2: Quick Fixes

**Email Issues:**
```bash
# Verify SMTP settings
echo $EMAIL_USER
echo $SMTP_HOST

# Test email manually
# Use a test script or email client
```

**R2 Issues:**
```bash
# Verify R2 credentials
echo $R2_ACCESS_KEY_ID
echo $R2_BUCKET_NAME

# Test R2 access
aws s3 ls s3://$R2_BUCKET_NAME --endpoint-url=...
```

**Invoice Generation Issues:**
```bash
# Check JasperReports template
ls -l src/main/resources/invoice_template.jrxml

# Verify dependencies
mvn dependency:tree | grep jasper
```

### Step 3: Rollback (if needed)
```bash
# Revert to previous version
git revert HEAD
git push origin main

# Or deploy previous JAR
java -jar project-previous-version.jar
```

## Performance Optimization

### Async Configuration
```java
// Adjust thread pool size if needed
@Bean
public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(100);
    return executor;
}
```

### R2 Upload Optimization
- Use multipart upload for large files
- Enable compression if needed
- Configure appropriate timeouts

### Email Optimization
- Use connection pooling
- Configure appropriate timeouts
- Consider email queue for high volume

## Security Checklist

- [ ] JWT authentication enabled
- [ ] Admin endpoints protected with `@PreAuthorize`
- [ ] User can only access their own invoices
- [ ] R2 credentials stored securely (environment variables)
- [ ] Email credentials stored securely
- [ ] HTTPS enabled in production
- [ ] CORS configured properly
- [ ] Rate limiting configured (if needed)

## Maintenance

### Regular Tasks
- [ ] Monitor disk space for logs
- [ ] Rotate logs regularly
- [ ] Monitor R2 storage usage
- [ ] Review invoice generation metrics
- [ ] Check email delivery rates
- [ ] Update dependencies periodically

### Backup Strategy
- [ ] MongoDB backups configured
- [ ] R2 bucket versioning enabled
- [ ] Invoice metadata backed up regularly

## Support & Documentation

### For Users
- [ ] User guide created
- [ ] FAQ document prepared
- [ ] Support email configured

### For Developers
- [ ] API documentation updated (Swagger)
- [ ] Code comments added
- [ ] Architecture diagrams created
- [ ] Troubleshooting guide prepared

## Success Criteria

✅ **Deployment is successful when:**
- [ ] Application starts without errors
- [ ] All endpoints respond correctly
- [ ] Invoice generation works automatically
- [ ] Emails are delivered successfully
- [ ] PDFs are generated correctly
- [ ] R2 storage is working
- [ ] Admin can download all invoices
- [ ] No errors in logs
- [ ] Performance is acceptable
- [ ] All tests pass

## Contact & Escalation

**If issues persist:**
1. Check logs: `logs/ecommerce-app.log`
2. Review documentation: `INVOICE_FEATURE.md`
3. Check troubleshooting: `test-invoice-generation.md`
4. Contact development team

---

**Deployment Date:** _____________

**Deployed By:** _____________

**Version:** 0.0.1-SNAPSHOT

**Status:** ☐ Success  ☐ Issues  ☐ Rollback

**Notes:**
_____________________________________________
_____________________________________________
_____________________________________________
