# Product Enhancements - Quick Start Guide

## What's New?

Three major features have been added to the Paribito e-commerce platform:

### 1. 🎨 Color Variants
Add multiple colors to a single product, each with its own images and stock.

### 2. 🖼️ Individual Image Management
Delete or add individual images without recreating the entire product.

### 3. 👔 Made-to-Measure / Bespoke Orders
Custom shirt orders with body measurements or sample shirt sizing.

---

## Quick Setup

### 1. Configuration
Add to your `.env` file (optional):
```env
BESPOKE_SHIPPING_ADDRESS=Your Company Name, Street Address, City, State ZIP, Country
```

### 2. Restart Application
```bash
mvn spring-boot:run
```

---

## Usage Examples

### Color Variants

#### Add a Color Variant (Admin)
```bash
curl -X POST http://localhost:8080/api/products/PRODUCT_ID/variants \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -F "colorName=Navy Blue" \
  -F "colorCode=#000080" \
  -F "stockQuantity=50" \
  -F "images=@navy_front.jpg" \
  -F "images=@navy_back.jpg"
```

#### Get All Variants
```bash
curl http://localhost:8080/api/products/PRODUCT_ID/variants
```

---

### Image Management

#### Delete One Image (Admin)
```bash
curl -X DELETE "http://localhost:8080/api/products/PRODUCT_ID/images?imageUrl=https://..." \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

#### Add New Images (Admin)
```bash
curl -X POST http://localhost:8080/api/products/PRODUCT_ID/images \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -F "images=@new_image1.jpg" \
  -F "images=@new_image2.jpg"
```

---

### Bespoke Orders

#### 1. Create a Bespoke Product (Admin)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -F "name=Custom Dress Shirt" \
  -F "description=Made-to-measure dress shirt" \
  -F "categoryId=SHIRTS_CATEGORY" \
  -F "price=2999" \
  -F "stockQuantity=0" \
  -F 'availableSizes=["CUSTOM"]' \
  -F "productType=BESPOKE" \
  -F 'availableDesigns=["Plain","Striped","Checkered"]' \
  -F "images=@shirt_design.jpg"
```

#### 2. Customer Places Bespoke Order
```bash
curl -X POST http://localhost:8080/api/bespoke-orders \
  -H "Authorization: Bearer USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "BESPOKE_PRODUCT_ID",
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
  }'
```

#### 3. Get Shipping Label (For Sample Shirt Option)
```bash
curl http://localhost:8080/api/bespoke-orders/shipping-label
```

Response:
```json
{
  "shippingAddress": "Paribito, 123 Fashion Street, Mumbai, Maharashtra 400001, India",
  "instructions": "Please print this address and attach it to your sample shirt package"
}
```

#### 4. View My Orders
```bash
curl http://localhost:8080/api/bespoke-orders/my-orders \
  -H "Authorization: Bearer USER_TOKEN"
```

