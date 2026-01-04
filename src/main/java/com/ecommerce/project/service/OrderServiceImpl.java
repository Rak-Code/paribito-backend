package com.ecommerce.project.service;

import com.ecommerce.project.dto.OrderRequestDTO;
import com.ecommerce.project.dto.OrderResponseDTO;
import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.Payment;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.exception.BadRequestException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.exception.UnauthorizedException;
import com.ecommerce.project.repository.OrderRepository;
import com.ecommerce.project.repository.PaymentRepository;
import com.ecommerce.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final InvoiceService invoiceService;
    private final PaymentRepository paymentRepository;

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {

        Order order = new Order();
        order.setUserId(dto.userId());
        order.setAddress(dto.address());
        order.setTotalAmount(dto.totalAmount());
        order.setItems(dto.items());
        order.setStatus(Order.Status.pending);

        Order saved = orderRepository.save(order);

        // NOTE: Email notifications are NOT sent here.
        // Order confirmation and admin notification emails are triggered
        // only after payment verification in RazorpayServiceImpl.
        // This ensures customers don't receive premature notifications
        // before payment is confirmed. (Requirements 2.2, 11.2)

        log.info("Order created with id: {} - awaiting payment verification for email notifications", saved.getId());

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

        Order.Status newStatus = Order.Status.valueOf(status.toLowerCase());
        Order.Status oldStatus = order.getStatus();
        order.setStatus(newStatus);

        Order savedOrder = orderRepository.save(order);

        // Generate and send invoice when order is delivered
        if (newStatus == Order.Status.delivered && oldStatus != Order.Status.delivered) {
            try {
                User user = userRepository.findById(order.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", order.getUserId()));
                
                // Generate invoice (this will be done asynchronously in a separate thread)
                generateAndSendInvoice(savedOrder, user);
                
                log.info("Invoice generation triggered for delivered order: {}", orderId);
            } catch (Exception e) {
                log.error("Failed to trigger invoice generation for order: {}", orderId, e);
                // Don't fail the status update if invoice generation fails
            }
        }

        return toDTO(savedOrder);
    }

    @Async
    private void generateAndSendInvoice(Order order, User user) {
        try {
            log.info("Async invoice generation started for order: {}", order.getId());
            
            // Generate invoice
            com.ecommerce.project.entity.Invoice invoice = invoiceService.generateInvoice(order, user);
            
            // Download PDF data
            byte[] pdfData = invoiceService.downloadInvoicePdf(invoice.getId());
            
            // Send emails with invoice attachment
            emailService.sendInvoiceToCustomer(invoice, user, pdfData);
            emailService.sendInvoiceToAdmin(invoice, user, pdfData);
            
            log.info("Invoice generated and emailed successfully for order: {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to generate and send invoice for order: {}", order.getId(), e);
        }
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
        Order savedOrder = orderRepository.save(order);

        // Send cancellation emails to customer and admin (Requirements 4.1, 5.1)
        try {
            User user = userRepository.findById(order.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", order.getUserId()));
            
            // Send cancellation email to customer
            emailService.sendOrderCancellationToCustomer(savedOrder, user);
            
            // Send cancellation notification to admin
            emailService.sendOrderCancellationToAdmin(savedOrder, user);
            
            log.info("Cancellation emails triggered for order: {}", orderId);
        } catch (Exception e) {
            log.error("Failed to send cancellation emails for order: {}", orderId, e);
            // Don't fail the cancellation if email fails
        }
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        // FIXED: Only return orders with successful payments
        // This prevents unpaid/abandoned checkout attempts from appearing in admin panel
        // Business rule: An order should only be visible to admin after payment is confirmed
        
        // Get all order IDs that have successful payments
        List<String> paidOrderIds = paymentRepository.findCompletedPayments()
                .stream()
                .map(Payment::getOrderId)
                .toList();
        
        // Return only orders with successful payments
        return orderRepository.findByIdIn(paidOrderIds)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        // FIXED: Only return orders with successful payments (paginated version)
        // This prevents unpaid/abandoned checkout attempts from appearing in admin panel
        
        // Get all order IDs that have successful payments
        List<String> paidOrderIds = paymentRepository.findCompletedPayments()
                .stream()
                .map(Payment::getOrderId)
                .toList();
        
        // Return only orders with successful payments (paginated)
        return orderRepository.findByIdIn(paidOrderIds, pageable)
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
