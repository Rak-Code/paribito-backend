# Keep-Alive Setup Guide

This setup prevents your server from sleeping on free-tier hosting by pinging it every 10 minutes.

## What's Included

1. **Health Endpoint** (`/api/health`)
   - Simple endpoint that returns server status
   - No authentication required
   - Returns: `{ "status": "UP", "timestamp": "...", "message": "Server is running" }`

2. **GitHub Actions Cron Job** (`.github/workflows/keep-alive.yml`)
   - Runs automatically every 10 minutes
   - Pings your health endpoint to keep server active
   - Prevents 15-minute inactivity timeout

## Setup Steps

### 1. Add Server URL to GitHub Secrets

1. Go to your GitHub repository
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add:
   - **Name**: `SERVER_URL`
   - **Value**: Your deployed server URL (e.g., `https://your-app.railway.app`)
   - Do NOT include trailing slash or `/api/health` path

### 2. Deploy Your Changes

Push these changes to your repository. The workflow will start automatically.

### 3. Test the Setup

**Test Health Endpoint:**
```bash
curl https://your-server-url.com/api/health
```

**Manually Trigger Workflow:**
1. Go to **Actions** tab in GitHub
2. Select **Keep Server Alive** workflow
3. Click **Run workflow**

## How It Works

- GitHub Actions runs on a schedule: `*/10 * * * *` (every 10 minutes)
- It sends a GET request to your `/api/health` endpoint
- This activity prevents the free-tier timeout (typically 15 minutes)
- The workflow continues even if ping fails (won't break your Actions)

## Monitoring

Check workflow runs in the **Actions** tab to ensure pings are successful.

## Alternative: External Cron Services

If you prefer not to use GitHub Actions, consider:
- **Cron-job.org** - Free cron job service
- **UptimeRobot** - Free uptime monitoring (pings every 5 minutes)
- **Render Cron Jobs** - If deployed on Render

## Notes

- GitHub Actions has usage limits on free tier (2,000 minutes/month)
- Each ping takes ~10 seconds = ~5 hours/month usage
- Well within free tier limits
