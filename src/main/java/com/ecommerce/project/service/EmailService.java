package com.ecommerce.project.service;

import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.entity.User;

public interface EmailService {
    
    void sendOrderConfirmationToCustomer(Order order, User user);
    
    void sendOrderNotificationToAdmin(Order order, User user);

    void sendCartReminderEmail(User user, Product product);

    void sendWishlistReminderEmail(User user, Product product);
}
