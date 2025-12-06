# Cloudflare R2 Quick Start Guide

## ✅ Setup Complete!

Your Cloudflare R2 integration is ready to use with these credentials:

- **Account ID**: `2e1218a82c75fb5d888f6389845f18c9`
- **Bucket Name**: `paribito`
- **Endpoint**: `https://2e1218a82c75fb5d888f6389845f18c9.r2.cloudflarestorage.com`
- **Public URL**: `https://2e1218a82c75fb5d888f6389845f18c9.r2.cloudflarestorage.com/paribito`

## 🚀 Quick Test

### 1. Start Your Application

```bash
mvnw spring-boot:run
```

### 2. Test Image Upload (Postman/cURL)

**Upload Single Image:**
```bash
POST http://localhost:8080/api/images/upload
Content-Type: multipart/form-data

Body:
- file: [select an image file]
- folder: products
```

**Upload Multiple Images:**
```bash
POST http://localhost:8080/api/images/upload-multiple
Content-Type: multipart/form-data

Body:
- files: [select multiple image files]
- folder: products
```

### 3. Expected Response

```json
{
  "success": true,
  "message": "Image uploaded successfully",
  "imageUrl": "https://2e1218a82c75fb5d888f6389845f18c9.r2.cloudflarestorage.com/paribito/products/1733500000000_uuid.jpg"
}
```

## 📋 Available Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/images/upload` | Upload single image |
| POST | `/api/images/upload-multiple` | Upload multiple images |
| DELETE | `/api/images/delete?imageUrl=...` | Delete single image |
| DELETE | `/api/images/delete-multiple` | Delete multiple images |

## 🔧 Configuration Files

All set in:
- ✅ `.env` - R2 credentials
- ✅ `application.properties` - R2 configuration
- ✅ `R2Config.java` - S3 client setup
- ✅ `ImageStorageService.java` - Upload/delete logic
- ✅ `ImageUploadController.java` - API endpoints

## 📝 File Validation

- **Max size**: 10MB per file
- **Allowed types**: jpg, jpeg, png, gif, webp
- **Folder structure**: Optional (default: "products")

## 🎯 Next Steps

1. **Test upload** - Use Postman to upload a test image
2. **Verify in R2** - Check your Cloudflare R2 dashboard to see the uploaded file
3. **Integrate with products** - Use the returned URLs in your product creation
4. **Set up public access** (if needed) - Configure custom domain or public bucket access in Cloudflare

## 🔐 Public Access Setup (Optional)

If you want images to be publicly accessible:

1. Go to Cloudflare R2 dashboard
2. Select your `paribito` bucket
3. Go to **Settings** → **Public Access**
4. Enable **Allow Access**
5. Optionally set up a custom domain for cleaner URLs

## 💡 Usage Example

```javascript
// Frontend: Upload images first
const formData = new FormData();
formData.append('files', imageFile1);
formData.append('files', imageFile2);
formData.append('folder', 'products');

const uploadRes = await fetch('/api/images/upload-multiple', {
  method: 'POST',
  body: formData
});

const { imageUrls } = await uploadRes.json();

// Then create product with image URLs
const product = {
  name: "Product Name",
  price: 99.99,
  imageUrls: imageUrls,
  // ... other fields
};
```

## 🐛 Troubleshooting

**Issue: "Access Denied"**
- Verify API keys in `.env` are correct
- Check bucket name is `paribito`

**Issue: "Images not accessible"**
- Enable public access in R2 bucket settings
- Verify the public URL is correct

**Issue: "File too large"**
- Max file size is 10MB
- Compress images before uploading

## 📚 More Info

See `CLOUDFLARE_R2_SETUP_GUIDE.md` for detailed documentation.
