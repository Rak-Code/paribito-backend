# Product Creation with Automatic Image Upload

## Overview
The product creation endpoint now automatically handles image uploads. When you create or update a product, simply include the image files in the request, and they'll be uploaded to Cloudflare R2 automatically. The URLs are saved to MongoDB without any extra steps.

## How It Works

### Create Product Flow:
```
1. Frontend: Select product images from device
   ↓
2. Frontend: Submit form with product data + images
   ↓
3. Backend: Upload images to Cloudflare R2
   ↓
4. Backend: Get public URLs from R2
   ↓
5. Backend: Save product with URLs to MongoDB
   ↓
6. Frontend: Receive product with image URLs
```

**Everything happens in ONE request!**

## API Endpoints

### 1. Create Product with Images

**Endpoint:** `POST /api/products`

**Content-Type:** `multipart/form-data`

**Authentication:** Required (Admin role)

**Parameters:**
- `name` (string, required) - Product name
- `description` (string, required) - Product description
- `categoryId` (string, required) - Category ID
- `price` (number, required) - Product price
- `stockQuantity` (number, required) - Stock quantity
- `size` (enum, optional) - Size: XS, S, M, L, XL, XXL, XXXL
- `color` (string, optional) - Product color
- `images` (file[], optional) - Image files (max 10MB each)

**Example with cURL:**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "name=Blue T-Shirt" \
  -F "description=Comfortable cotton t-shirt" \
  -F "categoryId=cat123" \
  -F "price=29.99" \
  -F "stockQuantity=100" \
  -F "size=M" \
  -F "color=Blue" \
  -F "images=@/path/to/image1.jpg" \
  -F "images=@/path/to/image2.jpg" \
  -F "images=@/path/to/image3.jpg"
```

**Response:**
```json
{
  "id": "prod123",
  "name": "Blue T-Shirt",
  "description": "Comfortable cotton t-shirt",
  "price": 29.99,
  "stockQuantity": 100,
  "categoryId": "cat123",
  "color": "Blue",
  "size": "M",
  "imageUrls": [
    "https://pub-xxxxx.r2.dev/products/1732464000000_abc123.jpg",
    "https://pub-xxxxx.r2.dev/products/1732464001000_def456.jpg",
    "https://pub-xxxxx.r2.dev/products/1732464002000_ghi789.jpg"
  ]
}
```

### 2. Update Product with Images

**Endpoint:** `PUT /api/products/{id}`

**Content-Type:** `multipart/form-data`

**Authentication:** Required (Admin role)

**Parameters:**
- All parameters from create endpoint
- `keepExistingImages` (boolean, optional, default: true) - Keep old images or replace them

**Behavior:**
- `keepExistingImages=true`: New images are added to existing ones
- `keepExistingImages=false`: Old images are deleted and replaced with new ones

**Example with cURL:**
```bash
# Add new images to existing ones
curl -X PUT http://localhost:8080/api/products/prod123 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "name=Blue T-Shirt Updated" \
  -F "description=Updated description" \
  -F "categoryId=cat123" \
  -F "price=34.99" \
  -F "stockQuantity=150" \
  -F "size=L" \
  -F "color=Blue" \
  -F "keepExistingImages=true" \
  -F "images=@/path/to/new-image.jpg"

# Replace all images
curl -X PUT http://localhost:8080/api/products/prod123 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "name=Blue T-Shirt Updated" \
  -F "description=Updated description" \
  -F "categoryId=cat123" \
  -F "price=34.99" \
  -F "stockQuantity=150" \
  -F "size=L" \
  -F "color=Blue" \
  -F "keepExistingImages=false" \
  -F "images=@/path/to/new-image1.jpg" \
  -F "images=@/path/to/new-image2.jpg"
```

### 3. Delete Product (Auto-deletes Images)

**Endpoint:** `DELETE /api/products/{id}`

**Authentication:** Required (Admin role)

When you delete a product, all associated images are automatically deleted from R2 storage.

```bash
curl -X DELETE http://localhost:8080/api/products/prod123 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Frontend Integration

### React/Next.js Example

```jsx
import { useState } from 'react';

function CreateProductForm() {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    categoryId: '',
    price: 0,
    stockQuantity: 0,
    size: 'M',
    color: ''
  });
  const [images, setImages] = useState([]);

  const handleImageChange = (e) => {
    setImages(Array.from(e.target.files));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const formDataToSend = new FormData();
    
    // Add product data
    formDataToSend.append('name', formData.name);
    formDataToSend.append('description', formData.description);
    formDataToSend.append('categoryId', formData.categoryId);
    formDataToSend.append('price', formData.price);
    formDataToSend.append('stockQuantity', formData.stockQuantity);
    formDataToSend.append('size', formData.size);
    formDataToSend.append('color', formData.color);
    
    // Add images
    images.forEach(image => {
      formDataToSend.append('images', image);
    });

    try {
      const response = await fetch('/api/products', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: formDataToSend
      });

      const product = await response.json();
      console.log('Product created:', product);
      console.log('Image URLs:', product.imageUrls);
      
      // Product is ready to use with images!
      
    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Product Name"
        value={formData.name}
        onChange={(e) => setFormData({...formData, name: e.target.value})}
      />
      
      <textarea
        placeholder="Description"
        value={formData.description}
        onChange={(e) => setFormData({...formData, description: e.target.value})}
      />
      
      <input
        type="number"
        placeholder="Price"
        value={formData.price}
        onChange={(e) => setFormData({...formData, price: parseFloat(e.target.value)})}
      />
      
      <input
        type="number"
        placeholder="Stock Quantity"
        value={formData.stockQuantity}
        onChange={(e) => setFormData({...formData, stockQuantity: parseInt(e.target.value)})}
      />
      
      <select
        value={formData.size}
        onChange={(e) => setFormData({...formData, size: e.target.value})}
      >
        <option value="XS">XS</option>
        <option value="S">S</option>
        <option value="M">M</option>
        <option value="L">L</option>
        <option value="XL">XL</option>
        <option value="XXL">XXL</option>
        <option value="XXXL">XXXL</option>
      </select>
      
      <input
        type="text"
        placeholder="Color"
        value={formData.color}
        onChange={(e) => setFormData({...formData, color: e.target.value})}
      />
      
      <input
        type="file"
        multiple
        accept="image/*"
        onChange={handleImageChange}
      />
      
      <button type="submit">Create Product</button>
    </form>
  );
}
```

