# Frontend Quick Reference Card

## 🚀 Quick Start

### Create Product with Images (React)

```jsx
const handleSubmit = async (e) => {
  e.preventDefault();
  
  const formData = new FormData();
  formData.append('name', productName);
  formData.append('description', description);
  formData.append('categoryId', categoryId);
  formData.append('price', price);
  formData.append('stockQuantity', stock);
  formData.append('size', size);
  formData.append('color', color);
  
  // Add images
  images.forEach(img => formData.append('images', img));
  
  const token = localStorage.getItem('token');
  const response = await fetch('http://localhost:8080/api/products', {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token}` },
    body: formData
  });
  
  const product = await response.json();
  console.log('Created:', product.imageUrls);
};
```

### Create Product (Vanilla JS)

```javascript
document.getElementById('form').addEventListener('submit', async (e) => {
  e.preventDefault();
  
  const formData = new FormData(e.target);
  const token = localStorage.getItem('token');
  
  const response = await fetch('http://localhost:8080/api/products', {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token}` },
    body: formData
  });
  
  const product = await response.json();
  alert('Product created!');
});
```

---

## 📋 API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/products` | Admin | Create product with images |
| PUT | `/api/products/{id}` | Admin | Update product with images |
| DELETE | `/api/products/{id}` | Admin | Delete product (auto-deletes images) |
| GET | `/api/products` | Public | Get all products |
| GET | `/api/products/{id}` | Public | Get single product |

---

## 📝 Form Fields

### Required Fields
```javascript
{
  name: string,           // Product name
  description: string,    // Description
  categoryId: string,     // Category ID
  price: number,          // Price (e.g., 29.99)
  stockQuantity: number   // Stock count
}
```

### Optional Fields
```javascript
{
  size: enum,            // XS, S, M, L, XL, XXL, XXXL
  color: string,         // Color name
  images: File[]         // Image files (max 10MB each)
}
```

---

## 🖼️ Image Handling

### File Input
```html
<input 
  type="file" 
  name="images" 
  multiple 
  accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
/>
```

### Validation
```javascript
const MAX_SIZE = 10 * 1024 * 1024; // 10MB
const ALLOWED_TYPES = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];

function validateImage(file) {
  if (file.size > MAX_SIZE) return false;
  if (!ALLOWED_TYPES.includes(file.type)) return false;
  return true;
}
```

### Preview
```javascript
// React
const [previews, setPreviews] = useState([]);

const handleImageChange = (e) => {
  const files = Array.from(e.target.files);
  const urls = files.map(f => URL.createObjectURL(f));
  setPreviews(urls);
};

// Vanilla JS
const reader = new FileReader();
reader.onload = (e) => {
  img.src = e.target.result;
};
reader.readAsDataURL(file);
```

---

## 🎨 Display Product Images

### Single Image
```jsx
<img 
  src={product.imageUrls?.[0]} 
  alt={product.name}
  className="w-full h-64 object-cover"
/>
```

### Image Gallery
```jsx
{product.imageUrls?.map((url, i) => (
  <img key={i} src={url} alt={`${product.name} ${i+1}`} />
))}
```

### With Fallback
```jsx
{product.imageUrls && product.imageUrls.length > 0 ? (
  <img src={product.imageUrls[0]} alt={product.name} />
) : (
  <div className="placeholder">No image</div>
)}
```

---

## ⚠️ Error Handling

### Basic
```javascript
try {
  const response = await fetch(url, options);
  if (!response.ok) throw new Error('Failed');
  const data = await response.json();
} catch (error) {
  alert(error.message);
}
```

### With Status Codes
```javascript
if (response.status === 401) {
  // Redirect to login
  window.location.href = '/login';
}
if (response.status === 403) {
  alert('You need admin access');
}
if (response.status === 400) {
  const error = await response.json();
  alert(error.message);
}
```

---

## 🔐 Authentication

### Store Token (After Login)
```javascript
localStorage.setItem('token', jwtToken);
```

### Use Token
```javascript
const token = localStorage.getItem('token');

fetch(url, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
```

### Check Token
```javascript
const token = localStorage.getItem('token');
if (!token) {
  window.location.href = '/login';
}
```

---

## 📦 Complete Form Example (Copy-Paste Ready)

### HTML
```html
<form id="productForm" enctype="multipart/form-data">
  <input type="text" name="name" placeholder="Product Name" required>
  <textarea name="description" placeholder="Description" required></textarea>
  <input type="text" name="categoryId" placeholder="Category ID" required>
  <input type="number" name="price" step="0.01" placeholder="Price" required>
  <input type="number" name="stockQuantity" placeholder="Stock" required>
  <select name="size">
    <option value="M">M</option>
    <option value="L">L</option>
  </select>
  <input type="text" name="color" placeholder="Color">
  <input type="file" name="images" multiple accept="image/*">
  <button type="submit">Create Product</button>
</form>
```

### JavaScript
```javascript
document.getElementById('productForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  
  const formData = new FormData(e.target);
  const token = localStorage.getItem('token');
  
  try {
    const response = await fetch('http://localhost:8080/api/products', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` },
      body: formData
    });
    
    if (!response.ok) throw new Error('Failed to create product');
    
    const product = await response.json();
    alert('Product created successfully!');
    console.log('Image URLs:', product.imageUrls);
    e.target.reset();
    
  } catch (error) {
    alert('Error: ' + error.message);
  }
});
```

---

## 🎯 Common Mistakes to Avoid

### ❌ Wrong
```javascript
// Don't use JSON for file uploads
fetch(url, {
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ name: 'Product', images: files })
});

