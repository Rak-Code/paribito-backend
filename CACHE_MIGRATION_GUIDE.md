# Cache Migration Guide: Caffeine to Redis Cloud

This document explains the migration from Caffeine (in-memory cache) to Redis Cloud (distributed cache).

## What Changed?

### Before (Caffeine)
- **Local in-memory cache** - Each application instance had its own cache
- **Lost on restart** - Cache cleared when application restarts
- **Single instance** - Not shared across multiple servers
- **Limited by JVM memory** - Cache size limited by heap space

### After (Redis Cloud)
- **Distributed cache** - Shared across all application instances
- **Persistent** - Cache survives application restarts
- **Scalable** - Can handle multiple application instances
- **Managed service** - No infrastructure to maintain

## Benefits

1. **Horizontal Scaling**: Deploy multiple instances without cache inconsistency
2. **Faster Restarts**: No need to warm up cache after deployment
3. **Better Performance**: Sub-millisecond response times from Redis
4. **Monitoring**: Built-in dashboard for cache metrics
5. **Cost-Effective**: Free tier available (30MB)

## Technical Changes

### Dependencies (pom.xml)

**Removed:**
```xml
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

**Added:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### Configuration

**New Files:**
- `src/main/java/com/ecommerce/project/config/RedisConfig.java` - Redis connection configuration
- Updated `src/main/java/com/ecommerce/project/config/CacheConfig.java` - Redis cache manager

**New Properties (application.properties):**
```properties
spring.data.redis.host=${REDIS_HOST:localhost}
spring.data.redis.port=${REDIS_PORT:6379}
spring.data.redis.password=${REDIS_PASSWORD:}
spring.data.redis.ssl.enabled=${REDIS_SSL_ENABLED:true}
spring.data.redis.timeout=60000
spring.data.redis.lettuce.pool.max-active=10
spring.data.redis.lettuce.pool.max-idle=5
spring.data.redis.lettuce.pool.min-idle=2
```

**New Environment Variables (.env):**
```env
REDIS_HOST=your-redis-cloud-host.cloud.redislabs.com
REDIS_PORT=12345
REDIS_PASSWORD=your_redis_password
REDIS_SSL_ENABLED=true
```

### Serialization

Redis requires proper serialization for Java objects. The new configuration includes:

1. **Jackson ObjectMapper** with:
   - JavaTimeModule for LocalDateTime support
   - Polymorphic type handling for complex objects
   - JSON serialization for all cached values

2. **String Serializer** for cache keys

3. **GenericJackson2JsonRedisSerializer** for cache values

### Cache TTL Changes

| Cache Name | Old TTL (Caffeine) | New TTL (Redis) |
|------------|-------------------|-----------------|
| product | 1 hour | 2 hours |
| productsAll | 1 hour | 30 minutes |
| productsPage | 1 hour | 1 hour |
| productsByCategory | 1 hour | 1 hour |
| productsBySearch | 1 hour | 30 minutes |

**Rationale:**
- Individual products change less frequently → longer TTL
- Product lists change more frequently → shorter TTL
- Search results may become stale quickly → shorter TTL

## Migration Steps

### 1. Set Up Redis Cloud

Follow the detailed instructions in [REDIS_SETUP.md](REDIS_SETUP.md)

### 2. Update Dependencies

```bash
./mvnw clean install
```

This will download the new Redis dependencies and remove Caffeine.

### 3. Configure Environment Variables

Update your `.env` file with Redis Cloud credentials:

```env
REDIS_HOST=your-actual-host.cloud.redislabs.com
REDIS_PORT=12345
REDIS_PASSWORD=your-actual-password
REDIS_SSL_ENABLED=true
```

### 4. Test Locally (Optional)

For local testing without Redis Cloud:

```bash
# Run Redis in Docker
docker run -d -p 6379:6379 redis:7-alpine

# Update .env for local Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_SSL_ENABLED=false
```

### 5. Deploy

Deploy your application with the new Redis configuration. The cache will automatically start using Redis.

### 6. Verify

Check the logs for successful Redis connection:
```
INFO  c.e.p.config.RedisConfig - Redis connection established
```

Monitor cache operations:
```
INFO  c.e.p.service.ProductServiceImpl - Fetching product with ID: 123
```

## Rollback Plan

If you need to rollback to Caffeine:

1. Revert the changes in `pom.xml`
2. Restore the old `CacheConfig.java` from git history
3. Remove `RedisConfig.java`
4. Remove Redis properties from `application.properties`
5. Rebuild and redeploy

```bash
git checkout HEAD~1 -- pom.xml
git checkout HEAD~1 -- src/main/java/com/ecommerce/project/config/CacheConfig.java
rm src/main/java/com/ecommerce/project/config/RedisConfig.java
./mvnw clean install
```

## Monitoring

### Redis Cloud Dashboard

Monitor your cache in the Redis Cloud dashboard:
- **Memory Usage**: Track how much of your 30MB is used
- **Hit Rate**: Percentage of cache hits vs misses
- **Operations/sec**: Number of cache operations
- **Latency**: Response time for cache operations

### Application Metrics

The application logs cache operations. Look for:
- Cache hits: Fast response times
- Cache misses: Slower response times (database query)
- Cache evictions: When data is updated/deleted

## Performance Comparison

### Before (Caffeine)
- Cache hit: ~1ms (in-memory)
- Cache miss: ~50-100ms (MongoDB query)
- Restart: All cache lost, slow first requests

### After (Redis Cloud)
- Cache hit: ~2-5ms (network + Redis)
- Cache miss: ~50-100ms (MongoDB query)
- Restart: Cache preserved, fast from first request

**Note**: Slightly slower cache hits due to network latency, but overall better performance due to cache persistence.

## Troubleshooting

### Issue: Connection Timeout

**Solution:**
1. Check Redis Cloud firewall settings
2. Verify host and port in `.env`
3. Ensure SSL is enabled if required

### Issue: Serialization Error

**Solution:**
1. Check that all cached objects are serializable
2. Verify Jackson dependencies are included
3. Check logs for specific serialization errors

### Issue: Memory Limit Exceeded

**Solution:**
1. Reduce cache TTL values
2. Remove less important caches
3. Upgrade to a paid Redis plan

### Issue: Slow Performance

**Solution:**
1. Check Redis Cloud region (should be close to your app)
2. Monitor network latency
3. Verify connection pool settings
4. Check Redis Cloud dashboard for issues

## Best Practices

1. **Monitor Memory Usage**: Keep an eye on Redis memory consumption
2. **Adjust TTL**: Fine-tune TTL values based on data change frequency
3. **Use Appropriate Keys**: Use descriptive cache keys for debugging
4. **Handle Failures**: Application should work even if Redis is down
5. **Security**: Always use SSL in production
6. **Backup**: Enable Redis persistence (RDB/AOF) in production

## Support

For issues or questions:
1. Check [REDIS_SETUP.md](REDIS_SETUP.md) for setup instructions
2. Review Redis Cloud documentation
3. Check application logs for errors
4. Monitor Redis Cloud dashboard

## Additional Resources

- [Redis Cloud Documentation](https://docs.redis.com/latest/rc/)
- [Spring Data Redis](https://docs.spring.io/spring-data/redis/docs/current/reference/html/)
- [Redis Best Practices](https://redis.io/docs/manual/patterns/)
