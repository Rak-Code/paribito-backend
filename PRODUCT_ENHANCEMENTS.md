# Product Page Enhancements - Implementation Guide

This document describes the three major enhancements implemented for the Paribito e-commerce platform.

## 1. Color Variants for Products

### Overview
Products can now have multiple color variants, each with its own images and stock quantity. This allows a single product (e.g., a shirt design) to be available in multiple colors without creating separate products.

### Database Schema Changes
- Added `ProductVariant` entity with fields:
  - `variantId`: Unique identifier
  - `colorName`: Display name (e.g., "Navy Blue")
  - `colorCode`: Hex color code (e.g., "#000080")
  - `imageUrls`: List of images for this color
  - `stockQuantity`: Stock for this specific color

- Updated `Product` entity:
  - Added `colorVariants` field (List<ProductVariant>)

### API Endpoints

#### Add Color Variant
```
POST /api/products/{productId}/variants
Authorization: Admin only
Content-Type: multipart/form-data

Parameters:
- colorName: String (required) - e.g., "Navy Blue"
- colorCode: String (required) - e.g., "#000080"
- stockQuantity: int (optional, default: 0)
- images: MultipartFile[] (optional) - Images for this color variant

Response: ProductResponseDTO with updated variants
```

#### Update Color Variant
```
PUT /api/products/{productId}/variants/{variantId}
Authorization: Admin only
Content-Type: multipart/form-data

Parameters:
- colorName: String (optional)
- colorCode: String (optional)
- stockQuantity: int (optional)
- images: MultipartFile[] (optional)
- keepExistingImages: boolean (default: true)

Response: ProductResponseDTO with updated variants
```

#### Delete Color Variant
```
DELETE /api/products/{productId}/variants/{variantId}
Authorization: Admin only

Response: ProductResponseDTO without the deleted variant
```

#### Get Color Variants
```
GET /api/products/{productId}/variants

Response: List of color variants with their details
```

### Frontend Integration Example
```javascript
// Add a color variant
const formData = new FormData();
formData.append('colorName', 'Navy Blue');
formData.append('colorCode', '#000080');
formData.append('stockQuantity', 50);
formData.append('images', file1);
formData.append('images', file2);

fetch('/api/products/PRODUCT_ID/variants', {
  method: 'POST',
  headers: { 'Authorization': 'Bearer TOKEN' },
  body: formData
});
```

---

## 2. Individual Image Management

### Overview
You can now delete individual images from products or color variants without deleting the entire product. You can also add new images to existing products/variants.

### API Endpoints

#### Delete Product Image
```
DELETE /api/products/{productId}/images?imageUrl={imageUrl}
Authorization: Admin only

Parameters:
- imageUrl: String (required) - Full URL of the image to delete

Response: ProductResponseDTO with updated image list
```

#### Delete Variant Image
```
DELETE /api/products/{productId}/variants/{variantId}/images?imageUrl={imageUrl}
Authorization: Admin only

Parameters:
- imageUrl: String (required) - Full URL of the image to delete

Response: ProductResponseDTO with updated variant images
```

#### Add Product Images
```
POST /api/products/{productId}/images
Authorization: Admin only
Content-Type: multipart/form-data

Parameters:
- images: MultipartFile[] (required) - New images to add

Response: ProductResponseDTO with all images
```

#### Add Variant Images
```
POST /api/products/{productId}/variants/{variantId}/images
Authorization: Admin only
Content-Type: multipart/form-data

Parameters:
- images: MultipartFile[] (required) - New images to add to variant

Response: ProductResponseDTO with updated variant images
```

### Frontend Integration Example
```javascript
// Delete a specific image
fetch('/api/products/PRODUCT_ID/images?imageUrl=' + encodeURIComponent(imageUrl), {
  method: 'DELETE',
  headers: { 'Authorization': 'Bearer TOKEN' }
});

// Add new images
const formData = new FormData();
formData.append('images', file1);
formData.append('images', file2);

fetch('/api/products/PRODUCT_ID/images', {
  method: 'POST',
  headers: { 'Authorization': 'Bearer TOKEN' },
  body: formData
});
```

---

## 3. Made-to-Measure / Bespoke Category

### Overview
A new product type for custom-made shirts where customers can:
1. Provide their body measurements
2. Send a sample shirt for sizing
3. Select designs and colors
4. Get a printable shipping label for sample shirts

### Database Schema

#### Product Entity Updates
- Added `productType` field:
  - `REGULAR`: Standard ready-made products
  - `BESPOKE`: Made-to-measure products
  - `MADE_TO_MEASURE`: Alias for bespoke
