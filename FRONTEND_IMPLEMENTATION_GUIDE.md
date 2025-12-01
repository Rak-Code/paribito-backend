# Complete Frontend Implementation Guide

## Table of Contents
1. [Overview](#overview)
2. [API Endpoints Reference](#api-endpoints-reference)
3. [React/Next.js Implementation](#reactnextjs-implementation)
4. [Vanilla JavaScript Implementation](#vanilla-javascript-implementation)
5. [Image Handling Best Practices](#image-handling-best-practices)
6. [Error Handling](#error-handling)
7. [Complete Examples](#complete-examples)

---

## Overview

### Key Concepts
- **Single Request**: Product creation + image upload in ONE API call
- **Multipart Form Data**: Use `FormData` API, NOT JSON
- **Automatic URLs**: Backend returns image URLs ready to use
- **No Manual Upload**: Images upload automatically with product data

### What You DON'T Need to Do
❌ Upload images separately first
❌ Manage image URLs manually
❌ Make multiple API calls
❌ Handle image storage logic

### What You DO Need to Do
✅ Use `FormData` instead of JSON
✅ Append image files to form data
✅ Send multipart/form-data request
✅ Display returned image URLs

---

## API Endpoints Reference

### Base URL
```
http://localhost:8080/api
```

### Authentication
All product management endpoints require JWT token:
```javascript
headers: {
  'Authorization': `Bearer ${token}`
}
```

### 1. Create Product with Images

**Endpoint:** `POST /api/products`

**Content-Type:** `multipart/form-data` (automatic with FormData)

**Request Parameters:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | string | Yes | Product name |
| description | string | Yes | Product description |
| categoryId | string | Yes | Category ID |
| price | number | Yes | Product price |
| stockQuantity | number | Yes | Available stock |
| size | enum | No | XS, S, M, L, XL, XXL, XXXL |
| color | string | No | Product color |
| images | file[] | No | Image files (max 10MB each) |

**Response:**
```json
{
  "id": "prod123",
  "name": "Product Name",
  "description": "Description",
  "price": 99.99,
  "stockQuantity": 50,
  "categoryId": "cat123",
  "color": "Blue",
  "size": "M",
  "imageUrls": [
    "https://pub-xxxxx.r2.dev/products/img1.jpg",
    "https://pub-xxxxx.r2.dev/products/img2.jpg"
  ]
}
```

### 2. Update Product with Images

**Endpoint:** `PUT /api/products/{id}`

**Additional Parameter:**
- `keepExistingImages` (boolean, default: true)
  - `true`: Add new images to existing ones
  - `false`: Replace all images with new ones

### 3. Get All Products

**Endpoint:** `GET /api/products`

**Response:** Array of products with image URLs

### 4. Get Single Product

**Endpoint:** `GET /api/products/{id}`

### 5. Delete Product

**Endpoint:** `DELETE /api/products/{id}`

*Note: Automatically deletes associated images from R2*

---

## React/Next.js Implementation

### Complete Product Creation Component

```jsx
'use client'; // For Next.js 13+ App Router

import { useState } from 'react';

export default function CreateProductForm() {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    categoryId: '',
    price: '',
    stockQuantity: '',
    size: 'M',
    color: ''
  });
  
  const [images, setImages] = useState([]);
  const [imagePreviews, setImagePreviews] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  // Handle text input changes
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  // Handle image selection
  const handleImageChange = (e) => {
    const files = Array.from(e.target.files);
    
    // Validate file size (10MB max)
    const validFiles = files.filter(file => {
      if (file.size > 10 * 1024 * 1024) {
        alert(`${file.name} is too large. Max size is 10MB`);
        return false;
      }
      return true;
    });
    
    setImages(validFiles);
    
    // Create preview URLs
    const previews = validFiles.map(file => URL.createObjectURL(file));
    setImagePreviews(previews);
  };

  // Remove image from selection
  const removeImage = (index) => {
    setImages(prev => prev.filter((_, i) => i !== index));
    setImagePreviews(prev => prev.filter((_, i) => i !== index));
  };

  // Submit form
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess(false);

    try {
      // Create FormData object
      const formDataToSend = new FormData();
      
      // Append all product data
      formDataToSend.append('name', formData.name);
      formDataToSend.append('description', formData.description);
      formDataToSend.append('categoryId', formData.categoryId);
      formDataToSend.append('price', formData.price);
      formDataToSend.append('stockQuantity', formData.stockQuantity);
      formDataToSend.append('size', formData.size);
      formDataToSend.append('color', formData.color);
      
      // Append all images
      images.forEach(image => {
        formDataToSend.append('images', image);
      });

      // Get JWT token from localStorage
      const token = localStorage.getItem('token');
      
      // Make API request
      const response = await fetch('http://localhost:8080/api/products', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formDataToSend
      });

      if (!response.ok) {
        throw new Error('Failed to create product');
      }

      const product = await response.json();
      console.log('Product created:', product);
      
      setSuccess(true);
      
      // Reset form
      setFormData({
        name: '',
        description: '',
        categoryId: '',
        price: '',
        stockQuantity: '',
        size: 'M',
        color: ''
      });
      setImages([]);
      setImagePreviews([]);
      
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto p-6">
      <h1 className="text-2xl font-bold mb-6">Create New Product</h1>
      
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}
      
      {success && (
        <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
          Product created successfully!
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Product Name */}
        <div>
          <label className="block text-sm font-medium mb-1">
            Product Name *
          </label>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            className="w-full px-3 py-2 border rounded-lg"
            placeholder="e.g., Blue Cotton T-Shirt"
          />
        </div>

        {/* Description */}
        <div>
          <label className="block text-sm font-medium mb-1">
            Description *
          </label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            required
            rows={4}
            className="w-full px-3 py-2 border rounded-lg"
            placeholder="Detailed product description..."
          />
        </div>

        {/* Category ID */}
        <div>
          <label className="block text-sm font-medium mb-1">
            Category ID *
          </label>
          <input
            type="text"
            name="categoryId"
            value={formData.categoryId}
            onChange={handleChange}
            required
            className="w-full px-3 py-2 border rounded-lg"
            placeholder="e.g., cat123"
          />
        </div>

        {/* Price and Stock */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium mb-1">
              Price *
            </label>
            <input
              type="number"
              name="price"
              value={formData.price}
              onChange={handleChange}
              required
              step="0.01"
              min="0"
              className="w-full px-3 py-2 border rounded-lg"
              placeholder="29.99"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium mb-1">
              Stock Quantity *
            </label>
            <input
              type="number"
              name="stockQuantity"
              value={formData.stockQuantity}
              onChange={handleChange}
              required
              min="0"
              className="w-full px-3 py-2 border rounded-lg"
              placeholder="100"
            />
          </div>
        </div>

        {/* Size and Color */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium mb-1">
              Size
            </label>
            <select
              name="size"
              value={formData.size}
              onChange={handleChange}
              className="w-full px-3 py-2 border rounded-lg"
            >
              <option value="XS">XS</option>
              <option value="S">S</option>
              <option value="M">M</option>
              <option value="L">L</option>
              <option value="XL">XL</option>
              <option value="XXL">XXL</option>
              <option value="XXXL">XXXL</option>
            </select>
          </div>
          
          <div>
            <label className="block text-sm font-medium mb-1">
              Color
            </label>
            <input
              type="text"
              name="color"
              value={formData.color}
              onChange={handleChange}
              className="w-full px-3 py-2 border rounded-lg"
              placeholder="e.g., Blue"
            />
          </div>
        </div>

        {/* Image Upload */}
        <div>
          <label className="block text-sm font-medium mb-1">
            Product Images
          </label>
          <input
            type="file"
            multiple
            accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
            onChange={handleImageChange}
            className="w-full px-3 py-2 border rounded-lg"
          />
          <p className="text-sm text-gray-500 mt-1">
            Max 10MB per image. Formats: JPG, PNG, GIF, WebP
          </p>
        </div>

        {/* Image Previews */}
        {imagePreviews.length > 0 && (
          <div>
            <label className="block text-sm font-medium mb-2">
              Selected Images ({imagePreviews.length})
            </label>
            <div className="grid grid-cols-4 gap-4">
              {imagePreviews.map((preview, index) => (
                <div key={index} className="relative">
                  <img
                    src={preview}
                    alt={`Preview ${index + 1}`}
                    className="w-full h-24 object-cover rounded-lg"
                  />
                  <button
                    type="button"
                    onClick={() => removeImage(index)}
                    className="absolute top-1 right-1 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center"
                  >
                    ×
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Submit Button */}
        <button
          type="submit"
          disabled={loading}
          className="w-full bg-blue-600 text-white py-3 rounded-lg font-medium hover:bg-blue-700 disabled:bg-gray-400"
        >
          {loading ? 'Creating Product...' : 'Create Product'}
        </button>
      </form>
    </div>
  );
}
```


### Product Update Component

```jsx
import { useState, useEffect } from 'react';

export default function UpdateProductForm({ productId }) {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    categoryId: '',
    price: '',
    stockQuantity: '',
    size: 'M',
    color: ''
  });
  
  const [newImages, setNewImages] = useState([]);
  const [existingImages, setExistingImages] = useState([]);
  const [keepExistingImages, setKeepExistingImages] = useState(true);
  const [loading, setLoading] = useState(false);

  // Load existing product data
  useEffect(() => {
    const fetchProduct = async () => {
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8080/api/products/${productId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      const product = await response.json();
      
      setFormData({
        name: product.name,
        description: product.description,
        categoryId: product.categoryId,
        price: product.price,
        stockQuantity: product.stockQuantity,
        size: product.size || 'M',
        color: product.color || ''
      });
      
      setExistingImages(product.imageUrls || []);
    };
    
    fetchProduct();
  }, [productId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const formDataToSend = new FormData();
      
      // Append product data
      Object.keys(formData).forEach(key => {
        formDataToSend.append(key, formData[key]);
      });
      
      // Append new images
      newImages.forEach(image => {
        formDataToSend.append('images', image);
      });
      
      // Keep or replace existing images
      formDataToSend.append('keepExistingImages', keepExistingImages);

      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8080/api/products/${productId}`, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}` },
        body: formDataToSend
      });

      if (!response.ok) throw new Error('Update failed');
      
      const updatedProduct = await response.json();
      console.log('Product updated:', updatedProduct);
      alert('Product updated successfully!');
      
    } catch (err) {
      alert('Error: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {/* Same form fields as create... */}
      
      {/* Existing Images */}
      {existingImages.length > 0 && (
        <div>
          <label className="block text-sm font-medium mb-2">
            Current Images
          </label>
          <div className="grid grid-cols-4 gap-4 mb-2">
            {existingImages.map((url, index) => (
              <img
                key={index}
                src={url}
                alt={`Current ${index + 1}`}
                className="w-full h-24 object-cover rounded-lg"
              />
            ))}
          </div>
          
          <label className="flex items-center space-x-2">
            <input
              type="checkbox"
              checked={keepExistingImages}
              onChange={(e) => setKeepExistingImages(e.target.checked)}
            />
            <span className="text-sm">Keep existing images</span>
          </label>
        </div>
      )}
      
      {/* New Images Upload */}
      <div>
        <label className="block text-sm font-medium mb-1">
          {keepExistingImages ? 'Add New Images' : 'Replace with New Images'}
        </label>
        <input
          type="file"
          multiple
          accept="image/*"
          onChange={(e) => setNewImages(Array.from(e.target.files))}
          className="w-full px-3 py-2 border rounded-lg"
        />
      </div>

      <button
        type="submit"
        disabled={loading}
        className="w-full bg-blue-600 text-white py-3 rounded-lg"
      >
        {loading ? 'Updating...' : 'Update Product'}
      </button>
    </form>
  );
}
```

### Display Product with Images

```jsx
export default function ProductCard({ product }) {
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  const nextImage = () => {
    setCurrentImageIndex((prev) => 
      (prev + 1) % product.imageUrls.length
    );
  };

  const prevImage = () => {
    setCurrentImageIndex((prev) => 
      prev === 0 ? product.imageUrls.length - 1 : prev - 1
    );
  };

  return (
    <div className="border rounded-lg p-4">
      {/* Image Gallery */}
      {product.imageUrls && product.imageUrls.length > 0 ? (
        <div className="relative mb-4">
          <img
            src={product.imageUrls[currentImageIndex]}
            alt={product.name}
            className="w-full h-64 object-cover rounded-lg"
          />
          
          {product.imageUrls.length > 1 && (
            <>
              <button
                onClick={prevImage}
                className="absolute left-2 top-1/2 -translate-y-1/2 bg-white/80 rounded-full p-2"
              >
                ←
              </button>
              <button
                onClick={nextImage}
                className="absolute right-2 top-1/2 -translate-y-1/2 bg-white/80 rounded-full p-2"
              >
                →
              </button>
              
              {/* Thumbnails */}
              <div className="flex gap-2 mt-2">
                {product.imageUrls.map((url, index) => (
                  <img
                    key={index}
                    src={url}
                    alt={`Thumbnail ${index + 1}`}
                    onClick={() => setCurrentImageIndex(index)}
                    className={`w-16 h-16 object-cover rounded cursor-pointer ${
                      index === currentImageIndex ? 'ring-2 ring-blue-500' : ''
                    }`}
                  />
                ))}
              </div>
            </>
          )}
        </div>
      ) : (
        <div className="w-full h-64 bg-gray-200 rounded-lg flex items-center justify-center mb-4">
          <span className="text-gray-400">No image</span>
        </div>
      )}

      {/* Product Info */}
      <h3 className="text-xl font-bold mb-2">{product.name}</h3>
      <p className="text-gray-600 mb-2">{product.description}</p>
      <p className="text-2xl font-bold text-blue-600">${product.price}</p>
      <p className="text-sm text-gray-500">Stock: {product.stockQuantity}</p>
      {product.size && <p className="text-sm">Size: {product.size}</p>}
      {product.color && <p className="text-sm">Color: {product.color}</p>}
    </div>
  );
}
```

### Custom Hook for Product Management

```jsx
// hooks/useProducts.js
import { useState, useEffect } from 'react';

export function useProducts() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchProducts = async () => {
    setLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/products');
      const data = await response.json();
      setProducts(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const createProduct = async (formData, images) => {
    const formDataToSend = new FormData();
    
    Object.keys(formData).forEach(key => {
      formDataToSend.append(key, formData[key]);
    });
    
    images.forEach(image => {
      formDataToSend.append('images', image);
    });

    const token = localStorage.getItem('token');
    const response = await fetch('http://localhost:8080/api/products', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` },
      body: formDataToSend
    });

    if (!response.ok) throw new Error('Failed to create product');
    
    const newProduct = await response.json();
    setProducts(prev => [...prev, newProduct]);
    return newProduct;
  };

  const deleteProduct = async (productId) => {
    const token = localStorage.getItem('token');
    const response = await fetch(`http://localhost:8080/api/products/${productId}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    });

    if (!response.ok) throw new Error('Failed to delete product');
    
    setProducts(prev => prev.filter(p => p.id !== productId));
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  return {
    products,
    loading,
    error,
    createProduct,
    deleteProduct,
    refetch: fetchProducts
  };
}

// Usage:
// const { products, createProduct, deleteProduct } = useProducts();
```

---

## Vanilla JavaScript Implementation

### Complete HTML + JavaScript Example

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Create Product</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body { font-family: Arial, sans-serif; padding: 20px; max-width: 800px; margin: 0 auto; }
    .form-group { margin-bottom: 15px; }
    label { display: block; margin-bottom: 5px; font-weight: bold; }
    input, textarea, select { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
    textarea { resize: vertical; min-height: 100px; }
    .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
    button { background: #007bff; color: white; padding: 12px 24px; border: none; border-radius: 4px; cursor: pointer; width: 100%; font-size: 16px; }
    button:hover { background: #0056b3; }
    button:disabled { background: #ccc; cursor: not-allowed; }
    .preview-container { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; margin-top: 10px; }
    .preview-item { position: relative; }
    .preview-item img { width: 100%; height: 100px; object-fit: cover; border-radius: 4px; }
    .remove-btn { position: absolute; top: 5px; right: 5px; background: red; color: white; border: none; border-radius: 50%; width: 25px; height: 25px; cursor: pointer; }
    .alert { padding: 12px; margin-bottom: 15px; border-radius: 4px; }
    .alert-success { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
    .alert-error { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
  </style>
</head>
<body>
  <h1>Create New Product</h1>
  
  <div id="alertContainer"></div>

  <form id="productForm">
    <div class="form-group">
      <label for="name">Product Name *</label>
      <input type="text" id="name" name="name" required>
    </div>

    <div class="form-group">
      <label for="description">Description *</label>
      <textarea id="description" name="description" required></textarea>
    </div>

    <div class="form-group">
      <label for="categoryId">Category ID *</label>
      <input type="text" id="categoryId" name="categoryId" required>
    </div>

    <div class="grid">
      <div class="form-group">
        <label for="price">Price *</label>
        <input type="number" id="price" name="price" step="0.01" min="0" required>
      </div>

      <div class="form-group">
        <label for="stockQuantity">Stock Quantity *</label>
        <input type="number" id="stockQuantity" name="stockQuantity" min="0" required>
      </div>
    </div>

    <div class="grid">
      <div class="form-group">
        <label for="size">Size</label>
        <select id="size" name="size">
          <option value="XS">XS</option>
          <option value="S">S</option>
          <option value="M" selected>M</option>
          <option value="L">L</option>
          <option value="XL">XL</option>
          <option value="XXL">XXL</option>
          <option value="XXXL">XXXL</option>
        </select>
      </div>

      <div class="form-group">
        <label for="color">Color</label>
        <input type="text" id="color" name="color">
      </div>
    </div>

    <div class="form-group">
      <label for="images">Product Images</label>
      <input type="file" id="images" name="images" multiple accept="image/*">
      <small>Max 10MB per image. Formats: JPG, PNG, GIF, WebP</small>
    </div>

    <div id="previewContainer" class="preview-container"></div>

    <button type="submit" id="submitBtn">Create Product</button>
  </form>

  <script>
    const form = document.getElementById('productForm');
    const imagesInput = document.getElementById('images');
    const previewContainer = document.getElementById('previewContainer');
    const submitBtn = document.getElementById('submitBtn');
    const alertContainer = document.getElementById('alertContainer');
    
    let selectedFiles = [];

    // Handle image selection
    imagesInput.addEventListener('change', (e) => {
      const files = Array.from(e.target.files);
      
      // Validate file sizes
      selectedFiles = files.filter(file => {
        if (file.size > 10 * 1024 * 1024) {
          showAlert(`${file.name} is too large. Max size is 10MB`, 'error');
          return false;
        }
        return true;
      });
      
      displayPreviews();
    });

    // Display image previews
    function displayPreviews() {
      previewContainer.innerHTML = '';
      
      selectedFiles.forEach((file, index) => {
        const reader = new FileReader();
        
        reader.onload = (e) => {
          const div = document.createElement('div');
          div.className = 'preview-item';
          div.innerHTML = `
            <img src="${e.target.result}" alt="Preview ${index + 1}">
            <button type="button" class="remove-btn" onclick="removeImage(${index})">×</button>
          `;
          previewContainer.appendChild(div);
        };
        
        reader.readAsDataURL(file);
      });
    }

    // Remove image from selection
    window.removeImage = function(index) {
      selectedFiles.splice(index, 1);
      displayPreviews();
    };

    // Show alert message
    function showAlert(message, type) {
      alertContainer.innerHTML = `
        <div class="alert alert-${type}">
          ${message}
        </div>
      `;
      
      setTimeout(() => {
        alertContainer.innerHTML = '';
      }, 5000);
    }

    // Handle form submission
    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      
      submitBtn.disabled = true;
      submitBtn.textContent = 'Creating Product...';

      try {
        // Create FormData
        const formData = new FormData();
        
        // Append form fields
        formData.append('name', document.getElementById('name').value);
        formData.append('description', document.getElementById('description').value);
        formData.append('categoryId', document.getElementById('categoryId').value);
        formData.append('price', document.getElementById('price').value);
        formData.append('stockQuantity', document.getElementById('stockQuantity').value);
        formData.append('size', document.getElementById('size').value);
        formData.append('color', document.getElementById('color').value);
        
        // Append images
        selectedFiles.forEach(file => {
          formData.append('images', file);
        });

        // Get JWT token
        const token = localStorage.getItem('token');
        
        if (!token) {
          throw new Error('Please login first');
        }

        // Make API request
        const response = await fetch('http://localhost:8080/api/products', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`
          },
          body: formData
        });

        if (!response.ok) {
          const error = await response.json();
          throw new Error(error.message || 'Failed to create product');
        }

        const product = await response.json();
        console.log('Product created:', product);
        
        showAlert('Product created successfully!', 'success');
        
        // Reset form
        form.reset();
        selectedFiles = [];
        previewContainer.innerHTML = '';
        
      } catch (error) {
        console.error('Error:', error);
        showAlert(error.message, 'error');
      } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Create Product';
      }
    });
  </script>
</body>
</html>
```

---

## Image Handling Best Practices

### 1. File Validation

```javascript
function validateImage(file) {
  const maxSize = 10 * 1024 * 1024; // 10MB
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
  
  if (file.size > maxSize) {
    return { valid: false, error: 'File too large (max 10MB)' };
  }
  
  if (!allowedTypes.includes(file.type)) {
    return { valid: false, error: 'Invalid file type' };
  }
  
  return { valid: true };
}

// Usage
const validation = validateImage(file);
if (!validation.valid) {
  alert(validation.error);
}
```

### 2. Image Compression (Optional)

```javascript
// Install: npm install browser-image-compression

import imageCompression from 'browser-image-compression';

async function compressImage(file) {
  const options = {
    maxSizeMB: 1,
    maxWidthOrHeight: 1920,
    useWebWorker: true
  };
  
  try {
    const compressedFile = await imageCompression(file, options);
    return compressedFile;
  } catch (error) {
    console.error('Compression failed:', error);
    return file; // Return original if compression fails
  }
}

// Usage in form
const compressedImages = await Promise.all(
  images.map(img => compressImage(img))
);
```

### 3. Image Preview with Cleanup

```javascript
function createImagePreview(file) {
  return new Promise((resolve) => {
    const reader = new FileReader();
    reader.onload = (e) => resolve(e.target.result);
    reader.readAsDataURL(file);
  });
}

// Cleanup preview URLs to prevent memory leaks
useEffect(() => {
  return () => {
    imagePreviews.forEach(url => URL.revokeObjectURL(url));
  };
}, [imagePreviews]);
```

### 4. Progress Tracking

```javascript
async function uploadProductWithProgress(formData, onProgress) {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();
    
    xhr.upload.addEventListener('progress', (e) => {
      if (e.lengthComputable) {
        const percentComplete = (e.loaded / e.total) * 100;
        onProgress(percentComplete);
      }
    });
    
    xhr.addEventListener('load', () => {
      if (xhr.status === 201) {
        resolve(JSON.parse(xhr.responseText));
      } else {
        reject(new Error('Upload failed'));
      }
    });
    
    xhr.addEventListener('error', () => reject(new Error('Network error')));
    
    const token = localStorage.getItem('token');
    xhr.open('POST', 'http://localhost:8080/api/products');
    xhr.setRequestHeader('Authorization', `Bearer ${token}`);
    xhr.send(formData);
  });
}

// Usage
const [uploadProgress, setUploadProgress] = useState(0);

await uploadProductWithProgress(formData, (progress) => {
  setUploadProgress(progress);
});
```

---

## Error Handling

### Comprehensive Error Handler

```javascript
async function createProduct(formData, images) {
  try {
    const formDataToSend = new FormData();
    
    // Append data
    Object.keys(formData).forEach(key => {
      formDataToSend.append(key, formData[key]);
    });
    
    images.forEach(image => {
      formDataToSend.append('images', image);
    });

    const token = localStorage.getItem('token');
    
    if (!token) {
      throw new Error('AUTHENTICATION_REQUIRED');
    }

    const response = await fetch('http://localhost:8080/api/products', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` },
      body: formDataToSend
    });

    // Handle different error codes
    if (response.status === 401) {
      throw new Error('UNAUTHORIZED');
    }
    
    if (response.status === 403) {
      throw new Error('FORBIDDEN');
    }
    
    if (response.status === 400) {
      const error = await response.json();
      throw new Error(`VALIDATION_ERROR: ${error.message}`);
    }
    
    if (!response.ok) {
      throw new Error('SERVER_ERROR');
    }

    return await response.json();
    
  } catch (error) {
    // Handle specific errors
    const errorMessages = {
      'AUTHENTICATION_REQUIRED': 'Please login to continue',
      'UNAUTHORIZED': 'Your session has expired. Please login again',
      'FORBIDDEN': 'You do not have permission to create products',
      'SERVER_ERROR': 'Server error. Please try again later',
      'NetworkError': 'Network error. Check your connection'
    };
    
    const message = errorMessages[error.message] || error.message;
    
    // Log for debugging
    console.error('Product creation error:', error);
    
    // Show user-friendly message
    throw new Error(message);
  }
}
```

### Error Display Component

```jsx
function ErrorMessage({ error, onDismiss }) {
  if (!error) return null;
  
  return (
    <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4">
      <strong className="font-bold">Error: </strong>
      <span className="block sm:inline">{error}</span>
      {onDismiss && (
        <button
          onClick={onDismiss}
          className="absolute top-0 bottom-0 right-0 px-4 py-3"
        >
          ×
        </button>
      )}
    </div>
  );
}
```

---

## Complete Examples

### Example 1: Simple Product Form (Minimal)

```jsx
function SimpleProductForm() {
  const handleSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    const token = localStorage.getItem('token');
    
    const response = await fetch('http://localhost:8080/api/products', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` },
      body: formData
    });
    
    if (response.ok) {
      alert('Product created!');
      e.target.reset();
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input name="name" placeholder="Name" required />
      <textarea name="description" placeholder="Description" required />
      <input name="categoryId" placeholder="Category ID" required />
      <input name="price" type="number" step="0.01" placeholder="Price" required />
      <input name="stockQuantity" type="number" placeholder="Stock" required />
      <input name="images" type="file" multiple accept="image/*" />
      <button type="submit">Create</button>
    </form>
  );
}
```

### Example 2: Product List with Images

```jsx
function ProductList() {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetch('http://localhost:8080/api/products')
      .then(res => res.json())
      .then(data => setProducts(data));
  }, []);

  return (
    <div className="grid grid-cols-3 gap-4">
      {products.map(product => (
        <div key={product.id} className="border rounded p-4">
          {product.imageUrls?.[0] && (
            <img
              src={product.imageUrls[0]}
              alt={product.name}
              className="w-full h-48 object-cover rounded mb-2"
            />
          )}
          <h3 className="font-bold">{product.name}</h3>
          <p className="text-gray-600">${product.price}</p>
        </div>
      ))}
    </div>
  );
}
```

### Example 3: Product with Image Zoom

```jsx
function ProductImageZoom({ imageUrl, alt }) {
  const [isZoomed, setIsZoomed] = useState(false);

  return (
    <>
      <img
        src={imageUrl}
        alt={alt}
        onClick={() => setIsZoomed(true)}
        className="cursor-zoom-in"
      />
      
      {isZoomed && (
        <div
          className="fixed inset-0 bg-black/80 flex items-center justify-center z-50"
          onClick={() => setIsZoomed(false)}
        >
          <img
            src={imageUrl}
            alt={alt}
            className="max-w-[90%] max-h-[90%] object-contain"
          />
        </div>
      )}
    </>
  );
}
```

---

## Quick Reference

### ✅ DO's

```javascript
// ✅ Use FormData
const formData = new FormData();
formData.append('name', 'Product');
formData.append('images', file);

// ✅ Include Authorization header
headers: { 'Authorization': `Bearer ${token}` }

// ✅ Validate files before upload
if (file.size > 10MB) return;

// ✅ Handle errors properly
try { ... } catch (error) { showError(error); }

// ✅ Show loading state
setLoading(true);

// ✅ Display image previews
URL.createObjectURL(file);
```

### ❌ DON'Ts

```javascript
// ❌ Don't use JSON for file uploads
body: JSON.stringify({ images: files }) // WRONG!

// ❌ Don't set Content-Type header
headers: { 'Content-Type': 'multipart/form-data' } // WRONG! Let browser set it

// ❌ Don't upload images separately first
await uploadImages(); // Not needed anymore!
await createProduct();

// ❌ Don't forget authentication
fetch('/api/products', { body: formData }); // Missing token!

// ❌ Don't ignore file size limits
formData.append('images', hugeFile); // Validate first!
```

---

## Testing Checklist

Before deploying your frontend:

- [ ] Product creation works with images
- [ ] Product creation works without images
- [ ] Multiple images can be uploaded
- [ ] Image previews display correctly
- [ ] File size validation works (10MB limit)
- [ ] File type validation works (jpg, png, gif, webp)
- [ ] Loading states display during upload
- [ ] Error messages display properly
- [ ] Success messages display after creation
- [ ] Product images display in product list
- [ ] Product images display in product detail
- [ ] Image URLs are accessible (test in browser)
- [ ] Update product keeps existing images (if selected)
- [ ] Update product replaces images (if selected)
- [ ] Delete product works
- [ ] Authentication errors handled
- [ ] Network errors handled
- [ ] Form resets after successful creation

---

## Support & Resources

### API Documentation
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs: See `API_ENDPOINTS_GUIDE.md`

### Backend Setup
- R2 Setup: See `CLOUDFLARE_R2_SETUP_GUIDE.md`
- Quick Test: See `R2_QUICK_TEST.md`

### Common Issues
- **401 Unauthorized**: Check JWT token
- **403 Forbidden**: User needs ADMIN role
- **400 Bad Request**: Check form data format
- **File too large**: Max 10MB per image
- **Images not loading**: Check R2 public access

---

**You're ready to build!** Use these examples as templates for your frontend implementation. 🚀
