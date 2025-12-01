# Quick Test Guide for R2 Image Upload

## Prerequisites
1. Configure `.env` with your R2 credentials
2. Start your Spring Boot application
3. Have test images ready

## Test with cURL

### 1. Upload Single Image
```bash
curl -X POST http://localhost:8080/api/images/upload \
  -F "file=@/path/to/your/image.jpg" \
  -F "folder=products"
```

### 2. Upload Multiple Images
```bash
curl -X POST http://localhost:8080/api/images/upload-multiple \
  -F "files=@/path/to/image1.jpg" \
  -F "files=@/path/to/image2.jpg" \
  -F "folder=products"
```

### 3. Delete Image
```bash
curl -X DELETE "http://localhost:8080/api/images/delete?imageUrl=https://your-bucket.r2.dev/products/image.jpg"
```

## Test with Postman

### Upload Single Image
1. Create new request: `POST http://localhost:8080/api/images/upload`
2. Go to Body → form-data
3. Add key `file` (change type to File) → Select image
4. Add key `folder` (Text) → Value: `products`
5. Send

### Upload Multiple Images
1. Create new request: `POST http://localhost:8080/api/images/upload-multiple`
2. Go to Body → form-data
3. Add key `files` (change type to File) → Select multiple images
4. Add key `folder` (Text) → Value: `products`
5. Send

## Expected Response

### Success Response:
```json
{
  "success": true,
  "message": "Images uploaded successfully",
  "imageUrls": [
    "https://pub-xxxxx.r2.dev/products/1732464000000_abc123.jpg",
    "https://pub-xxxxx.r2.dev/products/1732464001000_def456.jpg"
  ],
  "count": 2
}
```

### Error Response:
```json
{
  "success": false,
  "message": "File size exceeds maximum limit of 10MB"
}
```

## Integration with Product Creation

### Step 1: Upload Images
```bash
curl -X POST http://localhost:8080/api/images/upload-multiple \
  -F "files=@product1.jpg" \
  -F "files=@product2.jpg" \
  -F "folder=products"
```

Response:
```json
{
  "imageUrls": [
    "https://pub-xxxxx.r2.dev/products/img1.jpg",
    "https://pub-xxxxx.r2.dev/products/img2.jpg"
  ]
}
```

### Step 2: Create Product with URLs
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Test Product",
    "description": "Product with R2 images",
    "price": 99.99,
    "stockQuantity": 10,
    "categoryId": "category_id_here",
    "imageUrls": [
      "https://pub-xxxxx.r2.dev/products/img1.jpg",
      "https://pub-xxxxx.r2.dev/products/img2.jpg"
    ],
    "size": "M",
    "color": "Blue"
  }'
```

## Verify in MongoDB

```javascript
// Connect to MongoDB
use ecommerce_db

// Find product with images
db.products.findOne({ name: "Test Product" })

// Should see:
{
  "_id": "...",
  "name": "Test Product",
  "imageUrls": [
    "https://pub-xxxxx.r2.dev/products/img1.jpg",
    "https://pub-xxxxx.r2.dev/products/img2.jpg"
  ],
  ...
}
```

## Verify in Browser

Simply paste the image URL in browser:
```
https://pub-xxxxx.r2.dev/products/1732464000000_abc123.jpg
```

You should see the image displayed.

## Common Issues

### 1. "Access Denied"
- Check R2 credentials in `.env`
- Verify bucket has public access enabled

### 2. "File too large"
- Max size is 10MB per file
- Compress images before uploading

### 3. "Invalid file type"
- Only jpg, jpeg, png, gif, webp allowed
- Check file extension

### 4. Images not loading in frontend
- Verify CORS is configured on R2 bucket
- Check public URL is correct
- Ensure bucket has public access enabled