#### 5. Admin Updates Order Status
```bash
curl -X PATCH "http://localhost:8080/api/bespoke-orders/ORDER_ID/status?status=IN_PRODUCTION" \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

---

## Frontend Integration

### React Example - Color Variant Selector
```jsx
function ColorVariantSelector({ productId, variants }) {
  const [selectedVariant, setSelectedVariant] = useState(null);

  return (
    <div className="color-variants">
      <h3>Available Colors:</h3>
      <div className="color-options">
        {variants.map(variant => (
          <button
            key={variant.variantId}
            onClick={() => setSelectedVariant(variant)}
            style={{
              backgroundColor: variant.colorCode,
              border: selectedVariant?.variantId === variant.variantId ? '3px solid black' : '1px solid gray'
            }}
            title={variant.colorName}
          >
            {variant.colorName}
          </button>
        ))}
      </div>
      
      {selectedVariant && (
        <div className="variant-images">
          {selectedVariant.imageUrls.map((url, idx) => (
            <img key={idx} src={url} alt={`${selectedVariant.colorName} ${idx + 1}`} />
          ))}
          <p>Stock: {selectedVariant.stockQuantity}</p>
        </div>
      )}
    </div>
  );
}
```

### React Example - Bespoke Order Form
```jsx
function BespokeOrderForm({ productId }) {
  const [measurementOption, setMeasurementOption] = useState('PROVIDE_MEASUREMENTS');
  const [measurements, setMeasurements] = useState({
    chest: '', waist: '', shoulder: '', sleeveLength: '',
    shirtLength: '', neck: '', bicep: '', wrist: '',
    unit: 'INCHES', notes: ''
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    const order = {
      productId,
      selectedColor: document.getElementById('color').value,
      selectedDesign: document.getElementById('design').value,
      measurementOption,
      customMeasurements: measurementOption === 'PROVIDE_MEASUREMENTS' ? measurements : null,
      customerNotes: document.getElementById('notes').value
    };

    const response = await fetch('/api/bespoke-orders', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(order)
    });

    if (response.ok) {
      alert('Bespoke order created successfully!');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Custom Order</h2>
      
      <select id="color" required>
        <option value="White">White</option>
        <option value="Navy Blue">Navy Blue</option>
        <option value="Black">Black</option>
      </select>

      <select id="design" required>
        <option value="Plain">Plain</option>
        <option value="Striped">Striped</option>
        <option value="Checkered">Checkered</option>
      </select>

      <div>
        <label>
          <input
            type="radio"
            value="PROVIDE_MEASUREMENTS"
            checked={measurementOption === 'PROVIDE_MEASUREMENTS'}
            onChange={(e) => setMeasurementOption(e.target.value)}
          />
          I'll provide my measurements
        </label>
        
        <label>
          <input
            type="radio"
            value="SEND_SAMPLE_SHIRT"
            checked={measurementOption === 'SEND_SAMPLE_SHIRT'}
            onChange={(e) => setMeasurementOption(e.target.value)}
          />
          I'll send a sample shirt
        </label>
      </div>

      {measurementOption === 'PROVIDE_MEASUREMENTS' && (
        <div className="measurements">
          <input
            type="number"
            placeholder="Chest (inches)"
            value={measurements.chest}
            onChange={(e) => setMeasurements({...measurements, chest: parseFloat(e.target.value)})}
            required
          />
          <input
            type="number"
            placeholder="Waist (inches)"
            value={measurements.waist}
            onChange={(e) => setMeasurements({...measurements, waist: parseFloat(e.target.value)})}
            required
          />
          {/* Add more measurement fields */}
        </div>
      )}

      {measurementOption === 'SEND_SAMPLE_SHIRT' && (
        <div className="shipping-info">
          <p>After placing your order, you'll receive a shipping label to send us your sample shirt.</p>
          <button type="button" onClick={async () => {
            const res = await fetch('/api/bespoke-orders/shipping-label');
            const data = await res.json();
            alert(data.shippingAddress);
          }}>
            View Shipping Address
          </button>
        </div>
      )}

      <textarea id="notes" placeholder="Additional notes"></textarea>
      
      <button type="submit">Place Custom Order</button>
    </form>
  );
}
```

---

## API Endpoints Summary

### Color Variants
- `POST /api/products/{id}/variants` - Add color variant
- `PUT /api/products/{id}/variants/{variantId}` - Update variant
- `DELETE /api/products/{id}/variants/{variantId}` - Delete variant
- `GET /api/products/{id}/variants` - Get all variants

### Image Management
- `DELETE /api/products/{id}/images?imageUrl=...` - Delete product image
- `DELETE /api/products/{id}/variants/{variantId}/images?imageUrl=...` - Delete variant image
- `POST /api/products/{id}/images` - Add product images
- `POST /api/products/{id}/variants/{variantId}/images` - Add variant images

### Bespoke Orders
- `POST /api/bespoke-orders` - Create order
- `PUT /api/bespoke-orders/{id}` - Update order
- `PATCH /api/bespoke-orders/{id}/status` - Update status (Admin)
- `PATCH /api/bespoke-orders/{id}/tracking` - Add tracking ID
- `GET /api/bespoke-orders/my-orders` - Get user's orders
- `GET /api/bespoke-orders` - Get all orders (Admin)
- `GET /api/bespoke-orders/shipping-label` - Get shipping address
- `DELETE /api/bespoke-orders/{id}` - Delete order (Admin)

---

## Testing

Run the application and test with Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

Look for:
- "Product Controller" - Color variants and image management
- "Bespoke Orders" - Made-to-measure functionality

---

## Need Help?

See the full documentation: `PRODUCT_ENHANCEMENTS.md`
