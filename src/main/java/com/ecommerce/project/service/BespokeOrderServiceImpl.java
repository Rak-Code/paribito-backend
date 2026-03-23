package com.ecommerce.project.service;

import com.ecommerce.project.dto.BespokeOrderRequestDTO;
import com.ecommerce.project.dto.BespokeOrderResponseDTO;
import com.ecommerce.project.dto.CustomMeasurementDTO;
import com.ecommerce.project.entity.BespokeOrder;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.repository.BespokeOrderRepository;
import com.ecommerce.project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BespokeOrderServiceImpl implements BespokeOrderService {
    
    private final BespokeOrderRepository bespokeOrderRepository;
    private final ProductRepository productRepository;
    
    @Value("${bespoke.shipping.address:Paribito, 123 Fashion Street, Mumbai, Maharashtra 400001, India}")
    private String shippingAddress;
    
    @Override
    public BespokeOrderResponseDTO createBespokeOrder(String userId, BespokeOrderRequestDTO dto) {
        // Validate product exists and is bespoke type
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", dto.productId()));
        
        if (product.getProductType() != Product.ProductType.BESPOKE && 
            product.getProductType() != Product.ProductType.MADE_TO_MEASURE) {
            throw new IllegalArgumentException("Product is not available for bespoke/made-to-measure orders");
        }
        
        BespokeOrder order = new BespokeOrder();
        order.setUserId(userId);
        order.setProductId(dto.productId());
        order.setSelectedColor(dto.selectedColor());
        order.setSelectedDesign(dto.selectedDesign());
        order.setMeasurementOption(dto.measurementOption());
        order.setCustomerNotes(dto.customerNotes());
        order.setPrice(product.getPrice()); // Base price, can be adjusted
        
        // Set measurements if provided
        if (dto.customMeasurements() != null) {
            order.setCustomMeasurements(dto.customMeasurements().toEntity());
            order.setStatus(BespokeOrder.OrderStatus.MEASUREMENTS_RECEIVED);
        } else if (dto.measurementOption() == BespokeOrder.MeasurementOption.SEND_SAMPLE_SHIRT) {
            order.setStatus(BespokeOrder.OrderStatus.PENDING_MEASUREMENTS);
        } else {
            order.setStatus(BespokeOrder.OrderStatus.PENDING_MEASUREMENTS);
        }
        
        BespokeOrder saved = bespokeOrderRepository.save(order);
        log.info("Created bespoke order {} for user {}", saved.getId(), userId);
        
        return toDTO(saved);
    }
    
    @Override
    public BespokeOrderResponseDTO updateBespokeOrder(String orderId, BespokeOrderRequestDTO dto) {
        BespokeOrder order = bespokeOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("BespokeOrder", "id", orderId));
        
        order.setSelectedColor(dto.selectedColor());
        order.setSelectedDesign(dto.selectedDesign());
        order.setMeasurementOption(dto.measurementOption());
        order.setCustomerNotes(dto.customerNotes());
        order.setUpdatedAt(LocalDateTime.now());
        
        if (dto.customMeasurements() != null) {
            order.setCustomMeasurements(dto.customMeasurements().toEntity());
            if (order.getStatus() == BespokeOrder.OrderStatus.PENDING_MEASUREMENTS) {
                order.setStatus(BespokeOrder.OrderStatus.MEASUREMENTS_RECEIVED);
            }
        }
        
        BespokeOrder updated = bespokeOrderRepository.save(order);
        log.info("Updated bespoke order {}", orderId);
        
        return toDTO(updated);
    }
    
    @Override
    public BespokeOrderResponseDTO updateOrderStatus(String orderId, BespokeOrder.OrderStatus status) {
        BespokeOrder order = bespokeOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("BespokeOrder", "id", orderId));
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        BespokeOrder updated = bespokeOrderRepository.save(order);
        log.info("Updated bespoke order {} status to {}", orderId, status);
        
        return toDTO(updated);
    }
    
    @Override
    public BespokeOrderResponseDTO addSampleTrackingId(String orderId, String trackingId) {
        BespokeOrder order = bespokeOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("BespokeOrder", "id", orderId));
        
        order.setSampleShippingTrackingId(trackingId);
        order.setStatus(BespokeOrder.OrderStatus.SAMPLE_RECEIVED);
        order.setUpdatedAt(LocalDateTime.now());
        
        BespokeOrder updated = bespokeOrderRepository.save(order);
        log.info("Added sample tracking ID {} to bespoke order {}", trackingId, orderId);
        
        return toDTO(updated);
    }
    
    @Override
    public BespokeOrderResponseDTO getBespokeOrder(String orderId) {
        BespokeOrder order = bespokeOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("BespokeOrder", "id", orderId));
        return toDTO(order);
    }
    
    @Override
    public List<BespokeOrderResponseDTO> getUserBespokeOrders(String userId) {
        return bespokeOrderRepository.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .toList();
    }
    
    @Override
    public List<BespokeOrderResponseDTO> getBespokeOrdersByStatus(BespokeOrder.OrderStatus status) {
        return bespokeOrderRepository.findByStatus(status)
                .stream()
                .map(this::toDTO)
                .toList();
    }
    
    @Override
    public Page<BespokeOrderResponseDTO> getAllBespokeOrders(Pageable pageable) {
        return bespokeOrderRepository.findAll(pageable)
                .map(this::toDTO);
    }
    
    @Override
    public void deleteBespokeOrder(String orderId) {
        bespokeOrderRepository.deleteById(orderId);
        log.info("Deleted bespoke order {}", orderId);
    }
    
    @Override
    public String generateShippingLabel() {
        return shippingAddress;
    }
    
    private BespokeOrderResponseDTO toDTO(BespokeOrder order) {
        return new BespokeOrderResponseDTO(
                order.getId(),
                order.getUserId(),
                order.getProductId(),
                order.getSelectedColor(),
                order.getSelectedDesign(),
                order.getMeasurementOption(),
                CustomMeasurementDTO.fromEntity(order.getCustomMeasurements()),
                order.getSampleShippingTrackingId(),
                order.getPrice(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getCustomerNotes()
        );
    }
}
