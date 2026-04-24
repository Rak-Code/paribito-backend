# Swagger Deployment & Configuration Checklist

## Pre-Deployment Checklist

### Development Environment
- [ ] OpenApiConfig.java created in `src/main/java/com/ecommerce/project/config/`
- [ ] Swagger UI accessible at `http://localhost:8080/swagger-ui.html`
- [ ] All controllers properly annotated with `@RestController`
- [ ] API endpoints documented with `@Operation` annotations
- [ ] Request/Response DTOs properly annotated with `@Schema`
- [ ] Authentication endpoints return proper schema models
- [ ] Test all API endpoints in Swagger UI

### Dependencies Verification
```xml
<!-- Verify in pom.xml -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.0</version>
</dependency>
```

### Configuration File Updates
- [ ] `application.properties` contains Swagger configuration section
- [ ] `springdoc.swagger-ui.enabled=true` (or set via env variable)
- [ ] `springdoc.api-docs.path=/v3/api-docs` configured
- [ ] `springdoc.swagger-ui.path=/swagger-ui.html` configured
- [ ] Server URLs configured in OpenApiConfig bean

## Production Deployment Checklist

### Environment-Specific Configuration

#### Render Deployment
- [ ] Application deployed to: `https://paribito-backend.onrender.com`
- [ ] Swagger accessible at: `https://paribito-backend.onrender.com/swagger-ui.html`
- [ ] Production server added to OpenAPI server list
- [ ] Environment variable: `APP_URL=https://paribito-backend.onrender.com`

#### Railway Deployment (Backup)
- [ ] Application deployed to: `https://paribito-backend-production.up.railway.app`
- [ ] Swagger accessible at: `https://paribito-backend-production.up.railway.app/swagger-ui.html`
- [ ] Backup server added to OpenAPI server list
- [ ] Environment variable for Railway configured

### Security Configuration for Production

```properties
# In production environment or production-specific properties file:

# Enable/Disable Swagger UI (optional - recommended enabled for API access)
springdoc.swagger-ui.enabled=true

# Hide operational details (optional)
springdoc.swagger-ui.display-operation-id=false

# Set documentation expansion (optional)
springdoc.swagger-ui.doc-expansion=list

# Hide models not in use (optional)
springdoc.swagger-ui.default-model-expand-depth=2
```

### CORS Configuration

- [ ] Add production URLs to CORS allowed-origins in `application.properties`
- [ ] Production Origin 1: `https://paribito-backend.onrender.com`
- [ ] Production Origin 2: `https://paribito-backend-production.up.railway.app`
- [ ] Frontend URLs included: `https://theparibito.com`, `https://paribito20.vercel.app`

Current CORS Configuration:
```properties
cors.allowed-origins=http://localhost:5173,http://localhost:3000,http://localhost:8081,https://paribito-backend-production.up.railway.app,https://paribito20.vercel.app,https://theparibito.com
```

### API Documentation Review

- [ ] API title and description are clear
- [ ] Contact information is accurate: `aditaenterpriseindia@gmail.com`
- [ ] License information is correct: `Proprietary License`
- [ ] All endpoints are documented
- [ ] All data models have descriptions
- [ ] Authentication requirements are clear

## Post-Deployment Validation

### Swagger UI Accessibility
```bash
# Test Development
curl -o /dev/null -s -w "%{http_code}" http://localhost:8080/swagger-ui.html

# Test Production (Render)
curl -o /dev/null -s -w "%{http_code}" https://paribito-backend.onrender.com/swagger-ui.html

# Test Production (Railway)
curl -o /dev/null -s -w "%{http_code}" https://paribito-backend-production.up.railway.app/swagger-ui.html
```

Expected Response: **200**

### API Schema Validation
```bash
# Verify OpenAPI schema is accessible
curl https://paribito-backend.onrender.com/v3/api-docs | jq '.info.title'

# Should output: "Paribito E-Commerce API"
```

