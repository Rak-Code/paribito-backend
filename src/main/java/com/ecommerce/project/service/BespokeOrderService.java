package com.ecommerce.project.service;

import com.ecommerce.project.dto.BespokeOrderRequestDTO;
import com.ecommerce.project.dto.BespokeOrderResponseDTO;
import com.ecommerce.project.entity.BespokeOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BespokeOrderService {
    
    BespokeOrderResponseDTO createBespokeOrder(String userId, BespokeOrderRequestDTO dto);
    
    BespokeOrderResponseDTO updateBespokeOrder(String orderId, BespokeOrderRequestDTO dto);
    
    BespokeOrderResponseDTO updateOrderStatus(String orderId, BespokeOrder.OrderStatus status);
    
    BespokeOrderResponseDTO addSampleTrackingId(String orderId, String trackingId);
    
    BespokeOrderResponseDTO getBespokeOrder(String orderId);
    
    List<BespokeOrderResponseDTO> getUserBespokeOrders(String userId);
    
    List<BespokeOrderResponseDTO> getBespokeOrdersByStatus(BespokeOrder.OrderStatus status);
    
    Page<BespokeOrderResponseDTO> getAllBespokeOrders(Pageable pageable);
    
    void deleteBespokeOrder(String orderId);
    
    String generateShippingLabel();
}
