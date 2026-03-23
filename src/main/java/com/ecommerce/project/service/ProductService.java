package com.ecommerce.project.service;

import com.ecommerce.project.dto.ProductRequestDTO;
import com.ecommerce.project.dto.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO dto);

    ProductResponseDTO createProductWithImages(ProductRequestDTO dto, MultipartFile[] images);

    ProductResponseDTO updateProduct(String id, ProductRequestDTO dto);

    ProductResponseDTO updateProductWithImages(String id, ProductRequestDTO dto, MultipartFile[] images, boolean keepExistingImages);

    void deleteProduct(String id);

    List<ProductResponseDTO> getProductsByCategory(String categoryId);

    List<ProductResponseDTO> searchProducts(String keyword);

    ProductResponseDTO getProduct(String id);

    List<ProductResponseDTO> getAllProducts();

    Page<ProductResponseDTO> getAllProducts(Pageable pageable);

    // Color variant management
    ProductResponseDTO addColorVariant(String productId, String colorName, String colorCode, int stockQuantity, MultipartFile[] images);
    
    ProductResponseDTO updateColorVariant(String productId, String variantId, String colorName, String colorCode, Integer stockQuantity, MultipartFile[] images, boolean keepExistingImages);
    
    ProductResponseDTO deleteColorVariant(String productId, String variantId);
    
    List<Map<String, Object>> getColorVariants(String productId);
    
    // Individual image management
    ProductResponseDTO deleteProductImage(String productId, String imageUrl);
    
    ProductResponseDTO deleteVariantImage(String productId, String variantId, String imageUrl);
    
    ProductResponseDTO addProductImages(String productId, MultipartFile[] images);
    
    ProductResponseDTO addVariantImages(String productId, String variantId, MultipartFile[] images);
}