### Display Product Images

```jsx
function ProductDisplay({ product }) {
  return (
    <div>
      <h2>{product.name}</h2>
      <p>{product.description}</p>
      <p>Price: ${product.price}</p>
      
      <div className="images">
        {product.imageUrls?.map((url, index) => (
          <img 
            key={index} 
            src={url} 
            alt={`${product.name} ${index + 1}`}
            style={{ width: '200px', height: '200px', objectFit: 'cover' }}
          />
        ))}
      </div>
    </div>
  );
}
```

### HTML Form Example

```html
<form id="productForm" enctype="multipart/form-data">
  <input type="text" name="name" placeholder="Product Name" required>
  <textarea name="description" placeholder="Description" required></textarea>
  <input type="text" name="categoryId" placeholder="Category ID" required>
  <input type="number" name="price" placeholder="Price" step="0.01" required>
  <input type="number" name="stockQuantity" placeholder="Stock" required>
  
  <select name="size">
    <option value="XS">XS</option>
    <option value="S">S</option>
    <option value="M">M</option>
    <option value="L">L</option>
    <option value="XL">XL</option>
  </select>
  
  <input type="text" name="color" placeholder="Color">
  <input type="file" name="images" multiple accept="image/*">
  
  <button type="submit">Create Product</button>
</form>

<script>
document.getElementById('productForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  
  const formData = new FormData(e.target);
  const token = localStorage.getItem('token');
  
  try {
    const response = await fetch('/api/products', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
      },
      body: formData
    });
    
    const product = await response.json();
    console.log('Product created:', product);
    alert('Product created successfully!');
    
  } catch (error) {
    console.error('Error:', error);
    alert('Failed to create product');
  }
});
</script>
```

## Postman Testing

### Create Product with Images

1. **Create New Request**
   - Method: `POST`
   - URL: `http://localhost:8080/api/products`

2. **Headers**
   - Key: `Authorization`
   - Value: `Bearer YOUR_JWT_TOKEN`

3. **Body** (form-data)
   - `name` (Text): "Blue T-Shirt"
   - `description` (Text): "Comfortable cotton t-shirt"
   - `categoryId` (Text): "your_category_id"
   - `price` (Text): "29.99"
   - `stockQuantity` (Text): "100"
   - `size` (Text): "M"
   - `color` (Text): "Blue"
   - `images` (File): Select multiple image files

4. **Send**

### Update Product with Images

1. **Create New Request**
   - Method: `PUT`
   - URL: `http://localhost:8080/api/products/{product_id}`

2. **Headers**
   - Key: `Authorization`
   - Value: `Bearer YOUR_JWT_TOKEN`

3. **Body** (form-data)
   - All fields from create
   - `keepExistingImages` (Text): "true" or "false"
   - `images` (File): Select new image files

4. **Send**

## File Validation

The system validates:
- **File size**: Max 10MB per image
- **File types**: jpg, jpeg, png, gif, webp only
- **Empty files**: Rejected

## Benefits

✅ **Single Request** - No need for separate upload calls
✅ **Automatic URLs** - Image URLs saved automatically
✅ **Clean Storage** - Images deleted when product is deleted
✅ **Flexible Updates** - Keep or replace images on update
✅ **Error Handling** - Comprehensive validation and error messages
✅ **Production Ready** - Includes logging and error recovery

## Important Notes

1. **Authentication Required**: Only admins can create/update products
2. **Images Optional**: You can create products without images
3. **Multiple Images**: Upload as many images as needed
4. **Automatic Cleanup**: Deleting a product deletes its images
5. **Update Flexibility**: Choose to keep or replace images on update

## Troubleshooting

### "Failed to upload product images"
- Check R2 credentials in `.env`
- Verify bucket has write permissions
- Check file size (max 10MB)
- Verify file type is allowed

### "Product created but no images"
- Check if images were included in request
- Verify `images` parameter name is correct
- Check file upload limits in application.properties

### Images not displaying
- Verify R2 bucket has public access enabled
- Check R2_PUBLIC_URL is correct
- Test image URL directly in browser

## Next Steps

1. Configure R2 credentials (see CLOUDFLARE_R2_SETUP_GUIDE.md)
2. Test product creation with Postman
3. Integrate with your frontend
4. Add image optimization (optional)
5. Set up CDN (optional)

---

**You're all set!** Create products with images in a single request. 🚀
