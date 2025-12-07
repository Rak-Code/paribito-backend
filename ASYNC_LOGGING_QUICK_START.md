# Async Logging Quick Start

## What's Been Done
✅ Logback async configuration created  
✅ AOP logging aspect for automatic method logging  
✅ Log rotation and error file separation  
✅ Dependencies added to pom.xml  

## Quick Test

### 1. Build the Project
```bash
mvn clean install
```

### 2. Run the Application
```bash
mvn spring-boot:run
```

### 3. Check Logs
You'll see:
- Console output with formatted logs
- `logs/ecommerce-app.log` - all logs
- `logs/ecommerce-app-error.log` - errors only

### 4. Use in Your Code

```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductService {
    
    public Product getProduct(String id) {
        log.info("Fetching product with id: {}", id);
        
        try {
            // Your logic here
            Product product = repository.findById(id);
            log.debug("Product found: {}", product.getName());
            return product;
        } catch (Exception e) {
            log.error("Failed to fetch product: {}", id, e);
            throw e;
        }
    }
}
```

## Key Features

### Automatic Logging
The `LoggingAspect` automatically logs:
- All controller methods
- All service methods  
- Execution times
- Exceptions

### Example Output
```
2024-12-07 10:30:15.123 [http-nio-8080-exec-1] DEBUG c.e.p.config.LoggingAspect - Entering controller method: ProductController.getProduct(..)
2024-12-07 10:30:15.125 [http-nio-8080-exec-1] INFO  c.e.p.service.ProductService - Fetching product with id: 123
2024-12-07 10:30:15.145 [http-nio-8080-exec-1] DEBUG c.e.p.config.LoggingAspect - Completed controller method: ProductController.getProduct(..) in 22ms
```

## Performance Benefits
- **Non-blocking**: Logging happens asynchronously
- **Queue-based**: 512 event queue prevents blocking
- **Batched writes**: More efficient I/O operations
- **No log loss**: `discardingThreshold=0` ensures all logs are written

## Log Levels by Environment

### Development
```properties
spring.profiles.active=dev
```
- DEBUG level enabled
- Detailed logging

### Production  
```properties
spring.profiles.active=prod
```
- INFO level only
- Reduced overhead

## Common Log Patterns

```java
// Info - business events
log.info("Order {} created for user {}", orderId, userId);

// Debug - detailed flow
log.debug("Processing payment for amount: {}", amount);

// Warn - potential issues
log.warn("Low stock for product {}: {} remaining", productId, stock);

// Error - exceptions
log.error("Payment failed for order {}", orderId, exception);
```

## Monitoring Logs

```bash
# Follow logs in real-time
tail -f logs/ecommerce-app.log

# View only errors
tail -f logs/ecommerce-app-error.log

# Search for specific text
grep "ERROR" logs/ecommerce-app.log

# Count errors today
grep "ERROR" logs/ecommerce-app-$(date +%Y-%m-%d).log | wc -l
```

## That's It!
Your application now has production-ready async logging with automatic method tracking and performance monitoring.
