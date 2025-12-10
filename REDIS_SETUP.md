# Redis Cloud Setup Guide

This application uses Redis Cloud for distributed caching to improve performance and scalability.

## Why Redis Cloud?

- **Distributed Caching**: Share cache across multiple application instances
- **Persistence**: Cache survives application restarts
- **Scalability**: Easy to scale as your application grows
- **Performance**: Sub-millisecond response times
- **Managed Service**: No infrastructure management needed

## Setup Instructions

### 1. Create a Redis Cloud Account

1. Go to [Redis Cloud](https://app.redislabs.com/)
2. Sign up for a free account (30MB free tier available)
3. Verify your email address

### 2. Create a Redis Database

1. Click **"New Database"** or **"Create Database"**
2. Choose your cloud provider (AWS, GCP, or Azure)
3. Select a region closest to your application
4. For the free tier:
   - Database Name: `ecommerce-cache`
   - Memory Limit: 30MB (free tier)
   - No replication (free tier)
5. Click **"Activate"**

### 3. Get Connection Details

After your database is created:

1. Go to your database dashboard
2. Find the **"Configuration"** or **"Connect"** section
3. Copy the following details:
   - **Endpoint**: `redis-xxxxx.x.xxxxxx.cloud.redislabs.com`
   - **Port**: Usually `12345` or similar
   - **Password**: Click "Show" to reveal

### 4. Configure Your Application

Update your `.env` file with the Redis connection details:

```env
# Redis Cloud Configuration
REDIS_HOST=redis-xxxxx.x.xxxxxx.cloud.redislabs.com
REDIS_PORT=12345
REDIS_PASSWORD=your_actual_redis_password_here
REDIS_SSL_ENABLED=true
```

**Important**: 
- Replace the values with your actual Redis Cloud credentials
- Keep `REDIS_SSL_ENABLED=true` for secure connections
- Never commit your `.env` file to version control

### 5. Test the Connection

Start your application:

```bash
# Using Maven
./mvnw spring-boot:run

# Or if already built
java -jar target/project-0.0.1-SNAPSHOT.jar
```

Check the logs for successful Redis connection:
```
INFO  c.e.p.config.RedisConfig - Redis connection established
```

## Cached Data

The following data is cached in Redis:

| Cache Name | Description | TTL |
|------------|-------------|-----|
| `product` | Individual product details | 2 hours |
| `productsAll` | All products list | 30 minutes |
| `productsPage` | Paginated product results | 1 hour |
| `productsByCategory` | Products filtered by category | 1 hour |
| `productsBySearch` | Product search results | 30 minutes |

## Cache Invalidation

Caches are automatically invalidated when:
- A product is created
- A product is updated
- A product is deleted

## Monitoring

### Redis Cloud Dashboard

Monitor your cache usage in the Redis Cloud dashboard:
- Memory usage
- Hit/miss ratio
- Operations per second
- Connected clients

### Application Logs

The application logs cache operations:
```
INFO  c.e.p.service.ProductServiceImpl - Fetching product with ID: 123
INFO  c.e.p.service.ProductServiceImpl - Cache hit for product: 123
```

## Troubleshooting

### Connection Timeout

If you see connection timeout errors:
1. Check your Redis Cloud firewall settings
2. Verify the host and port are correct
3. Ensure SSL is enabled if required
4. Check your network allows outbound connections to Redis Cloud

### Authentication Failed

If you see authentication errors:
1. Verify your Redis password is correct
2. Check for extra spaces in the `.env` file
3. Regenerate the password in Redis Cloud if needed

### Memory Limit Exceeded

If you hit the 30MB free tier limit:
1. Reduce cache TTL values in `CacheConfig.java`
2. Remove less important caches
3. Upgrade to a paid plan for more memory

## Local Development (Optional)

For local development without Redis Cloud, you can run Redis locally:

```bash
# Using Docker
docker run -d -p 6379:6379 redis:7-alpine

# Update .env for local Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_SSL_ENABLED=false
```

## Production Considerations

1. **High Availability**: Upgrade to a plan with replication
2. **Monitoring**: Set up alerts for memory usage and connection issues
3. **Backup**: Enable Redis persistence (RDB/AOF)
4. **Security**: Use strong passwords and enable SSL
5. **Scaling**: Monitor cache hit rates and adjust TTL values

## Cost Optimization

- Free tier: 30MB (suitable for small applications)
- Monitor your memory usage regularly
- Adjust cache TTL to balance performance vs memory
- Consider caching only frequently accessed data

## Additional Resources

- [Redis Cloud Documentation](https://docs.redis.com/latest/rc/)
- [Spring Data Redis Documentation](https://docs.spring.io/spring-data/redis/docs/current/reference/html/)
- [Redis Best Practices](https://redis.io/docs/manual/patterns/)
