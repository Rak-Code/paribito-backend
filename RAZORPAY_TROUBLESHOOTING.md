# Razorpay Integration - Troubleshooting Guide

## 🔍 Common Issues and Solutions

### 1. Order Creation Fails

#### Error: "Failed to create Razorpay order"

**Possible Causes:**
- Invalid API credentials
- Network connectivity issues
- Incorrect amount format

**Solutions:**
```bash
# Verify credentials in application.properties
razorpay.key.id=rzp_test_xxxxxxxxxxxxx
razorpay.key.secret=your_secret_key

# Check if credentials are correct in Razorpay Dashboard
# Settings → API Keys

# Verify amount is positive and in correct format
# Amount should be in rupees (e.g., 1000.00)
```

**Test:**
```bash
curl -X POST http://localhost:8080/api/payments/razorpay/create-order \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"orderId":"TEST123","amount":100.00}'
```

---

### 2. Payment Verification Fails

#### Error: "Invalid payment signature"

**Possible Causes:**
- Incorrect Key Secret
- Signature mismatch
- Tampered payment data

**Solutions:**
```java
// Verify Key Secret is correct
@Value("${razorpay.key.secret}")
private String keySecret;

// Ensure signature verification is using correct secret
boolean isValidSignature = Utils.verifyPaymentSignature(options, keySecret);
```

**Debug Steps:**
1. Log the received signature
2. Log the generated signature
3. Compare both
4. Verify Key Secret matches Razorpay dashboard

---

### 3. Payment Not Found

#### Error: "Payment not found for order"

**Possible Causes:**
- Order not created in database
- Incorrect order ID
- Database connection issues

**Solutions:**
```bash
# Check MongoDB connection
mongo
use ecommerce_db
db.payments.find({orderId: "ORDER123"})

# Verify order was created
# Check application logs for order creation
```

**Fix:**
```java
// Ensure payment is saved after order creation
Payment payment = new Payment();
payment.setOrderId(dto.orderId());
payment.setRazorpayOrderId(order.get("id"));
paymentRepository.save(payment); // Make sure this executes
```

---

### 4. Refund Fails

#### Error: "Refund failed"

**Possible Causes:**
- Payment not in completed status
- Insufficient balance in Razorpay account
- Invalid payment ID
- Refund amount exceeds payment amount

**Solutions:**
```java
// Check payment status before refund
if (payment.getPaymentStatus() != Payment.PaymentStatus.completed) {
    throw new RuntimeException("Payment must be completed before refund");
}

// Verify refund amount
if (amount > payment.getAmount()) {
    throw new RuntimeException("Refund amount exceeds payment amount");
}
```

**Verify in Razorpay Dashboard:**
1. Go to Transactions → Payments
2. Find the payment
3. Check if refund is allowed
4. Verify account balance

---

### 5. Razorpay Checkout Not Opening

#### Error: Checkout modal doesn't appear

**Possible Causes:**
- Razorpay script not loaded
- Invalid order ID
- JavaScript errors

**Solutions:**
```html
<!-- Ensure script is loaded -->
<script src="https://checkout.razorpay.com/v1/checkout.js"></script>

<!-- Check browser console for errors -->
<!-- Verify order ID is correct -->
```

**Debug:**
```javascript
// Add error handling
const razorpay = new Razorpay(options);
razorpay.on('payment.failed', function (response){
    console.error('Payment failed:', response.error);
});
razorpay.open();
```

---

### 6. CORS Issues

#### Error: "CORS policy: No 'Access-Control-Allow-Origin' header"

**Solution:**
```java
// Add CORS configuration
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "https://yourdomain.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

---

### 7. JWT Token Issues

#### Error: "Unauthorized" or "Invalid token"

**Solutions:**
```javascript
// Ensure token is included in request
fetch('/api/payments/razorpay/create-order', {
    headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    }
})

// Verify token is not expired
// Check token format (should start with "Bearer ")
```

---

### 8. Amount Mismatch

#### Error: "Amount mismatch" or incorrect amount charged

**Cause:**
- Amount not converted to paise correctly

**Solution:**
```javascript
// Frontend: Amount should be in paise (multiply by 100)
amount: orderData.amount * 100

// Backend: Amount should be in paise
orderRequest.put("amount", (int) (dto.amount() * 100));
```

---

### 9. Database Connection Issues

#### Error: "Unable to save payment"

**Solutions:**
```properties
# Verify MongoDB connection in application.properties
spring.data.mongodb.uri=mongodb://localhost:27017/ecommerce_db