- Added `availableDesigns` field: List of design options

#### New Entities

**CustomMeasurement**
```java
- chest: Double (cm or inches)
- waist: Double
- shoulder: Double
- sleeveLength: Double
- shirtLength: Double
- neck: Double
- bicep: Double
- wrist: Double
- unit: MeasurementUnit (CM or INCHES)
- notes: String
```

**BespokeOrder**
```java
- id: String
- userId: String
- productId: String (base shirt design)
- selectedColor: String
- selectedDesign: String
- measurementOption: PROVIDE_MEASUREMENTS or SEND_SAMPLE_SHIRT
- customMeasurements: CustomMeasurement (if provided)
- sampleShippingTrackingId: String (if sending sample)
- price: double
- status: OrderStatus
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
- customerNotes: String
```

**Order Statuses**
- `PENDING_MEASUREMENTS`: Waiting for measurements or sample
- `MEASUREMENTS_RECEIVED`: Measurements provided
- `SAMPLE_RECEIVED`: Sample shirt received
- `IN_PRODUCTION`: Being manufactured
- `COMPLETED`: Ready to ship
- `SHIPPED`: Shipped to customer
- `DELIVERED`: Delivered
- `CANCELLED`: Order cancelled

### API Endpoints

#### Create Bespoke Order
```
POST /api/bespoke-orders
Authorization: Authenticated user
Content-Type: application/json

Request Body:
{
  "productId": "PRODUCT_ID",
  "selectedColor": "Navy Blue",
  "selectedDesign": "Striped",
  "measurementOption": "PROVIDE_MEASUREMENTS",
  "customMeasurements": {
    "chest": 40.0,
    "waist": 34.0,
    "shoulder": 18.0,
    "sleeveLength": 25.0,
    "shirtLength": 30.0,
    "neck": 15.5,
    "bicep": 14.0,
    "wrist": 7.0,
    "unit": "INCHES",
    "notes": "Prefer slightly loose fit"
  },
  "customerNotes": "Please use premium fabric"
}

Response: BespokeOrderResponseDTO
```

#### Update Bespoke Order
```
PUT /api/bespoke-orders/{orderId}
Authorization: Authenticated user
Content-Type: application/json

Request Body: Same as create
Response: BespokeOrderResponseDTO
```

#### Update Order Status (Admin)
```
PATCH /api/bespoke-orders/{orderId}/status?status=IN_PRODUCTION
Authorization: Admin only

Response: BespokeOrderResponseDTO
```

#### Add Sample Tracking ID
```
PATCH /api/bespoke-orders/{orderId}/tracking?trackingId=TRACK123
Authorization: Authenticated user

Response: BespokeOrderResponseDTO
```

#### Get User's Bespoke Orders
```
GET /api/bespoke-orders/my-orders
Authorization: Authenticated user

Response: List<BespokeOrderResponseDTO>
```

#### Get All Bespoke Orders (Admin)
```
GET /api/bespoke-orders?page=0&size=10&status=IN_PRODUCTION
Authorization: Admin only

Parameters:
- page: int (optional)
- size: int (optional)
- status: OrderStatus (optional)
- sortBy: String (default: "createdAt")
- sortDirection: String (default: "DESC")

Response: Page<BespokeOrderResponseDTO>
```

#### Get Shipping Label
```
GET /api/bespoke-orders/shipping-label

Response:
{
  "shippingAddress": "Paribito, 123 Fashion Street, Mumbai, Maharashtra 400001, India",
  "instructions": "Please print this address and attach it to your sample shirt package"
}
```

### Configuration
Add to `application.properties`:
```properties
# Bespoke order shipping address
bespoke.shipping.address=Paribito, 123 Fashion Street, Mumbai, Maharashtra 400001, India
```

### Frontend Integration Examples

#### Create Bespoke Order with Measurements
```javascript
const order = {
  productId: 'SHIRT_PRODUCT_ID',
  selectedColor: 'Navy Blue',
  selectedDesign: 'Striped',
  measurementOption: 'PROVIDE_MEASUREMENTS',
  customMeasurements: {
    chest: 40.0,
    waist: 34.0,
    shoulder: 18.0,
    sleeveLength: 25.0,
    shirtLength: 30.0,
    neck: 15.5,
    bicep: 14.0,
    wrist: 7.0,
    unit: 'INCHES',
    notes: 'Prefer slightly loose fit'
  },
  customerNotes: 'Please use premium fabric'
};

fetch('/api/bespoke-orders', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer TOKEN',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(order)
});
```

