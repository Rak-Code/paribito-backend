# Cloudflare R2 Image Storage Setup Guide

## Overview
This guide will help you set up Cloudflare R2 storage for image uploads in your e-commerce application. Images will be uploaded to R2, and public URLs will be stored in MongoDB.

## Step 1: Create Cloudflare R2 Bucket

1. **Sign up/Login to Cloudflare**
   - Go to https://dash.cloudflare.com/
   - Navigate to R2 Storage in the sidebar

2. **Create a Bucket**
   - Click "Create bucket"
   - Name: `ecommerce-images` (or your preferred name)
   - Location: Choose closest to your users
   - Click "Create bucket"

3. **Configure Public Access**
   - Go to your bucket settings
   - Click "Settings" tab
   - Under "Public access", click "Allow Access"
   - Enable "Public URL access"
   - Copy the public URL (e.g., `https://pub-xxxxx.r2.dev`)

4. **Create API Token**
   - Go to R2 → Manage R2 API Tokens
   - Click "Create API Token"
   - Permissions: "Object Read & Write"
   - Select your bucket or "Apply to all buckets"
   - Click "Create API Token"
   - **IMPORTANT**: Copy these values immediately:
     - Access Key ID
     - Secret Access Key
     - Account ID (found in R2 overview page)

## Step 2: Configure Environment Variables

Update your `.env` file with the R2 credentials:

```properties
# Cloudflare R2 Configuration
R2_ACCOUNT_ID=your_account_id_here
R2_ACCESS_KEY_ID=your_access_key_here
R2_SECRET_ACCESS_KEY=your_secret_key_here
R2_BUCKET_NAME=ecommerce-images
R2_PUBLIC_URL=https://pub-xxxxx.r2.dev
```

## Step 3: Install Dependencies

The AWS SDK dependency has been added to `pom.xml`. Reload Maven:

```bash
mvn clean install
```

Or in your IDE: Right-click `pom.xml` → Maven → Reload Project

## Step 4: API Endpoints

### Upload Single Image
```http
POST /api/images/upload
Content-Type: multipart/form-data

Parameters:
- file: (file) The image file
- folder: (string, optional) Folder name (default: "products")

Response:
{
  "success": true,
  "message": "Image uploaded successfully",
  "imageUrl": "https://pub-xxxxx.r2.dev/products/1234567890_uuid.jpg"
}
```

### Upload Multiple Images
```http
POST /api/images/upload-multiple
Content-Type: multipart/form-data

Parameters:
- files: (file[]) Array of image files
- folder: (string, optional) Folder name (default: "products")

Response:
{
  "success": true,
  "message": "Images uploaded successfully",
  "imageUrls": [
    "https://pub-xxxxx.r2.dev/products/1234567890_uuid1.jpg",
    "https://pub-xxxxx.r2.dev/products/1234567891_uuid2.jpg"
  ],
  "count": 2
}
```

### Delete Image
```http
DELETE /api/images/delete?imageUrl=https://pub-xxxxx.r2.dev/products/image.jpg

Response:
{
  "success": true,
  "message": "Image deleted successfully"
}
```

### Delete Multiple Images
```http
DELETE /api/images/delete-multiple
Content-Type: application/json

Body:
[
  "https://pub-xxxxx.r2.dev/products/image1.jpg",
  "https://pub-xxxxx.r2.dev/products/image2.jpg"
]

Response:
{
  "success": true,
  "message": "All images deleted successfully"
}
```

## Step 5: Integration with Product Creation

When creating a product, first upload images, then use the returned URLs:

### Example Flow:

1. **Upload Images**
```javascript
const formData = new FormData();
formData.append('files', imageFile1);
formData.append('files', imageFile2);
formData.append('folder', 'products');

const uploadResponse = await fetch('/api/images/upload-multiple', {
  method: 'POST',
  body: formData
});

const { imageUrls } = await uploadResponse.json();
```

2. **Create Product with Image URLs**
```javascript
const productData = {
  name: "Product Name",
  description: "Description",
  price: 99.99,
  imageUrls: imageUrls, // URLs from step 1
  // ... other fields
};

await fetch('/api/products', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(productData)
});
```

## Step 6: File Validation

The service validates:
- **File size**: Maximum 10MB per file
- **File types**: jpg, jpeg, png, gif, webp
- **Empty files**: Rejected

## Step 7: Testing with Postman

1. **Upload Single Image**
   - Method: POST
   - URL: `http://localhost:8080/api/images/upload`
   - Body: form-data
     - Key: `file` (type: File)
     - Key: `folder` (type: Text, value: "products")

2. **Upload Multiple Images**
   - Method: POST
   - URL: `http://localhost:8080/api/images/upload-multiple`
   - Body: form-data
     - Key: `files` (type: File, select multiple files)
     - Key: `folder` (type: Text, value: "products")

## Step 8: Frontend Integration Example

### React/Next.js Example:

```jsx
const handleImageUpload = async (files) => {
  const formData = new FormData();
  
  files.forEach(file => {
    formData.append('files', file);
  });
  formData.append('folder', 'products');

  try {
    const response = await fetch('/api/images/upload-multiple', {
      method: 'POST',
      body: formData
    });
    
    const data = await response.json();
    
    if (data.success) {
      console.log('Uploaded URLs:', data.imageUrls);
      // Use these URLs in your product form
      setProductImages(data.imageUrls);
    }
  } catch (error) {
    console.error('Upload failed:', error);
  }
};
```

### HTML Form Example:

```html
<form id="uploadForm">
  <input type="file" name="files" multiple accept="image/*" />
  <button type="submit">Upload Images</button>
</form>

<script>
document.getElementById('uploadForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  
  const formData = new FormData(e.target);
  formData.append('folder', 'products');
  
  const response = await fetch('/api/images/upload-multiple', {
    method: 'POST',
    body: formData
  });
  
  const data = await response.json();
  console.log('Image URLs:', data.imageUrls);
});
</script>
```

## Step 9: Security Considerations

1. **CORS Configuration**: Add CORS settings if frontend is on different domain
2. **Authentication**: Add authentication to upload endpoints in production
3. **Rate Limiting**: Implement rate limiting to prevent abuse
4. **File Scanning**: Consider adding virus scanning for uploaded files

## Step 10: Cost Optimization

Cloudflare R2 Pricing:
- **Storage**: $0.015 per GB/month
- **Class A Operations** (writes): $4.50 per million
- **Class B Operations** (reads): $0.36 per million
- **Egress**: FREE (no bandwidth charges)

**Free Tier**: 10 GB storage, 1 million Class A operations, 10 million Class B operations per month

## Troubleshooting

### Issue: "Access Denied" Error
- Verify API token has correct permissions
- Check bucket name matches configuration
- Ensure public access is enabled on bucket

### Issue: "Invalid Credentials"
- Double-check R2_ACCESS_KEY_ID and R2_SECRET_ACCESS_KEY
- Verify R2_ACCOUNT_ID is correct
- Regenerate API token if needed

### Issue: Images Not Accessible
- Verify R2_PUBLIC_URL is correct
- Check bucket has public access enabled
- Ensure custom domain is configured if using one

### Issue: File Upload Fails
- Check file size (max 10MB)
- Verify file type is allowed
- Check application.properties multipart settings

## Next Steps

1. Configure your R2 credentials in `.env`
2. Restart your Spring Boot application
3. Test image upload using Postman
4. Integrate with your frontend
5. Update product creation flow to use image URLs

## Support

For issues with:
- **Cloudflare R2**: https://developers.cloudflare.com/r2/
- **AWS SDK**: https://docs.aws.amazon.com/sdk-for-java/
