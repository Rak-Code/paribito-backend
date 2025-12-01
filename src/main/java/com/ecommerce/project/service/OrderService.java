package com.ecommerce.project.service;

import com.ecommerce.project.dto.OrderRequestDTO;
import com.ecommerce.project.dto.OrderResponseDTO;
import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(OrderRequestDTO dto);

    OrderResponseDTO getOrder(String orderId);

    List<OrderResponseDTO> getUserOrders(String userId);

    OrderResponseDTO updateOrderStatus(String orderId, String status);

    void cancelOrder(String orderId, String userId);

    List<OrderResponseDTO> getAllOrders();
}