# Test connection
mongo
show dbs
use ecommerce_db
db.payments.find()
```

**Check:**
- MongoDB is running
- Database name is correct
- Connection string is valid
- Network connectivity

---

### 10. Webhook Not Receiving Events

#### Error: Webhooks not triggering

**Solutions:**
1. **Verify webhook URL in Razorpay Dashboard**
   - Must be publicly accessible
   - Must use HTTPS (for production)
   - Must return 200 OK

2. **Check webhook signature verification**
```java
@PostMapping("/webhook")
public ResponseEntity<String> handleWebhook(
    @RequestBody String payload,
    @RequestHeader("X-Razorpay-Signature") String signature) {
    
    // Verify signature
    boolean isValid = verifyWebhookSignature(payload, signature);
    if (!isValid) {
        return ResponseEntity.status(400).body("Invalid signature");
    }
    
    // Process webhook
    return ResponseEntity.ok("Success");
}
```

3. **Test webhook locally using ngrok**
```bash
ngrok http 8080
# Use ngrok URL in Razorpay webhook configuration
```

---

## 🔧 Debugging Tools

### 1. Enable Debug Logging
```properties
# application.properties
logging.level.com.ecommerce.project=DEBUG
logging.level.com.razorpay=DEBUG
```

### 2. Check Razorpay Dashboard
- Go to https://dashboard.razorpay.com/
- Check Transactions → Payments
- View payment details and status
- Check webhook logs

### 3. MongoDB Queries
```javascript
// Check payment records
db.payments.find().pretty()

// Find by order ID
db.payments.find({orderId: "ORDER123"})

// Find by status
db.payments.find({paymentStatus: "pending"})

// Count payments
db.payments.count()
```

### 4. Test API with cURL
```bash
# Create order
curl -X POST http://localhost:8080/api/payments/razorpay/create-order \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{"orderId":"TEST","amount":100}'

# Get payment
curl -X GET http://localhost:8080/api/payments/order/TEST \
  -H "Authorization: Bearer TOKEN"
```

---

## 📊 Monitoring Checklist

- [ ] Check application logs regularly
- [ ] Monitor payment success rate
- [ ] Track failed payments
- [ ] Review Razorpay dashboard daily
- [ ] Set up alerts for failures
- [ ] Monitor response times
- [ ] Check database performance

---

## 🆘 Emergency Procedures

### If Payments Are Failing

1. **Immediate Actions**
   ```bash
   # Check application status
   curl http://localhost:8080/actuator/health
   
   # Check MongoDB
   mongo --eval "db.adminCommand('ping')"
   
   # Check logs
   tail -f logs/application.log
   ```

2. **Verify Razorpay Status**
   - Check https://status.razorpay.com/
   - Verify API keys are active
   - Check account status

3. **Rollback if Necessary**
   - Deploy previous working version
   - Notify users
   - Investigate issue

---

## 📞 Getting Help

### Razorpay Support
- Email: support@razorpay.com
- Dashboard: https://dashboard.razorpay.com/support
- Documentation: https://razorpay.com/docs/

### Check These Resources
1. Razorpay API Documentation
2. Razorpay Status Page
3. Application logs
4. MongoDB logs
5. Server logs

---

## 💡 Best Practices

1. **Always test in Test Mode first**
2. **Log all payment transactions**
3. **Implement proper error handling**
4. **Use webhooks for reliability**
5. **Monitor payment success rate**
6. **Keep SDK updated**
7. **Regular security audits**
8. **Backup payment data**

---

## 🔍 Quick Diagnostic Commands

```bash
# Check if application is running
curl http://localhost:8080/actuator/health

# Check MongoDB connection
mongo --eval "db.adminCommand('ping')"

# View recent logs
tail -n 100 logs/application.log

# Check Java process
jps -l | grep project

# Check port availability
netstat -an | grep 8080

# Test Razorpay connectivity
curl https://api.razorpay.com/v1/

# Check environment variables
echo $RAZORPAY_KEY_ID
```

---

## 📝 Logging Best Practices

```java
// Add comprehensive logging
@Slf4j
@Service
public class RazorpayServiceImpl implements RazorpayService {
    
    @Override
    public RazorpayOrderResponseDTO createRazorpayOrder(RazorpayOrderRequestDTO dto) {
        log.info("Creating Razorpay order for orderId: {}", dto.orderId());
        try {
            // ... order creation logic
            log.info("Razorpay order created successfully: {}", order.get("id"));
            return response;
        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay order: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create Razorpay order", e);
        }
    }
}
```

---

**Remember:** Most issues can be resolved by checking logs, verifying credentials, and ensuring proper configuration. Always test in Test Mode before deploying to production!
