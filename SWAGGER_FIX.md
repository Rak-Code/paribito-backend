# Swagger UI Fix for Railway Deployment

## Changes Made

1. **Updated SwaggerConfig.java** - Added Railway production server URL
2. **Updated application.properties** - Added Railway URL to CORS allowed origins
3. **Updated CorsConfig.java** - Added support for all Vercel app domains (*.vercel.app)

## Next Steps

### Deploy to Railway

After committing these changes, redeploy your application on Railway:

```bash
git add .
git commit -m "Fix Swagger UI for Railway deployment"
git push
```

### Access Swagger UI

Once deployed, access Swagger at:
https://paribito-backend-production.up.railway.app/swagger-ui/index.html

**Important:** In the Swagger UI, make sure the server dropdown at the top is set to:
- **Production server (Railway)** - when testing on Railway
- **Development server** - when testing locally

### Test the Chat Endpoint

1. Open Swagger UI on Railway
2. Select "Production server (Railway)" from the server dropdown
3. Authorize with your JWT token (click "Authorize" button)
4. Try the `/api/chat` endpoint

## What Was Wrong?

Swagger UI was hardcoded to use `localhost:8080`, which doesn't exist when deployed on Railway. The fix adds your Railway URL as the primary server, so Swagger knows where to send API requests.
