package com.ecommerce.project.service;

import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.from}")
    private String fromEmail;

    @Value("${email.admin}")
    private String adminEmail;

    @Override
    @Async
    public void sendOrderConfirmationToCustomer(Order order, User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Order Confirmation - Order #" + order.getId());
            message.setText(buildCustomerEmailContent(order, user));

            mailSender.send(message);
            log.info("Order confirmation email sent to customer: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send order confirmation email to customer: {}", user.getEmail(), e);
        }
    }

    @Override
    @Async
    public void sendOrderNotificationToAdmin(Order order, User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(adminEmail);
            message.setSubject("New Order Received - Order #" + order.getId());
            message.setText(buildAdminEmailContent(order, user));

            mailSender.send(message);
            log.info("Order notification email sent to admin: {}", adminEmail);
        } catch (Exception e) {
            log.error("Failed to send order notification email to admin", e);
        }
    }

    @Override
    @Async
    public void sendCartReminderEmail(User user, Product product) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Don't Forget Your Cart! Complete Your Purchase");
            message.setText(buildCartReminderContent(user, product));

            mailSender.send(message);
            log.info("Cart reminder email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send cart reminder email to: {}", user.getEmail(), e);
        }
    }

    @Override
    @Async
    public void sendWishlistReminderEmail(User user, Product product) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Your Wishlist Item is Waiting! Buy Now");
            message.setText(buildWishlistReminderContent(user, product));

            mailSender.send(message);
            log.info("Wishlist reminder email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send wishlist reminder email to: {}", user.getEmail(), e);
        }
    }

    private String buildCustomerEmailContent(Order order, User user) {
        StringBuilder content = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        content.append("Dear ").append(user.getFullName()).append(",\n\n");
        content.append("Thank you for your order! We have received your order and it is being processed.\n\n");
        content.append("Order Details:\n");
        content.append("=====================================\n");
        content.append("Order ID: ").append(order.getId()).append("\n");
        content.append("Order Date: ").append(order.getOrderDate().format(formatter)).append("\n");
        content.append("Order Status: ").append(order.getStatus()).append("\n");
        content.append("Total Amount: ₹").append(String.format("%.2f", order.getTotalAmount())).append("\n\n");

        content.append("Items Ordered:\n");
        content.append("-------------------------------------\n");
        for (int i = 0; i < order.getItems().size(); i++) {
            Order.OrderItem item = order.getItems().get(i);
            content.append((i + 1)).append(". Product ID: ").append(item.getProductId()).append("\n");
            content.append("   Quantity: ").append(item.getQuantity()).append("\n");
            content.append("   Price: ₹").append(String.format("%.2f", item.getPrice())).append("\n\n");
        }

        content.append("Shipping Address:\n");
        content.append("-------------------------------------\n");
        Order.Address address = order.getAddress();
        content.append(address.getAddressLine()).append("\n");
        content.append(address.getCity()).append(", ").append(address.getState()).append("\n");
        content.append(address.getPostalCode()).append(", ").append(address.getCountry()).append("\n\n");

        content.append("We will notify you once your order is shipped.\n\n");
        content.append("Thank you for shopping with us!\n\n");
        content.append("Best Regards,\n");
        content.append("Adita Enterprise India");

        return content.toString();
    }

    private String buildAdminEmailContent(Order order, User user) {
        StringBuilder content = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        content.append("New Order Received!\n\n");
        content.append("Order Details:\n");
        content.append("=====================================\n");
        content.append("Order ID: ").append(order.getId()).append("\n");
        content.append("Order Date: ").append(order.getOrderDate().format(formatter)).append("\n");
        content.append("Order Status: ").append(order.getStatus()).append("\n");
        content.append("Total Amount: ₹").append(String.format("%.2f", order.getTotalAmount())).append("\n\n");

        content.append("Customer Details:\n");
        content.append("-------------------------------------\n");
        content.append("Name: ").append(user.getFullName()).append("\n");
        content.append("Email: ").append(user.getEmail()).append("\n");
        content.append("Phone: ").append(user.getPhone() != null ? user.getPhone() : "N/A").append("\n");
        content.append("User ID: ").append(user.getId()).append("\n\n");

        content.append("Items Ordered:\n");
        content.append("-------------------------------------\n");
        for (int i = 0; i < order.getItems().size(); i++) {
            Order.OrderItem item = order.getItems().get(i);
            content.append((i + 1)).append(". Product ID: ").append(item.getProductId()).append("\n");
            content.append("   Quantity: ").append(item.getQuantity()).append("\n");
            content.append("   Price: ₹").append(String.format("%.2f", item.getPrice())).append("\n\n");
        }

        content.append("Shipping Address:\n");
        content.append("-------------------------------------\n");
        Order.Address address = order.getAddress();
        content.append(address.getAddressLine()).append("\n");
        content.append(address.getCity()).append(", ").append(address.getState()).append("\n");
        content.append(address.getPostalCode()).append(", ").append(address.getCountry()).append("\n\n");

        content.append("Please process this order promptly.\n\n");
        content.append("---\n");
        content.append("Automated notification from Ecommerce System");

        return content.toString();
    }

    private String buildCartReminderContent(User user, Product product) {
        StringBuilder content = new StringBuilder();

        content.append("Dear ").append(user.getFullName()).append(",\n\n");
        content.append("We noticed you left something in your cart!\n\n");
        content.append("Don't miss out on this amazing product:\n\n");
        content.append("=====================================\n");
        content.append("Product: ").append(product.getName()).append("\n");
        content.append("Price: ₹").append(String.format("%.2f", product.getPrice())).append("\n");
        
        if (product.getStockQuantity() > 0 && product.getStockQuantity() <= 5) {
            content.append("⚠️ Only ").append(product.getStockQuantity()).append(" left in stock!\n");
        }
        
        content.append("=====================================\n\n");
        content.append("Complete your purchase now before it's gone!\n\n");
        content.append("Your cart is waiting for you. Click below to checkout:\n");
        content.append("👉 Visit our store and complete your order today!\n\n");
        content.append("If you have any questions, feel free to reach out to us.\n\n");
        content.append("Happy Shopping!\n\n");
        content.append("Best Regards,\n");
        content.append("Adita Enterprise India\n\n");
        content.append("---\n");
        content.append("P.S. This is a friendly reminder. If you've already completed your purchase, please ignore this email.");

        return content.toString();
    }

    private String buildWishlistReminderContent(User user, Product product) {
        StringBuilder content = new StringBuilder();

        content.append("Dear ").append(user.getFullName()).append(",\n\n");
        content.append("Your wishlist item is calling you! 💝\n\n");
        content.append("Remember this product you loved?\n\n");
        content.append("=====================================\n");
        content.append("Product: ").append(product.getName()).append("\n");
        content.append("Price: ₹").append(String.format("%.2f", product.getPrice())).append("\n");
        
        if (product.getStockQuantity() > 0 && product.getStockQuantity() <= 5) {
            content.append("⚠️ Hurry! Only ").append(product.getStockQuantity()).append(" left in stock!\n");
        } else if (product.getStockQuantity() > 0) {
            content.append("✅ In Stock - Available Now!\n");
        } else {
            content.append("❌ Currently Out of Stock\n");
        }
        
        content.append("=====================================\n\n");
        
        if (product.getStockQuantity() > 0) {
            content.append("Why wait? Turn your wish into reality today!\n\n");
            content.append("Move it to your cart and checkout now:\n");
            content.append("👉 Buy Now and make it yours!\n\n");
        } else {
            content.append("This item is currently out of stock, but we'll notify you when it's back!\n\n");
        }
        
        content.append("Don't let this opportunity slip away.\n\n");
        content.append("Happy Shopping!\n\n");
        content.append("Best Regards,\n");
        content.append("Adita Enterprise India\n\n");
        content.append("---\n");
        content.append("P.S. You can manage your wishlist anytime by visiting our store.");

        return content.toString();
    }
}
