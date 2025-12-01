# Razorpay Integration - Deployment Checklist

## ✅ Pre-Deployment Checklist

### 1. Development Setup
- [ ] Razorpay test account created
- [ ] Test API keys obtained
- [ ] `application.properties` configured with test keys
- [ ] Application builds successfully (`mvn clean install`)
- [ ] Application runs without errors (`mvn spring-boot:run`)

### 2. Testing
- [ ] Create order endpoint tested
- [ ] Payment verification tested with test card
- [ ] Payment status updates correctly in database
- [ ] Refund functionality tested (if applicable)
- [ ] All endpoints return correct responses
- [ ] Error handling works properly

### 3. Frontend Integration
- [ ] Razorpay checkout script added to frontend
- [ ] Order creation flow implemented
- [ ] Payment verification flow implemented
- [ ] Success/failure handling implemented
- [ ] Loading states added
- [ ] Error messages displayed properly

### 4. Security Review
- [ ] Key Secret not exposed in frontend code
- [ ] All payment endpoints require authentication
- [ ] Signature verification implemented correctly
- [ ] Refund endpoint restricted to admin only
- [ ] HTTPS enabled (for production)
- [ ] CORS configured properly

### 5. Database
- [ ] Payment collection exists in MongoDB
- [ ] Indexes created on orderId and razorpayOrderId
- [ ] Payment records storing correctly
- [ ] Old payment data migrated (if applicable)

## 🚀 Production Deployment

### 1. Razorpay Production Setup
- [ ] Razorpay account verified (KYC completed)
- [ ] Live mode enabled in Razorpay dashboard
- [ ] Live API keys generated
- [ ] Webhook URL configured
- [ ] Webhook secret obtained
- [ ] Payment methods enabled (Cards, UPI, Netbanking, etc.)

### 2. Application Configuration
- [ ] Update `application.properties` with live keys:
  ```properties
  razorpay.key.id=rzp_live_YOUR_LIVE_KEY_ID
  razorpay.key.secret=YOUR_LIVE_KEY_SECRET
  razorpay.currency=INR
  ```
- [ ] Keys stored in environment variables (not in code)
- [ ] Configuration management setup (AWS Secrets Manager, etc.)

### 3. Server Setup
- [ ] SSL/TLS certificate installed
- [ ] HTTPS enabled and enforced
- [ ] Firewall configured
- [ ] Server timezone set correctly
- [ ] Logging configured
- [ ] Monitoring setup

### 4. Build & Deploy
```bash
# Build production artifact
mvn clean package -DskipTests

# Deploy to server
# (Your deployment process here)

# Verify deployment
curl https://yourdomain.com/api/payments/health
```

### 5. Post-Deployment Verification
- [ ] Application starts successfully
- [ ] Health check endpoint responds
- [ ] Create order endpoint works
- [ ] Test payment with live credentials
- [ ] Payment verification works
- [ ] Database records created correctly
- [ ] Logs are being generated
- [ ] Monitoring alerts configured

### 6. Webhook Setup (Recommended)
- [ ] Webhook endpoint implemented
- [ ] Webhook URL added in Razorpay dashboard
- [ ] Webhook signature verification implemented
- [ ] Webhook events handled:
  - [ ] payment.captured
  - [ ] payment.failed
  - [ ] refund.created
  - [ ] refund.processed
- [ ] Webhook tested with Razorpay test events

## 📊 Monitoring & Maintenance

### Daily Checks
- [ ] Monitor payment success rate
- [ ] Check for failed payments
- [ ] Review error logs
- [ ] Verify webhook deliveries

### Weekly Checks
- [ ] Reconcile payments with Razorpay dashboard
- [ ] Review refund requests
- [ ] Check payment trends
- [ ] Update documentation if needed

### Monthly Checks
- [ ] Review Razorpay fees and charges
- [ ] Analyze payment method preferences
- [ ] Update test cases
- [ ] Security audit

## 🔧 Configuration Files to Update

### application.properties (Production)
```properties
# Razorpay Production Configuration
razorpay.key.id=${RAZORPAY_KEY_ID}
razorpay.key.secret=${RAZORPAY_KEY_SECRET}
razorpay.currency=INR

# Webhook (if implemented)
razorpay.webhook.secret=${RAZORPAY_WEBHOOK_SECRET}
```

### Environment Variables
```bash
export RAZORPAY_KEY_ID=rzp_live_xxxxxxxxxxxxx
export RAZORPAY_KEY_SECRET=your_live_secret_key
export RAZORPAY_WEBHOOK_SECRET=your_webhook_secret
```

## 🆘 Rollback Plan

If issues occur after deployment:

1. **Immediate Actions**
   - [ ] Switch back to previous version
   - [ ] Notify users of payment issues
   - [ ] Disable payment gateway temporarily

2. **Investigation**
   - [ ] Check application logs
   - [ ] Review Razorpay dashboard
   - [ ] Verify configuration
   - [ ] Test in staging environment

3. **Resolution**
   - [ ] Fix identified issues
   - [ ] Test thoroughly
   - [ ] Redeploy with fixes
   - [ ] Monitor closely

## 📞 Support Contacts

- **Razorpay Support:** https://razorpay.com/support/
- **Razorpay Dashboard:** https://dashboard.razorpay.com/
- **Documentation:** https://razorpay.com/docs/

## 📝 Important Notes

1. **Never commit secrets to version control**
   - Use environment variables
   - Use secret management tools
   - Add `.env` to `.gitignore`

2. **Test thoroughly before going live**
   - Test all payment methods
   - Test failure scenarios
   - Test refund process

3. **Keep backups**
   - Database backups
   - Configuration backups
   - Code backups

4. **Monitor continuously**
   - Set up alerts for failures
   - Monitor payment success rate
   - Track response times

5. **Stay updated**
   - Check Razorpay SDK updates
   - Review security advisories
   - Update dependencies regularly

## ✅ Final Verification

Before marking deployment complete:

- [ ] All checklist items completed
- [ ] Test payment successful in production
- [ ] Monitoring and alerts working
- [ ] Documentation updated
- [ ] Team trained on new system
- [ ] Support team notified
- [ ] Rollback plan documented
- [ ] Success metrics defined

---

**Deployment Date:** _______________  
**Deployed By:** _______________  
**Verified By:** _______________  
**Status:** ⬜ Pending | ⬜ In Progress | ⬜ Complete

## 🎉 Post-Deployment

Once everything is verified:
1. Announce the new payment system to users
2. Monitor closely for first 24-48 hours
3. Gather user feedback
4. Document any issues and resolutions
5. Plan for future enhancements

---

**Good luck with your deployment! 🚀**
