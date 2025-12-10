# Redis Cache Quick Reference

## Environment Variables

```env
REDIS_HOST=your-host.cloud.redislabs.com
REDIS_PORT=12345
REDIS_PASSWORD=your-password
REDIS_SSL_ENABLED=true
```

## Cache Names & TTL

| Cache | TTL | Usage |
|-------|-----|-------|
| `product` | 2h | Single product by ID |
| `productsAll` | 30m | All products list |
| `productsPage` | 1h | Paginated products |
| `productsByCategory` | 1h | Products by category |
| `productsBySearch` | 30m | Search results |

## Cache Keys

```
product::{productId}
productsAll::SimpleKey []
productsPage::{pageNumber-pageSize-sort}
productsByCategory::{categoryId}
productsBySearch::{keyword}
```

## Common Commands

### Check Redis Connection
```bash
# Using redis-cli (if installed)
redis-cli -h your-host.cloud.redislabs.com -p 12345 -a your-password --tls ping

# Expected output: PONG
```

### View All Cache Keys
```bash
redis-cli -h your-host.cloud.redislabs.com -p 12345 -a your-password --tls keys "*"
```

### Clear Specific Cache
```bash
# Clear all product caches
redis-cli -h your-host.cloud.redislabs.com -p 12345 -a your-password --tls del "product::*"

# Clear all caches
redis-cli -h your-host.cloud.redislabs.com -p 12345 -a your-password --tls flushdb
```

### Check Cache Size
```bash
redis-cli -h your-host.cloud.redislabs.com -p 12345 -a your-password --tls dbsize
```

### Monitor Cache Operations
```bash
redis-cli -h your-host.cloud.redislabs.com -p 12345 -a your-password --tls monitor
```

## Testing Locally

### Run Redis with Docker
```bash
docker run -d --name redis-local -p 6379:6379 redis:7-alpine
```

### Local Configuration
```env
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_SSL_ENABLED=false
```

### Stop Local Redis
```bash
docker stop redis-local
docker rm redis-local
```

## Programmatic Cache Management

### Clear Cache in Code
```java
@Autowired
private CacheManager cacheManager;

// Clear specific cache
cacheManager.getCache("product").clear();

// Clear all caches
cacheManager.getCacheNames().forEach(cacheName -> 
    cacheManager.getCache(cacheName).clear()
);
```

### Manual Cache Operations
```java
@Autowired
private RedisTemplate<String, Object> redisTemplate;

// Get value
Object value = redisTemplate.opsForValue().get("product::123");

// Set value with TTL
redisTemplate.opsForValue().set("key", value, Duration.ofHours(1));

// Delete key
redisTemplate.delete("product::123");

// Check if key exists
Boolean exists = redisTemplate.hasKey("product::123");
```

## Monitoring

### Application Logs
```
INFO  c.e.p.service.ProductServiceImpl - Fetching product with ID: 123
INFO  c.e.p.service.ProductServiceImpl - Cache hit for product: 123
```

### Redis Cloud Dashboard
- Memory usage: Monitor your 30MB limit
- Hit rate: Should be > 80% for good performance
- Operations/sec: Track cache load
- Latency: Should be < 5ms

## Troubleshooting

### Connection Issues
```bash
# Test connection
telnet your-host.cloud.redislabs.com 12345

# Check SSL
openssl s_client -connect your-host.cloud.redislabs.com:12345
```

### Clear Application Cache
```bash
# Restart application
./mvnw spring-boot:run

# Or clear Redis manually
redis-cli -h your-host --tls flushdb
```

### Check Memory Usage
```bash
redis-cli -h your-host --tls info memory
```

## Performance Tips

1. **Warm up cache**: Hit frequently accessed endpoints after deployment
2. **Monitor hit rate**: Aim for > 80% cache hit rate
3. **Adjust TTL**: Longer TTL for stable data, shorter for dynamic data
4. **Use pagination**: Cache paginated results instead of full lists
5. **Evict on updates**: Always evict cache when data changes

## Security Checklist

- ✅ Use SSL/TLS in production (`REDIS_SSL_ENABLED=true`)
- ✅ Use strong passwords (> 20 characters)
- ✅ Never commit `.env` file
- ✅ Rotate passwords regularly
- ✅ Use environment variables, not hardcoded values
- ✅ Enable Redis Cloud firewall rules
- ✅ Monitor for unusual access patterns

## Cost Management

- **Free Tier**: 30MB (suitable for ~10,000 products)
- **Monitor**: Check memory usage weekly
- **Optimize**: Reduce TTL if approaching limit
- **Upgrade**: Consider paid plan if consistently > 80% usage

## Quick Links

- [Redis Cloud Dashboard](https://app.redislabs.com/)
- [Full Setup Guide](REDIS_SETUP.md)
- [Migration Guide](CACHE_MIGRATION_GUIDE.md)
- [Redis Commands](https://redis.io/commands/)
