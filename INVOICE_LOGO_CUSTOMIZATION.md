# Invoice Logo Customization Guide

## Current Setup

The invoice template now includes your company logo at the top of every invoice PDF.

### Logo Location
```
src/main/resources/images/logo.png
```

### Logo Specifications
- **Format**: PNG (recommended for transparency)
- **Recommended Size**: 100x50 pixels (width x height)
- **Position**: Centered at the top of the invoice
- **Supported Formats**: PNG, JPG, JPEG, GIF

## How to Change the Logo

### Option 1: Replace Existing Logo
Simply replace the file at `src/main/resources/images/logo.png` with your new logo:

```bash
# Backup current logo (optional)
cp src/main/resources/images/logo.png src/main/resources/images/logo-backup.png

# Copy your new logo
cp /path/to/your/new-logo.png src/main/resources/images/logo.png

# Rebuild application
mvn clean package
```

### Option 2: Use Different Logo File
If you want to use a different filename:

1. **Add your logo file** to `src/main/resources/images/`
   ```bash
   cp /path/to/your-logo.png src/main/resources/images/company-logo.png
   ```

2. **Update InvoiceServiceImpl.java**
   ```java
   // Change this line:
   InputStream logoStream = new ClassPathResource("images/logo.png").getInputStream();
   
   // To:
   InputStream logoStream = new ClassPathResource("images/company-logo.png").getInputStream();
   ```

3. **Rebuild**
   ```bash
   mvn clean package
   ```

## Logo Design Recommendations

### Size & Dimensions
- **Width**: 80-120 pixels
- **Height**: 40-60 pixels
- **Aspect Ratio**: 2:1 (width:height) works best
- **File Size**: Keep under 100KB for faster PDF generation

### Format
- **PNG**: Best choice (supports transparency)
- **JPG**: Good for photos (no transparency)
- **GIF**: Acceptable but not recommended

### Design Tips
- Use transparent background (PNG) for professional look
- Ensure logo is clear and readable at small size
- Use high contrast colors
- Avoid very detailed logos (they may not render well)
- Test the invoice PDF to ensure logo looks good

## Adjusting Logo Size in Template

If you need to change the logo size in the invoice, edit `src/main/resources/invoice_template.jrxml`:

```xml
<!-- Current settings -->
<image hAlign="Center" vAlign="Middle">
    <reportElement x="227" y="0" width="100" height="50"/>
    <imageExpression><![CDATA[$P{logoPath}]]></imageExpression>
</image>
```

### Parameters Explained
- **x**: Horizontal position (227 centers a 100px wide logo on 555px page)
- **y**: Vertical position (0 = top of title band)
- **width**: Logo width in pixels (100)
- **height**: Logo height in pixels (50)

### Example: Larger Logo
```xml
<image hAlign="Center" vAlign="Middle">
    <reportElement x="202" y="0" width="150" height="75"/>
    <imageExpression><![CDATA[$P{logoPath}]]></imageExpression>
</image>
```

### Example: Left-Aligned Logo
```xml
<image hAlign="Left" vAlign="Middle">
    <reportElement x="0" y="0" width="100" height="50"/>
    <imageExpression><![CDATA[$P{logoPath}]]></imageExpression>
</image>
```

### Example: Right-Aligned Logo
```xml
<image hAlign="Right" vAlign="Middle">
    <reportElement x="455" y="0" width="100" height="50"/>
    <imageExpression><![CDATA[$P{logoPath}]]></imageExpression>
</image>
```

## Adjusting Title Band Height

If you change the logo size, you may need to adjust the title band height:

```xml
<!-- Current height -->
<title>
    <band height="180">
        <!-- Logo and other elements -->
    </band>
</title>

<!-- For larger logo, increase height -->
<title>
    <band height="220">
        <!-- Logo and other elements -->
    </band>
</title>
```

**Remember**: If you change the title band height, adjust the Y positions of elements below the logo accordingly.

## Testing Your Logo

After making changes:

1. **Rebuild the application**
   ```bash
   mvn clean package
   ```

2. **Generate a test invoice**
   ```bash
   # Mark an order as delivered
   curl -X PUT "http://localhost:8080/api/orders/{orderId}/status?status=delivered" \
     -H "Authorization: Bearer ADMIN_TOKEN"
   
   # Download the invoice
   curl -X GET "http://localhost:8080/api/invoices/{invoiceId}/download" \
     -H "Authorization: Bearer YOUR_TOKEN" \
     --output test-invoice.pdf
   ```

3. **Open and verify**
   ```bash
   # Open the PDF
   start test-invoice.pdf  # Windows
   open test-invoice.pdf   # Mac
   xdg-open test-invoice.pdf  # Linux
   ```

## Troubleshooting

### Logo Not Appearing
- Verify file exists: `src/main/resources/images/logo.png`
- Check file permissions (should be readable)
- Ensure file is not corrupted
- Check application logs for errors

### Logo Too Large/Small
- Adjust width/height in JRXML template
- Resize the actual image file
- Adjust title band height if needed

### Logo Position Wrong
- Adjust x/y coordinates in JRXML
- Use hAlign and vAlign attributes
- Consider page width (555px for A4)

### Logo Quality Poor
- Use higher resolution image
- Use PNG format for better quality
- Ensure original image is clear

### Build Errors After Changes
```bash
# Clean and rebuild
mvn clean compile

# Check for JRXML syntax errors
# Verify file paths are correct
```

## Multiple Logos (Advanced)

If you need different logos for different scenarios:

### 1. Add Multiple Logo Files
```
src/main/resources/images/
├── logo.png              # Default logo
├── logo-dark.png         # Dark theme logo
└── logo-international.png # International version
```

### 2. Update Service Logic
```java
// In InvoiceServiceImpl.java
private String getLogoPath(Order order) {
    // Logic to determine which logo to use
    if (order.isInternational()) {
        return "images/logo-international.png";
    }
    return "images/logo.png";
}

// Use in generateInvoicePdfInternal
InputStream logoStream = new ClassPathResource(getLogoPath(order)).getInputStream();
```

## Logo Branding Consistency

Ensure your invoice logo matches:
- Website logo
- Email signatures
- Marketing materials
- Business cards

This creates a consistent brand experience for your customers.

## Resources

- **JasperReports Image Documentation**: [JasperReports Docs](https://jasperreports.sourceforge.net/)
- **Image Optimization Tools**:
  - TinyPNG: https://tinypng.com/
  - ImageOptim: https://imageoptim.com/
  - Squoosh: https://squoosh.app/

## Support

For logo-related issues:
1. Check this guide
2. Review JRXML template
3. Test with different image formats
4. Contact development team if issues persist

---

**Current Logo**: `src/main/resources/images/logo.png`  
**Template**: `src/main/resources/invoice_template.jrxml`  
**Service**: `InvoiceServiceImpl.java`
