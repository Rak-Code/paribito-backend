# Keep-Alive Setup Guide

This setup prevents your server from sleeping on free-tier hosting using both internal scheduling and external GitHub Actions.

## What's Included

1. **Health Endpoint** (`/api/health`)
   - Simple endpoint that returns server status
   - No authentication required
   - Returns: `{ "status": "UP", "timestamp": "...", "message": "Server is running" }`

2. **Internal Keep-Alive Service** (`KeepAliveService`)
   - Runs automatically every 5 minutes within the application
   - Self-pings the health endpoint to maintain activity
   - Prevents 15-minute inactivity timeout on free-tier hosting
   - Configured via `app.url` and `app.health-check.endpoint` properties

3. **GitHub Actions Cron Job** (`.github/workflows/keep-alive.yml`) - Optional Backup
   - Runs automatically every 10 minutes as external backup
   - Pings your health endpoint from GitHub's servers
   - Additional layer of protection against timeouts

## Setup Steps

### 1. Internal Keep-Alive Service (Automatic)

The `KeepAliveService` is automatically enabled and requires no additional setup:
- Runs every 5 minutes automatically
- Uses the `APP_URL` environment variable (set in `.env`)
- Self-pings `/api/health` endpoint
- Logs success/failure for monitoring

**Configuration:**
```properties
# In application.properties
app.url=${APP_URL:http://localhost:8080}
app.health-check.endpoint=/api/health
```

```bash
# In .env file
APP_URL=https://your-deployed-app-url.com
```

### 2. GitHub Actions Backup (Optional)

For additional redundancy, you can also set up external GitHub Actions pinging:

### 2. GitHub Actions Backup (Optional)

For additional redundancy, you can also set up external GitHub Actions pinging:

1. Go to your GitHub repository
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add:
   - **Name**: `SERVER_URL`
   - **Value**: Your deployed server URL (e.g., `https://your-app.railway.app`)
   - Do NOT include trailing slash or `/api/health` path

### 3. Deploy Your Changes

Push these changes to your repository. The internal keep-alive service will start automatically when the application runs.

### 4. Test the Setup

**Test Health Endpoint:**
```bash
curl https://your-server-url.com/api/health
```

**Manually Trigger Workflow:**
1. Go to **Actions** tab in GitHub
2. Select **Keep Server Alive** workflow
3. Click **Run workflow**

## How It Works

**Internal Keep-Alive Service:**
- Spring's `@Scheduled` annotation runs `keepAliveHealthCheck()` every 5 minutes
- Uses `RestClient` to send GET request to its own `/api/health` endpoint
- Prevents free-tier timeout (typically 15 minutes) with more frequent pings
- Logs all attempts for monitoring and debugging

**External GitHub Actions (Optional):**
- GitHub Actions runs on a schedule: `*/10 * * * *` (every 10 minutes)
- It sends a GET request to your `/api/health` endpoint from external servers
- This activity provides backup protection against timeouts
- The workflow continues even if ping fails (won't break your Actions)

**Combined Protection:**
- Internal: Every 5 minutes from within the app
- External: Every 10 minutes from GitHub (if configured)
- Dual-layer protection ensures maximum uptime

## Monitoring

**Internal Service Monitoring:**
- Check application logs for keep-alive messages:
  - Success: `Keep-alive health check executed successfully: {response}`
  - Failure: `Keep-alive health check failed: {error}`

**External GitHub Actions Monitoring:**
Check workflow runs in the **Actions** tab to ensure external pings are successful (if configured).

## Alternative: External Cron Services

If you prefer not to use GitHub Actions, consider:
- **Cron-job.org** - Free cron job service
- **UptimeRobot** - Free uptime monitoring (pings every 5 minutes)
- **Render Cron Jobs** - If deployed on Render

## Notes

**Internal Keep-Alive Service:**
- No external dependencies or usage limits
- Runs as long as the application is running
- More reliable than external services
- Uses minimal resources (simple HTTP GET request)

**GitHub Actions (Optional):**
- GitHub Actions has usage limits on free tier (2,000 minutes/month)
- Each ping takes ~10 seconds = ~5 hours/month usage
- Well within free tier limits
- Provides external backup if internal service fails
