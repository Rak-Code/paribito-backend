# Async Logging Implementation Guide

## Overview
Async logging has been implemented using Logback with AsyncAppender to improve application performance by handling logging operations asynchronously.

## What Was Implemented

### 1. Logback Configuration (`logback-spring.xml`)
- **Console Appender**: Logs to console with formatted output
- **File Appender**: Rotates daily, max 10MB per file, keeps 30 days
- **Error File Appender**: Separate file for ERROR level logs
- **Async Appenders**: Wraps all appenders for async processing

### 2. Async Configuration Details

#### Queue Settings
- **Console Queue**: 512 events
- **File Queue**: 512 events  
- **Error Queue**: 256 events
- **Discarding Threshold**: 0 (no logs discarded)
- **Never Block**: false (ensures logs aren't lost)

#### Log Rotation
- Daily rotation with size-based splitting
- Max file size: 10MB
- History: 30 days
- Total cap: 1GB

### 3. Logging Aspect (`LoggingAspect.java`)
Automatically logs:
- All controller method executions
- All service method executions
- Execution time for each method
- Warnings for slow methods (>1 second)
- Exception details with stack traces

## How to Use

### In Your Code
Use Lombok's `@Slf4j` annotation:

```java
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MyService {
    
    public void myMethod() {
        log.debug("Debug message");
        log.info("Info message");
        log.warn("Warning message");
        log.error("Error message", exception);
    }
}
```

### Log Levels
- **DEBUG**: Detailed information for debugging
- **INFO**: General informational messages
- **WARN**: Warning messages for potential issues
- **ERROR**: Error messages with exceptions

## Log Files Location

All logs are stored in the `logs/` directory:
- `ecommerce-app.log` - All logs
- `ecommerce-app-error.log` - Error logs only
- Rotated files: `ecommerce-app-YYYY-MM-DD.N.log`

## Performance Benefits

1. **Non-blocking**: Logging doesn't block application threads
2. **Batching**: Logs are written in batches for efficiency
3. **Queue Management**: Configurable queue sizes prevent memory issues
4. **Separate Error Logs**: Quick access to errors without scanning all logs

## Spring Profiles

### Development Profile
```properties
spring.profiles.active=dev
```
- DEBUG level for application code
- Detailed logging enabled

### Production Profile
```properties
spring.profiles.active=prod
```
- INFO level for application code
- WARN level for Spring framework
- Reduced logging overhead

## Configuration Options

### Change Log Levels
Edit `logback-spring.xml`:
```xml
<logger name="com.ecommerce.project" level="INFO"/>
```

### Change Queue Size
```xml
<appender name="ASYNC_FILE" class="ch.qos.logback.classic.AsyncAppender">
    <queueSize>1024</queueSize>  <!-- Increase for high-volume logging -->
</appender>
```

### Change File Rotation
```xml
<timeBasedFileNamingAndTriggeringPolicy>
    <maxFileSize>50MB</maxFileSize>  <!-- Increase file size -->
</timeBasedFileNamingAndTriggeringPolicy>
<maxHistory>60</maxHistory>  <!-- Keep logs for 60 days -->
```

## Monitoring

### Check Log Files
```bash
# View latest logs
tail -f logs/ecommerce-app.log

# View error logs
tail -f logs/ecommerce-app-error.log

# Search for specific errors
grep "ERROR" logs/ecommerce-app.log
```

### Performance Metrics
The LoggingAspect automatically logs:
- Method execution times
- Slow method warnings (>1 second)
- Exception details

## Troubleshooting

### Logs Not Appearing
1. Check `logs/` directory exists (created automatically)
2. Verify `logback-spring.xml` is in `src/main/resources/`
3. Check file permissions

### High Memory Usage
1. Reduce queue sizes in async appenders
2. Decrease log retention period
3. Reduce log levels in production

### Missing Logs
1. Ensure `discardingThreshold=0` (no logs discarded)
2. Check `neverBlock=false` (waits for queue space)
3. Increase queue size if needed

## Best Practices

1. **Use appropriate log levels**
   - DEBUG: Development only
   - INFO: Important business events
   - WARN: Recoverable issues
   - ERROR: Exceptions and failures

2. **Avoid logging sensitive data**
   - No passwords, tokens, or PII
   - Sanitize user input in logs

3. **Use parameterized logging**
   ```java
   // Good - lazy evaluation
   log.debug("User {} logged in", username);
   
   // Bad - string concatenation always executes
   log.debug("User " + username + " logged in");
   ```

4. **Log exceptions properly**
   ```java
   log.error("Failed to process order", exception);
   ```

## Dependencies Added

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

Logback is included by default in `spring-boot-starter-web`.

## Testing

1. Start the application
2. Check console output for formatted logs
3. Verify `logs/` directory is created
4. Make API calls and check log files
5. Trigger errors and verify error log file

## Next Steps

1. Configure log aggregation (ELK, Splunk, etc.)
2. Set up log monitoring and alerts
3. Implement structured logging (JSON format)
4. Add correlation IDs for request tracing
