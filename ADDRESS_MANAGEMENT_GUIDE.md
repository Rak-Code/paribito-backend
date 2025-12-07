# Address Management API Guide

## Overview
Complete address management system for handling multiple addresses per user in the e-commerce application.

## Files Created

### Entity
- **User.java** - Already contains embedded `Address` model (no changes needed)

### DTOs
- **AddressRequestDTO.java** - Request payload for creating/updating addresses
- **AddressResponseDTO.java** - Response payload for address data

### Service Layer
- **AddressService.java** - Service interface
- **AddressServiceImpl.java** - Service implementation

### Controller
- **AddressController.java** - REST API endpoints

## API Endpoints

All endpoints require authentication (Bearer token).

### 1. Add New Address
```http
POST /api/addresses
Authorization: Bearer <token>
Content-Type: application/json

{
  "addressLine": "123 Main Street, Apt 4B",
  "city": "New York",
  "state": "NY",
  "postalCode": "10001",
  "country": "USA",
  "isDefault": true
}
```

### 2. Get All Addresses
```http
GET /api/addresses
Authorization: Bearer <token>
```

### 3. Get Address by ID
```http
GET /api/addresses/{addressId}
Authorization: Bearer <token>
```

### 4. Update Address
```http
PUT /api/addresses/{addressId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "addressLine": "456 Oak Avenue",
  "city": "Los Angeles",
  "state": "CA",
  "postalCode": "90001",
  "country": "USA",
  "isDefault": false
}
```

### 5. Delete Address
```http
DELETE /api/addresses/{addressId}
Authorization: Bearer <token>
```

### 6. Set Default Address
```http
PATCH /api/addresses/{addressId}/set-default
Authorization: Bearer <token>
```

## Features

✅ **Multiple Addresses per User** - Users can store multiple delivery addresses
✅ **Default Address** - Mark one address as default for quick checkout
✅ **Full CRUD Operations** - Create, Read, Update, Delete addresses
✅ **User Isolation** - Each user can only access their own addresses
✅ **Validation** - All required fields are validated
✅ **Auto-generated IDs** - Each address gets a unique UUID
✅ **Swagger Documentation** - All endpoints documented in Swagger UI

## Testing with Postman

1. **Login** to get authentication token
2. **Add Address** - POST to `/api/addresses`
3. **View Addresses** - GET from `/api/addresses`
4. **Update Address** - PUT to `/api/addresses/{addressId}`
5. **Set Default** - PATCH to `/api/addresses/{addressId}/set-default`
6. **Delete Address** - DELETE to `/api/addresses/{addressId}`

## Integration with Orders

The Order entity already has an embedded Address model that captures a snapshot of the address at order time. When creating an order, you can:

1. Fetch user's default address from `/api/addresses`
2. Use that address data when creating the order
3. The order stores a snapshot, so address changes won't affect past orders

## Security

- All endpoints require JWT authentication
- Users can only manage their own addresses
- UserPrincipal automatically provides the authenticated user's ID

## Next Steps

Consider adding:
- Address validation service (verify postal codes, etc.)
- Geocoding integration for address verification
- Address type labels (Home, Work, Other)
- Nickname field for addresses
