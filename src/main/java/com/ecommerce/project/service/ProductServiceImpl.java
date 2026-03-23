package com.ecommerce.project.service;

import com.ecommerce.project.dto.ProductRequestDTO;
import com.ecommerce.project.dto.ProductResponseDTO;
import com.ecommerce.project.dto.ProductVariantDTO;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.entity.ProductVariant;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// Redis caching disabled - imports commented out
// import org.springframework.cache.annotation.CacheEvict;
// import org.springframework.cache.annotation.Cacheable;
// import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ImageStorageService imageStorageService;

    /**
     * Create product (Redis caching disabled)
     */
    @Override
    // @Caching(evict = {
    //         @CacheEvict(value = "productsAll", allEntries = true),
    //         @CacheEvict(value = "productsPage", allEntries = true),
    //         @CacheEvict(value = "productsByCategory", allEntries = true),
    //         @CacheEvict(value = "productsBySearch", allEntries = true),
    //         @CacheEvict(value = "product", allEntries = true)
    // })
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {

        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setCategoryId(dto.categoryId());
        product.setPrice(dto.price());
        product.setSizeTierPricing(dto.sizeTierPricing());
        product.setAvailableSizes(dto.availableSizes());
        product.setStockQuantity(dto.stockQuantity());
        product.setImageUrls(dto.imageUrls());
        product.setColor(dto.color());
        product.setProductType(dto.productType() != null ? dto.productType() : Product.ProductType.REGULAR);
        product.setAvailableDesigns(dto.availableDesigns());

        Product saved = productRepository.save(product);
        log.info("Created product with ID: {}", saved.getId());

        return toDTO(saved);
    }

    /**
     * Create product with images (Redis caching disabled)
     */
    @Override
    // @Caching(evict = {
    //         @CacheEvict(value = "productsAll", allEntries = true),
    //         @CacheEvict(value = "productsPage", allEntries = true),
    //         @CacheEvict(value = "productsByCategory", allEntries = true),
    //         @CacheEvict(value = "productsBySearch", allEntries = true),
    //         @CacheEvict(value = "product", allEntries = true)
    // })
    public ProductResponseDTO createProductWithImages(ProductRequestDTO dto, MultipartFile[] images) {
        List<String> imageUrls = new ArrayList<>();
        
        // Upload images if provided
        if (images != null && images.length > 0) {
            try {
                List<MultipartFile> imageList = Arrays.asList(images);
                imageUrls = imageStorageService.uploadImages(imageList, "products");
                log.info("Uploaded {} images for new product", imageUrls.size());
            } catch (Exception e) {
                log.error("Failed to upload images: {}", e.getMessage());
                throw new RuntimeException("Failed to upload product images: " + e.getMessage());
            }
        }
        
        // Create product with uploaded image URLs
        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setCategoryId(dto.categoryId());
        product.setPrice(dto.price());
        product.setSizeTierPricing(dto.sizeTierPricing());
        product.setAvailableSizes(dto.availableSizes());
        product.setStockQuantity(dto.stockQuantity());
        product.setImageUrls(imageUrls);
        product.setColor(dto.color());
        product.setProductType(dto.productType() != null ? dto.productType() : Product.ProductType.REGULAR);
        product.setAvailableDesigns(dto.availableDesigns());

        Product saved = productRepository.save(product);
        log.info("Created product with ID: {} and {} images", saved.getId(), imageUrls.size());

        return toDTO(saved);
    }

    /**
     * Update product (Redis caching disabled)
     */
    @Override
    // @Caching(evict = {
    //         @CacheEvict(value = "product", key = "#id"),
    //         @CacheEvict(value = "productsAll", allEntries = true),
    //         @CacheEvict(value = "productsPage", allEntries = true),
    //         @CacheEvict(value = "productsByCategory", allEntries = true),
    //         @CacheEvict(value = "productsBySearch", allEntries = true)
    // })
    public ProductResponseDTO updateProduct(String id, ProductRequestDTO dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setCategoryId(dto.categoryId());
        product.setPrice(dto.price());
        product.setSizeTierPricing(dto.sizeTierPricing());
        product.setAvailableSizes(dto.availableSizes());
        product.setStockQuantity(dto.stockQuantity());
        product.setImageUrls(dto.imageUrls());
        product.setColor(dto.color());
        product.setProductType(dto.productType() != null ? dto.productType() : product.getProductType());
        product.setAvailableDesigns(dto.availableDesigns());

        Product updated = productRepository.save(product);
        log.info("Updated product {}", id);

        return toDTO(updated);
    }

    /**
     * Update product with images (Redis caching disabled)
     */
    @Override
    // @Caching(evict = {
    //         @CacheEvict(value = "product", key = "#id"),
    //         @CacheEvict(value = "productsAll", allEntries = true),
    //         @CacheEvict(value = "productsPage", allEntries = true),
    //         @CacheEvict(value = "productsByCategory", allEntries = true),
    //         @CacheEvict(value = "productsBySearch", allEntries = true)
    // })
    public ProductResponseDTO updateProductWithImages(String id, ProductRequestDTO dto, MultipartFile[] images, boolean keepExistingImages) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        List<String> imageUrls = new ArrayList<>();
        
        // Keep existing images if requested
        if (keepExistingImages && product.getImageUrls() != null) {
            imageUrls.addAll(product.getImageUrls());
        } else if (!keepExistingImages && product.getImageUrls() != null) {
            // Delete old images from R2
            try {
                imageStorageService.deleteImages(product.getImageUrls());
                log.info("Deleted {} old images for product {}", product.getImageUrls().size(), id);
            } catch (Exception e) {
                log.warn("Failed to delete old images: {}", e.getMessage());
            }
        }
        
        // Upload new images if provided
        if (images != null && images.length > 0) {
            try {
                List<MultipartFile> imageList = Arrays.asList(images);
                List<String> newImageUrls = imageStorageService.uploadImages(imageList, "products");
                imageUrls.addAll(newImageUrls);
                log.info("Uploaded {} new images for product {}", newImageUrls.size(), id);
            } catch (Exception e) {
                log.error("Failed to upload new images: {}", e.getMessage());
                throw new RuntimeException("Failed to upload product images: " + e.getMessage());
            }
        }

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setCategoryId(dto.categoryId());
        product.setPrice(dto.price());
        product.setSizeTierPricing(dto.sizeTierPricing());
        product.setAvailableSizes(dto.availableSizes());
        product.setStockQuantity(dto.stockQuantity());
        product.setImageUrls(imageUrls);
        product.setColor(dto.color());
        product.setProductType(dto.productType() != null ? dto.productType() : product.getProductType());
        product.setAvailableDesigns(dto.availableDesigns());

        Product updated = productRepository.save(product);
        log.info("Updated product {} with {} total images", id, imageUrls.size());

        return toDTO(updated);
    }

    /**
     * Delete product (Redis caching disabled)
     */
    @Override
    // @Caching(evict = {
    //         @CacheEvict(value = "product", key = "#id"),
    //         @CacheEvict(value = "productsAll", allEntries = true),
    //         @CacheEvict(value = "productsPage", allEntries = true),
    //         @CacheEvict(value = "productsByCategory", allEntries = true),
    //         @CacheEvict(value = "productsBySearch", allEntries = true)
    // })
    public void deleteProduct(String id) {
        // Get product to delete its images
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        // Delete images from R2 storage
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            try {
                imageStorageService.deleteImages(product.getImageUrls());
                log.info("Deleted {} images for product {}", product.getImageUrls().size(), id);
            } catch (Exception e) {
                log.warn("Failed to delete images for product {}: {}", id, e.getMessage());
            }
        }
        
        productRepository.deleteById(id);
        log.info("Deleted product {}", id);
    }

    /**
     * Get products by category (Redis caching disabled)
     */
    @Override
    // @Cacheable(cacheNames = "productsByCategory", key = "#categoryId")
    public List<ProductResponseDTO> getProductsByCategory(String categoryId) {
        log.info("Fetching products for category: {}", categoryId);
        return productRepository.findByCategoryId(categoryId)
                .stream().map(this::toDTO).toList();
    }

    /**
     * Search products by keyword (Redis caching disabled)
     */
    @Override
    // @Cacheable(cacheNames = "productsBySearch", key = "#keyword")
    public List<ProductResponseDTO> searchProducts(String keyword) {
        log.info("Searching products with keyword: {}", keyword);
        return productRepository.findByNameContainingIgnoreCase(keyword)
                .stream().map(this::toDTO).toList();
    }

    /**
     * Get single product by id (Redis caching disabled)
     */
    @Override
    // @Cacheable(cacheNames = "product", key = "#id")
    public ProductResponseDTO getProduct(String id) {
        log.info("Fetching product with ID: {}", id);
        return productRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    /**
     * Get all products (Redis caching disabled)
     */
    @Override
    // @Cacheable(cacheNames = "productsAll")
    public List<ProductResponseDTO> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Get all products with pagination (Redis caching disabled)
     */
    @Override
    // @Cacheable(cacheNames = "productsPage", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + (#pageable.sort != null ? #pageable.sort.toString() : '')")
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        log.info("Fetching products with pagination - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(this::toDTO);
    }

    private ProductResponseDTO toDTO(Product p) {
        List<ProductVariantDTO> variantDTOs = null;
        if (p.getColorVariants() != null) {
            variantDTOs = p.getColorVariants().stream()
                    .map(v -> new ProductVariantDTO(
                            v.getVariantId(),
                            v.getColorName(),
                            v.getColorCode(),
                            v.getImageUrls(),
                            v.getStockQuantity()
                    ))
                    .toList();
        }
        
        return new ProductResponseDTO(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getSizeTierPricing(),
                p.getAvailableSizes(),
                p.getStockQuantity(),
                p.getCategoryId(),
                p.getColor(),
                p.getImageUrls(),
                variantDTOs,
                p.getProductType(),
                p.getAvailableDesigns()
        );
    }

    // ========== COLOR VARIANT MANAGEMENT ==========
    
    @Override
    public ProductResponseDTO addColorVariant(String productId, String colorName, String colorCode, int stockQuantity, MultipartFile[] images) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        // Initialize colorVariants list if null
        if (product.getColorVariants() == null) {
            product.setColorVariants(new ArrayList<>());
        }
        
        // Create new variant
        ProductVariant variant = new ProductVariant();
        variant.setVariantId(java.util.UUID.randomUUID().toString());
        variant.setColorName(colorName);
        variant.setColorCode(colorCode);
        variant.setStockQuantity(stockQuantity);
        
        // Upload images for this variant
        List<String> imageUrls = new ArrayList<>();
        if (images != null && images.length > 0) {
            try {
                List<MultipartFile> imageList = Arrays.asList(images);
                imageUrls = imageStorageService.uploadImages(imageList, "products/variants");
                log.info("Uploaded {} images for variant {}", imageUrls.size(), variant.getVariantId());
            } catch (Exception e) {
                log.error("Failed to upload variant images: {}", e.getMessage());
                throw new RuntimeException("Failed to upload variant images: " + e.getMessage());
            }
        }
        variant.setImageUrls(imageUrls);
        
        product.getColorVariants().add(variant);
        Product updated = productRepository.save(product);
        log.info("Added color variant {} to product {}", variant.getVariantId(), productId);
        
        return toDTO(updated);
    }
    
    @Override
    public ProductResponseDTO updateColorVariant(String productId, String variantId, String colorName, 
                                                  String colorCode, Integer stockQuantity, 
                                                  MultipartFile[] images, boolean keepExistingImages) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        if (product.getColorVariants() == null) {
            throw new ResourceNotFoundException("ColorVariant", "variantId", variantId);
        }
        
        ProductVariant variant = product.getColorVariants().stream()
                .filter(v -> v.getVariantId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("ColorVariant", "variantId", variantId));
        
        // Update variant properties
        if (colorName != null) variant.setColorName(colorName);
        if (colorCode != null) variant.setColorCode(colorCode);
        if (stockQuantity != null) variant.setStockQuantity(stockQuantity);
        
        // Handle images
        List<String> imageUrls = new ArrayList<>();
        if (keepExistingImages && variant.getImageUrls() != null) {
            imageUrls.addAll(variant.getImageUrls());
        } else if (!keepExistingImages && variant.getImageUrls() != null) {
            // Delete old images
            try {
                imageStorageService.deleteImages(variant.getImageUrls());
                log.info("Deleted {} old images for variant {}", variant.getImageUrls().size(), variantId);
            } catch (Exception e) {
                log.warn("Failed to delete old variant images: {}", e.getMessage());
            }
        }
        
        // Upload new images
        if (images != null && images.length > 0) {
            try {
                List<MultipartFile> imageList = Arrays.asList(images);
                List<String> newImageUrls = imageStorageService.uploadImages(imageList, "products/variants");
                imageUrls.addAll(newImageUrls);
                log.info("Uploaded {} new images for variant {}", newImageUrls.size(), variantId);
            } catch (Exception e) {
                log.error("Failed to upload new variant images: {}", e.getMessage());
                throw new RuntimeException("Failed to upload variant images: " + e.getMessage());
            }
        }
        
        variant.setImageUrls(imageUrls);
        Product updated = productRepository.save(product);
        log.info("Updated color variant {} for product {}", variantId, productId);
        
        return toDTO(updated);
    }
    
    @Override
    public ProductResponseDTO deleteColorVariant(String productId, String variantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        if (product.getColorVariants() == null) {
            throw new ResourceNotFoundException("ColorVariant", "variantId", variantId);
        }
        
        ProductVariant variant = product.getColorVariants().stream()
                .filter(v -> v.getVariantId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("ColorVariant", "variantId", variantId));
        
        // Delete variant images from storage
        if (variant.getImageUrls() != null && !variant.getImageUrls().isEmpty()) {
            try {
                imageStorageService.deleteImages(variant.getImageUrls());
                log.info("Deleted {} images for variant {}", variant.getImageUrls().size(), variantId);
            } catch (Exception e) {
                log.warn("Failed to delete variant images: {}", e.getMessage());
            }
        }
        
        product.getColorVariants().remove(variant);
        Product updated = productRepository.save(product);
        log.info("Deleted color variant {} from product {}", variantId, productId);
        
        return toDTO(updated);
    }
    
    @Override
    public List<Map<String, Object>> getColorVariants(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        if (product.getColorVariants() == null) {
            return new ArrayList<>();
        }
        
        return product.getColorVariants().stream()
                .map(variant -> {
                    Map<String, Object> variantMap = new HashMap<>();
                    variantMap.put("variantId", variant.getVariantId());
                    variantMap.put("colorName", variant.getColorName());
                    variantMap.put("colorCode", variant.getColorCode());
                    variantMap.put("imageUrls", variant.getImageUrls());
                    variantMap.put("stockQuantity", variant.getStockQuantity());
                    return variantMap;
                })
                .toList();
    }
    
    // ========== INDIVIDUAL IMAGE MANAGEMENT ==========
    
    @Override
    public ProductResponseDTO deleteProductImage(String productId, String imageUrl) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        if (product.getImageUrls() == null || !product.getImageUrls().contains(imageUrl)) {
            throw new IllegalArgumentException("Image URL not found in product");
        }
        
        // Delete from storage
        try {
            imageStorageService.deleteImage(imageUrl);
            log.info("Deleted image {} from storage", imageUrl);
        } catch (Exception e) {
            log.warn("Failed to delete image from storage: {}", e.getMessage());
        }
        
        // Remove from product
        product.getImageUrls().remove(imageUrl);
        Product updated = productRepository.save(product);
        log.info("Removed image from product {}", productId);
        
        return toDTO(updated);
    }
    
    @Override
    public ProductResponseDTO deleteVariantImage(String productId, String variantId, String imageUrl) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        if (product.getColorVariants() == null) {
            throw new ResourceNotFoundException("ColorVariant", "variantId", variantId);
        }
        
        ProductVariant variant = product.getColorVariants().stream()
                .filter(v -> v.getVariantId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("ColorVariant", "variantId", variantId));
        
        if (variant.getImageUrls() == null || !variant.getImageUrls().contains(imageUrl)) {
            throw new IllegalArgumentException("Image URL not found in variant");
        }
        
        // Delete from storage
        try {
            imageStorageService.deleteImage(imageUrl);
            log.info("Deleted image {} from storage", imageUrl);
        } catch (Exception e) {
            log.warn("Failed to delete image from storage: {}", e.getMessage());
        }
        
        // Remove from variant
        variant.getImageUrls().remove(imageUrl);
        Product updated = productRepository.save(product);
        log.info("Removed image from variant {} of product {}", variantId, productId);
        
        return toDTO(updated);
    }
    
    @Override
    public ProductResponseDTO addProductImages(String productId, MultipartFile[] images) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        if (product.getImageUrls() == null) {
            product.setImageUrls(new ArrayList<>());
        }
        
        // Upload new images
        if (images != null && images.length > 0) {
            try {
                List<MultipartFile> imageList = Arrays.asList(images);
                List<String> newImageUrls = imageStorageService.uploadImages(imageList, "products");
                product.getImageUrls().addAll(newImageUrls);
                log.info("Added {} images to product {}", newImageUrls.size(), productId);
            } catch (Exception e) {
                log.error("Failed to upload images: {}", e.getMessage());
                throw new RuntimeException("Failed to upload images: " + e.getMessage());
            }
        }
        
        Product updated = productRepository.save(product);
        return toDTO(updated);
    }
    
    @Override
    public ProductResponseDTO addVariantImages(String productId, String variantId, MultipartFile[] images) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        if (product.getColorVariants() == null) {
            throw new ResourceNotFoundException("ColorVariant", "variantId", variantId);
        }
        
        ProductVariant variant = product.getColorVariants().stream()
                .filter(v -> v.getVariantId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("ColorVariant", "variantId", variantId));
        
        if (variant.getImageUrls() == null) {
            variant.setImageUrls(new ArrayList<>());
        }
        
        // Upload new images
        if (images != null && images.length > 0) {
            try {
                List<MultipartFile> imageList = Arrays.asList(images);
                List<String> newImageUrls = imageStorageService.uploadImages(imageList, "products/variants");
                variant.getImageUrls().addAll(newImageUrls);
                log.info("Added {} images to variant {} of product {}", newImageUrls.size(), variantId, productId);
            } catch (Exception e) {
                log.error("Failed to upload variant images: {}", e.getMessage());
                throw new RuntimeException("Failed to upload variant images: " + e.getMessage());
            }
        }
        
        Product updated = productRepository.save(product);
        return toDTO(updated);
    }
}