// Don't set Content-Type manually
fetch(url, {
  headers: { 'Content-Type': 'multipart/form-data' },
  body: formData
});

// Don't upload images separately
await uploadImages();
await createProduct();
```

### ✅ Correct
```javascript
// Use FormData
const formData = new FormData();
formData.append('name', 'Product');
formData.append('images', file);

// Let browser set Content-Type
fetch(url, {
  headers: { 'Authorization': `Bearer ${token}` },
  body: formData
});

// Upload everything together
await createProduct(formData);
```

---

## 🧪 Testing Checklist

Quick tests to run:

```javascript
// 1. Create product without images
formData.append('name', 'Test Product');
// Don't append images

// 2. Create product with 1 image
formData.append('images', singleFile);

// 3. Create product with multiple images
files.forEach(f => formData.append('images', f));

// 4. Test file size limit (should fail)
const largeFile = new File([...], 'large.jpg', { size: 11MB });

// 5. Test invalid file type (should fail)
const txtFile = new File([...], 'file.txt');

// 6. Test without authentication (should fail)
fetch(url, { body: formData }); // No token
```

---

## 📱 Responsive Image Display

```css
/* Product Grid */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

/* Product Image */
.product-image {
  width: 100%;
  height: 300px;
  object-fit: cover;
  border-radius: 8px;
}

/* Image Gallery */
.image-gallery {
  display: flex;
  gap: 10px;
  overflow-x: auto;
}

.thumbnail {
  width: 80px;
  height: 80px;
  object-fit: cover;
  cursor: pointer;
  border: 2px solid transparent;
}

.thumbnail.active {
  border-color: #007bff;
}
```

---

## 🔗 Useful Links

- **Full Guide**: `FRONTEND_IMPLEMENTATION_GUIDE.md`
- **API Guide**: `API_ENDPOINTS_GUIDE.md`
- **Backend Setup**: `CLOUDFLARE_R2_SETUP_GUIDE.md`
- **Testing**: `R2_QUICK_TEST.md`
- **Swagger**: `http://localhost:8080/swagger-ui.html`

---

## 💡 Pro Tips

1. **Always validate files client-side** before uploading
2. **Show upload progress** for better UX
3. **Compress images** before upload (optional)
4. **Use image lazy loading** for better performance
5. **Cache product images** in browser
6. **Handle network errors** gracefully
7. **Show image previews** before upload
8. **Implement image zoom** for product details
9. **Use CDN** for faster image loading (R2 is CDN-ready)
10. **Test on mobile devices** for responsive images

---

## 🆘 Quick Troubleshooting

| Problem | Solution |
|---------|----------|
| 401 Unauthorized | Check JWT token in localStorage |
| 403 Forbidden | User needs ADMIN role |
| Images not uploading | Use FormData, not JSON |
| Images not displaying | Check R2 public access enabled |
| File too large | Max 10MB per image |
| Wrong file type | Only jpg, png, gif, webp allowed |
| CORS error | Backend needs CORS configuration |
| Network error | Check backend is running on port 8080 |

---

**Need more details?** Check `FRONTEND_IMPLEMENTATION_GUIDE.md` for complete examples! 🚀