#### Create Bespoke Order - Send Sample Shirt
```javascript
const order = {
  productId: 'SHIRT_PRODUCT_ID',
  selectedColor: 'White',
  selectedDesign: 'Plain',
  measurementOption: 'SEND_SAMPLE_SHIRT',
  customerNotes: 'Sending my favorite shirt for sizing'
};

// 1. Create order
const response = await fetch('/api/bespoke-orders', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer TOKEN',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(order)
});

const orderData = await response.json();

// 2. Get shipping label
const labelResponse = await fetch('/api/bespoke-orders/shipping-label');
const labelData = await labelResponse.json();

// Display shipping address to user
console.log(labelData.shippingAddress);
console.log(labelData.instructions);

// 3. After shipping, add tracking ID
await fetch(`/api/bespoke-orders/${orderData.id}/tracking?trackingId=TRACK123`, {
  method: 'PATCH',
  headers: { 'Authorization': 'Bearer TOKEN' }
});
```

#### Display Shipping Label (Printable)
```html
<div id="shipping-label" style="border: 2px solid black; padding: 20px; width: 400px;">
  <h2>Ship To:</h2>
  <p id="address"></p>
  <p><strong>Instructions:</strong></p>
  <p id="instructions"></p>
  <button onclick="window.print()">Print Label</button>
</div>

<script>
fetch('/api/bespoke-orders/shipping-label')
  .then(res => res.json())
  .then(data => {
    document.getElementById('address').textContent = data.shippingAddress;
    document.getElementById('instructions').textContent = data.instructions;
  });
</script>
```

---

## Creating a Bespoke Product

To create a product that supports made-to-measure orders:

```javascript
const formData = new FormData();
formData.append('name', 'Custom Dress Shirt');
formData.append('description', 'Made-to-measure dress shirt with custom designs');
formData.append('categoryId', 'SHIRTS_CATEGORY_ID');
formData.append('price', 2999.00);
formData.append('stockQuantity', 0); // Bespoke items are made on demand
formData.append('availableSizes', JSON.stringify(['CUSTOM']));
formData.append('productType', 'BESPOKE');
formData.append('availableDesigns', JSON.stringify([
  'Plain',
  'Striped',
  'Checkered',
  'Floral'
]));
formData.append('images', file1);

fetch('/api/products', {
  method: 'POST',
  headers: { 'Authorization': 'Bearer ADMIN_TOKEN' },
  body: formData
});
```

---

## Testing the Features

### 1. Test Color Variants
```bash
# Add a color variant
curl -X POST http://localhost:8080/api/products/PRODUCT_ID/variants \
  -H "Authorization: Bearer TOKEN" \
  -F "colorName=Navy Blue" \
  -F "colorCode=#000080" \
  -F "stockQuantity=50" \
  -F "images=@image1.jpg" \
  -F "images=@image2.jpg"

# Get variants
curl http://localhost:8080/api/products/PRODUCT_ID/variants
```

### 2. Test Image Management
```bash
# Delete an image
curl -X DELETE "http://localhost:8080/api/products/PRODUCT_ID/images?imageUrl=IMAGE_URL" \
  -H "Authorization: Bearer TOKEN"

# Add images
curl -X POST http://localhost:8080/api/products/PRODUCT_ID/images \
  -H "Authorization: Bearer TOKEN" \
  -F "images=@new_image.jpg"
```

### 3. Test Bespoke Orders
```bash
# Create bespoke order
curl -X POST http://localhost:8080/api/bespoke-orders \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "PRODUCT_ID",
    "selectedColor": "Navy Blue",
    "selectedDesign": "Striped",
    "measurementOption": "PROVIDE_MEASUREMENTS",
    "customMeasurements": {
      "chest": 40.0,
      "waist": 34.0,
      "shoulder": 18.0,
      "sleeveLength": 25.0,
      "shirtLength": 30.0,
      "neck": 15.5,
      "unit": "INCHES"
    }
  }'

# Get shipping label
curl http://localhost:8080/api/bespoke-orders/shipping-label
```

---

## Database Migration Notes

No explicit migration is needed as MongoDB is schema-less. The new fields will be added automatically when products/orders are created or updated. However, existing products will have:
- `colorVariants`: null or empty list
- `productType`: null (defaults to REGULAR in code)
- `availableDesigns`: null

---

## Summary

These three enhancements provide:

1. **Color Variants**: Manage multiple colors per product with separate images and stock
2. **Image Management**: Add/delete individual images without recreating products
3. **Bespoke Orders**: Complete made-to-measure workflow with measurements or sample shirts

All features are backward compatible with existing products.
