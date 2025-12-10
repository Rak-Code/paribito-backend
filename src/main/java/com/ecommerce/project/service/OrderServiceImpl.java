package com.ecommerce.project.service;

import com.ecommerce.project.dto.OrderRequestDTO;
import com.ecommerce.project.dto.OrderResponseDTO;
import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.exception.BadRequestException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.exception.UnauthorizedException;
import com.ecommerce.project.repository.OrderRepository;
import com.ecommerce.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {

        Order order = new Order();
        order.setUserId(dto.userId());
        order.setAddress(dto.address());
        order.setTotalAmount(dto.totalAmount());
        order.setItems(dto.items());
        order.setStatus(Order.Status.pending);

        Order saved = orderRepository.save(order);

        // Send emails asynchronously
        try {
            User user = userRepository.findById(dto.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", dto.userId()));
            
            // Send confirmation email to customer
            emailService.sendOrderConfirmationToCustomer(saved, user);
            
            // Send notification email to admin
            emailService.sendOrderNotificationToAdmin(saved, user);
            
            log.info("Order emails triggered for order: {}", saved.getId());
        } catch (Exception e) {
            log.error("Failed to send order emails for order: {}", saved.getId(), e);
            // Don't fail the order creation if email fails
        }

        return toDTO(saved);
    }

    @Override
    public OrderResponseDTO getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    @Override
    public List<OrderResponseDTO> getUserOrders(String userId) {
        return orderRepository.findByUserId(userId)
                .stream().map(this::toDTO)
                .toList();
    }

    @Override
    public Page<OrderResponseDTO> getUserOrders(String userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(this::toDTO);
    }

    @Override
    public OrderResponseDTO updateOrderStatus(String orderId, String status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        order.setStatus(Order.Status.valueOf(status.toLowerCase()));

        return toDTO(orderRepository.save(order));
    }

    @Override
    public void cancelOrder(String orderId, String userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUserId().equals(userId)) {
            throw new UnauthorizedException("Order does not belong to user");
        }

        if (order.getStatus() != Order.Status.pending && order.getStatus() != Order.Status.processing) {
            throw new BadRequestException("Order cannot be cancelled at this stage");
        }

        order.setStatus(Order.Status.cancelled);
        orderRepository.save(order);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream().map(this::toDTO).toList();
    }

    @Override
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::toDTO);
    }

    private OrderResponseDTO toDTO(Order o) {
        return new OrderResponseDTO(
                o.getId(),
                o.getUserId(),
                o.getAddress(),
                o.getTotalAmount(),
                o.getStatus(),
                o.getOrderDate(),
                o.getItems()
        );
    }
}