### Test Endpoints in Swagger
1. [ ] Open Swagger UI
2. [ ] Click environment dropdown
3. [ ] Select "Production Server"
4. [ ] Verify all endpoints are listed
5. [ ] Test public endpoints (no auth)
6. [ ] Click "Authorize" and add a valid JWT token
7. [ ] Test protected endpoints
8. [ ] Verify responses match schema

## Maintenance Tasks

### Regular Checks
- [ ] Monitor Swagger UI response times
- [ ] Check for any 5xx errors in logs
- [ ] Verify CORS headers are correct
- [ ] Test authentication flow monthly
- [ ] Update API documentation when endpoints change

### Version Updates
- [ ] Monitor springdoc-openapi updates
- [ ] Test compatibility with new versions
- [ ] Update dependency version in pom.xml
- [ ] Run regression tests after updates

### Documentation Updates
- [ ] Update OpenApiConfig.java when endpoints change
- [ ] Update API descriptions for clarity
- [ ] Add new server URLs if deployment URLs change
- [ ] Update contact and license information as needed

## Common Issues & Solutions

### Issue: Swagger UI Shows 404
**Solution:**
1. Verify application is running
2. Check `springdoc.swagger-ui.enabled=true`
3. Restart application
4. Clear browser cache

### Issue: Endpoints Not Showing in Swagger
**Solution:**
1. Verify `@RestController` annotation on class
2. Verify `@RequestMapping` or `@GetMapping`, etc. annotations present
3. Rebuild project: `mvn clean package`
4. Restart application
5. Clear Swagger cache: `Ctrl+Shift+Del` in browser

### Issue: Can't Test Endpoints (401 Unauthorized)
**Solution:**
1. Click "Authorize" button
2. Get JWT token from login endpoint
3. Paste in format: `Bearer <TOKEN>`
4. Click "Authorize"
5. Try endpoint again

### Issue: CORS Error When Testing
**Solution:**
1. Verify your frontend URL is in `cors.allowed-origins`
2. Ensure `CorsConfig.java` is properly configured
3. Check headers in browser Network tab
4. Add origin to allowed-origins if missing

## Rollback Procedure

If issues occur with Swagger deployment:

1. **Disable Swagger UI** (if causing issues):
   ```properties
   springdoc.swagger-ui.enabled=false
   ```

2. **Revert OpenApiConfig.java**:
   - Delete or rename `OpenApiConfig.java`
   - Rebuild and redeploy

3. **Verify Application Still Works**:
   - Test API endpoints directly via cURL
   - Check logs for errors
   - Restore from previous deployment if needed

## Performance Optimization

### For Large APIs (Many Endpoints)
```properties
# Add selective scanning
springdoc.paths-to-match=/api/**

# Exclude certain paths if needed
springdoc.paths-to-exclude=/actuator/**,/debug/**
```

### For Improved Load Times
```properties
# Lazy load OpenAPI schema
springdoc.swagger-ui.url=/v3/api-docs

# Cache settings
springdoc.cache.disabled=false
```

## Security Best Practices

1. **Production Considerations**:
   - Consider disabling Swagger UI in true production (set `enabled=false`)
   - Implement authentication for Swagger UI access
   - Use separate profiles for dev/prod documentation

2. **API Key Management**:
   - Don't expose secrets in API documentation
   - Use environment variables for sensitive data
   - Rotate API keys regularly

3. **CORS Security**:
   - Only whitelist necessary origins
   - Use HTTPS for production
   - Implement rate limiting on API endpoints

## Documentation Maintenance

### Update when:
- Adding new API endpoints
- Modifying request/response schemas
- Changing authentication flow
- Updating deployment URLs
- Changing error responses

### How to update:
1. Modify `OpenApiConfig.java` for servers/info
2. Add `@Operation` annotations to controller methods
3. Update DTO classes with `@Schema` annotations
4. Run `mvn clean package`
5. Deploy and verify

---

**Document Version**: 1.0  
**Last Updated**: April 2026  
**Maintenance Owner**: Development Team  
**Review Schedule**: Monthly
