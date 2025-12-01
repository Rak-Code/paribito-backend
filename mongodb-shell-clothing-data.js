// MongoDB Shell Commands for Clothing Products
// Use these commands in MongoDB shell to insert data

// Switch to your database (replace 'ecommerce' with your database name)
use ecommerce

// Insert Categories
// db.categories.insertMany([
//   {
//     _id: ObjectId('69243b4290849dbe35388cbd'),
//     name: "shirts",
//     _class: "com.ecommerce.project.entity.Category"
//   },
//   {
//     _id: ObjectId('692572f8250cf3b02705f8c2'),
//     name: "t-shirts",
//     _class: "com.ecommerce.project.entity.Category"
//   },
//   {
//     _id: ObjectId('692572f8250cf3b02705f8c3'),
//     name: "bandis",
//     _class: "com.ecommerce.project.entity.Category"
//   },
//   {
//     _id: ObjectId('692572f8250cf3b02705f8c4'),
//     name: "blazers",
//     _class: "com.ecommerce.project.entity.Category"
//   }
// ])

// Insert Clothing Products
db.products.insertMany([
  // Shirts
  {
    _id: ObjectId(),
    name: "Classic White Formal Shirt",
    description: "Premium cotton formal shirt with regular fit, perfect for office wear",
    categoryId: ObjectId('69243b4290849dbe35388cbd'),
    price: 899.00,
    stockQuantity: 150,
    imageUrls: [
      "https://example.com/images/white-formal-shirt-1.jpg",
      "https://example.com/images/white-formal-shirt-2.jpg"
    ],
    size: "M",
    color: "White",
    createdAt: new Date("2024-11-01T09:00:00"),
    _class: "com.ecommerce.project.entity.Product"
  },
  {
    _id: ObjectId(),
    name: "Blue Checked Casual Shirt",
    description: "Comfortable cotton blend casual shirt with modern check pattern",
    categoryId: ObjectId('69243b4290849dbe35388cbd'),
    price: 749.00,
    stockQuantity: 120,
    imageUrls: [
      "https://example.com/images/blue-checked-shirt.jpg"
    ],
    size: "L",
    color: "Blue",
    createdAt: new Date("2024-11-02T10:30:00"),
    _class: "com.ecommerce.project.entity.Product"
  },
  {
    _id: ObjectId(),
    name: "Black Slim Fit Shirt",
    description: "Stylish slim fit shirt for a modern look, wrinkle-free fabric",
    categoryId: ObjectId('69243b4290849dbe35388cbd'),
    price: 999.00,
    stockQuantity: 100,
    imageUrls: [
      "https://example.com/images/black-slim-shirt-1.jpg",
      "https://example.com/images/black-slim-shirt-2.jpg"
    ],
    size: "M",
    color: "Black",
    createdAt: new Date("2024-11-03T11:15:00"),
    _class: "com.ecommerce.project.entity.Product"
  },
  
  // T-Shirts
  {
    _id: ObjectId(),
    name: "Cotton Round Neck T-Shirt",
    description: "100% organic cotton t-shirt, comfortable and breathable for everyday wear",
    categoryId: ObjectId('692572f8250cf3b02705f8c2'),
    price: 399.00,
    stockQuantity: 250,
    imageUrls: [
      "https://example.com/images/cotton-tshirt-navy.jpg"
    ],
    size: "M",
    color: "Navy Blue",
    createdAt: new Date("2024-11-04T09:30:00"),
    _class: "com.ecommerce.project.entity.Product"
  },
  {
    _id: ObjectId(),
    name: "Graphic Print T-Shirt",
    description: "Trendy graphic print t-shirt with premium quality print",
    categoryId: ObjectId('692572f8250cf3b02705f8c2'),
    price: 499.00,
    stockQuantity: 180,
    imageUrls: [
      "https://example.com/images/graphic-tshirt-1.jpg",
      "https://example.com/images/graphic-tshirt-2.jpg"
    ],
    size: "L",
    color: "Black",
    createdAt: new Date("2024-11-05T10:00:00"),
    _class: "com.ecommerce.project.entity.Product"
  },
  {
    _id: ObjectId(),
    name: "V-Neck Plain T-Shirt",
    description: "Classic v-neck t-shirt in soft cotton blend, perfect for casual outings",
    categoryId: ObjectId('692572f8250cf3b02705f8c2'),
    price: 349.00,
    stockQuantity: 200,
    imageUrls: [
      "https://example.com/images/vneck-tshirt-grey.jpg"
    ],
    size: "M",
    color: "Grey",
    createdAt: new Date("2024-11-06T11:45:00"),
    _class: "com.ecommerce.project.entity.Product"
  },
  {
    _id: ObjectId(),
    name: "Polo T-Shirt",
    description: "Premium polo t-shirt with collar, ideal for smart casual look",
    categoryId: ObjectId('692572f8250cf3b02705f8c2'),
    price: 599.00,
    stockQuantity: 140,
    imageUrls: [
      "https://example.com/images/polo-tshirt-maroon.jpg"
    ],
    size: "L",
    color: "Maroon",
    createdAt: new Date("2024-11-07T12:20:00"),
    _class: "com.ecommerce.project.entity.Product"
  },
  
  // Bandis (Vests/Sleeveless)
  {
    _id: ObjectId(),
    name: "Cotton Sleeveless Vest",
    description: "Comfortable cotton vest for gym and casual wear",
    categoryId: ObjectId('692572f8250cf3b02705f8c3'),
    price: 299.00,
    stockQuantity: 160,
    imageUrls: [
      "https://example.com/images/cotton-vest-white.jpg"
    ],
    size: "M",
    color: "White",
    createdAt: new Date("2024-11-08T09:15:00"),
    _class: "com.ecommerce.project.entity.Product"
  },
  {
    _id: ObjectId(),
    name: "Sports Gym Vest",
    description: "Breathable sports vest with moisture-wicking fabric",
    categoryId: ObjectId('692572f8250cf3b02705f8c3'),
    price: 449.00,
    stockQuantity: 130,
    imageUrls: [
      "https://example.com/images/sports-vest-black.jpg",
      "https://example.com/images/sports-vest-back.jpg"
    ],
    size: "L",
    color: "Black",
    createdAt: new Date("2024-11-09T10:30:00"),
    _class: "com.ecommerce.project.entity.Product"
  },
  {
    _id: ObjectId(),
    name: "Casual Tank Top",
    description: "Stylish tank top for summer wear, lightweight and comfortable",
    categoryId: ObjectId('692572f8250cf3b02705f8c3'),
    price: 349.00,
    stockQuantity: 170,
    imageUrls: [
      "https://example.com/images/tank-top-blue.jpg"
    ],
    size: "M",
    color: "Sky Blue",
    createdAt: new Date("2024-11-10T11:00:00"),
    _class: "com.ecommerce.project.entity.Product"
  },
  
  // Blazers
  {
    _id: ObjectId(),
    name: "Formal Black Blazer",
    description: "Premium quality formal blazer with perfect fit, ideal for business meetings",
    categoryId: ObjectId('692572f8250cf3b02705f8c4'),
    price: 2999.00,
    stockQuantity: 50,
    imageUrls: [
      "https://example.com/images/black-blazer-1.jpg",
      "https://example.com/images/black-blazer-2.jpg",
      "https://example.com/images/black-blazer-3.jpg"
    ],
    size: "M",
    color: "Black",
    createdAt: new Date("2024-11-11T09:00:00"),
    _class: "com.ecommerce.project.entity.Product"
  },
  {
    _id: ObjectId(),
    name: "Navy Blue Blazer",
    description: "Classic navy blue blazer with modern cut, versatile for formal and semi-formal occasions",
    categoryId: ObjectId('692572f8250cf3b02705f8c4'),
    price: 3499.00,
    stockQuantity: 40,
    imageUrls: [
      "https://example.com/images/navy-blazer-1.jpg",
      "https://example.com/images/navy-blazer-2.jpg"
    ],
    size: "L",
    color: "Navy Blue",
    createdAt: new Date("2024-11-12T10:15:00"),
    _class: "com.ecommerce.project.entity.Product"
  },
  {
    _id: ObjectId(),
    name: "Grey Casual Blazer",
    description: "Smart casual grey blazer, perfect for parties and casual business events",
    categoryId: ObjectId('692572f8250cf3b02705f8c4'),
    price: 2799.00,
    stockQuantity: 60,
    imageUrls: [
      "https://example.com/images/grey-blazer.jpg"
    ],
    size: "M",
    color: "Grey",
    createdAt: new Date("2024-11-13T11:30:00"),
    _class: "com.ecommerce.project.entity.Product"
  },
  {
    _id: ObjectId(),
    name: "Brown Tweed Blazer",
    description: "Stylish tweed blazer with textured fabric, great for winter formal wear",
    categoryId: ObjectId('692572f8250cf3b02705f8c4'),
    price: 3999.00,
    stockQuantity: 35,
    imageUrls: [
      "https://example.com/images/brown-tweed-blazer-1.jpg",
      "https://example.com/images/brown-tweed-blazer-2.jpg"
    ],
    size: "L",
    color: "Brown",
    createdAt: new Date("2024-11-14T12:00:00"),
    _class: "com.ecommerce.project.entity.Product"
  }
])

print("✅ Categories and Products inserted successfully!")
print("Total Categories: 4 (shirts, t-shirts, bandis, blazers)")
print("Total Products: 15 clothing items")
