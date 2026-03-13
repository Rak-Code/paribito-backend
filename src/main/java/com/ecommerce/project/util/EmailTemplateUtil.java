package com.ecommerce.project.util;

import com.ecommerce.project.entity.Category;
import com.ecommerce.project.entity.Invoice;
import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.Payment;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for generating HTML email templates with brand styling.
 * All templates follow the Adita Enterprise India brand guidelines.
 */
@Component
@RequiredArgsConstructor
public class EmailTemplateUtil {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private static final String BRAND_NAME = "THE PARIBITO";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    /**
     * Formats a monetary amount with the ₹ symbol and two decimal places.
     * @param amount the amount to format
     * @return formatted currency string (e.g., "₹1234.56")
     */
    public String formatCurrency(Double amount) {
        if (amount == null) {
            return "₹0.00";
        }
        return String.format("₹%.2f", amount);
    }

    /**
     * Formats a date/time in the standard format (dd-MM-yyyy HH:mm:ss).
     * @param dateTime the date/time to format
     * @return formatted date string
     */
    public String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        return dateTime.format(DATE_FORMATTER);
    }

    /**
     * Safely gets a string value, returning a default if null or empty.
     */
    private String safeGet(String value, String defaultValue) {
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    /**
     * Builds the base HTML template structure with header and footer.
     */
    private String buildBaseTemplate(String title, String content) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<title>").append(title).append("</title>");
        html.append("</head>");
        html.append("<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;\">");
        html.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff;\">");
        
        // Header
        html.append("<tr>");
        html.append("<td style=\"background-color: #000000; padding: 20px; text-align: center;\">");
        html.append("<h1 style=\"color: #ffffff; margin: 0; font-size: 24px; font-weight: bold; letter-spacing: 2px;\">").append(BRAND_NAME).append("</h1>");
        html.append("</td>");
        html.append("</tr>");
        
        // Content
        html.append("<tr>");
        html.append("<td style=\"padding: 30px;\">");
        html.append(content);
        html.append("</td>");
        html.append("</tr>");
        
        // Footer
        html.append("<tr>");
        html.append("<td style=\"background-color: #f8f9fa; padding: 20px; text-align: center; font-size: 12px; color: #666666; border-top: 1px solid #dee2e6;\">");
        html.append("<p style=\"margin: 0;\">&copy; 2024 ").append(BRAND_NAME).append(". All rights reserved.</p>");
        html.append("<p style=\"margin: 5px 0 0 0;\">Thank you for shopping with us!</p>");
        html.append("<p style=\"margin: 5px 0 0 0; font-size: 10px; color: #999999;\">Managed by aivro.in</p>");
        html.append("</td>");
        html.append("</tr>");
        
        html.append("</table>");
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }


    /**
     * Builds the payment success email HTML content.
     * @param payment the payment details
     * @param order the associated order
     * @param user the customer
     * @return HTML email content
     */
    public String buildPaymentSuccessEmail(Payment payment, Order order, User user) {
        StringBuilder content = new StringBuilder();
        
        content.append("<h2 style=\"color: #000000; margin-top: 0; font-size: 24px; font-weight: bold;\">Payment Successful! ✓</h2>");
        content.append("<p>Dear ").append(safeGet(user.getFullName(), "Customer")).append(",</p>");
        content.append("<p>Your payment has been successfully processed. Thank you for your purchase!</p>");
        
        // Payment Details Box
        content.append("<div style=\"background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0; border: 1px solid #dee2e6;\">");
        content.append("<h3 style=\"margin-top: 0; color: #000000; font-size: 18px; font-weight: bold;\">Payment Details</h3>");
        content.append("<table style=\"width: 100%; border-collapse: collapse;\">");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Transaction ID:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(safeGet(payment.getRazorpayPaymentId(), payment.getTransactionId())).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Order ID:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(safeGet(order.getId(), "N/A")).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Payment Date:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(formatDate(payment.getPaymentDate())).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0;\"><strong>Amount Paid:</strong></td>");
        content.append("<td style=\"padding: 8px 0; text-align: right; font-size: 18px; color: #000000; font-weight: bold;\">").append(formatCurrency(payment.getAmount())).append("</td></tr>");
        content.append("</table>");
        content.append("</div>");
        
        content.append("<p>You will receive a separate email with your order confirmation and tracking details.</p>");
        content.append("<p>If you have any questions about your payment, please don't hesitate to contact us.</p>");
        content.append("<p style=\"margin-top: 30px;\">Best Regards,<br><strong>").append(BRAND_NAME).append("</strong></p>");
        
        return buildBaseTemplate("Payment Confirmation - " + BRAND_NAME, content.toString());
    }

    /**
     * Builds the order confirmation email HTML content for customers.
     * @param order the order details
     * @param user the customer
     * @return HTML email content
     */
    public String buildOrderConfirmationEmail(Order order, User user) {
        StringBuilder content = new StringBuilder();
        
        content.append("<h2 style=\"color: #000000; margin-top: 0; font-size: 24px; font-weight: bold;\">Order Confirmed!</h2>");
        content.append("<p>Dear ").append(safeGet(user.getFullName(), "Customer")).append(",</p>");
        content.append("<p>Thank you for your order! We have received your order and it is being processed.</p>");
        
        // Order Summary Box
        content.append("<div style=\"background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0; border: 1px solid #dee2e6;\">");
        content.append("<h3 style=\"margin-top: 0; color: #000000; font-size: 18px; font-weight: bold;\">Order Summary</h3>");
        content.append("<table style=\"width: 100%; border-collapse: collapse;\">");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Order ID:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(safeGet(order.getId(), "N/A")).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Order Date:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(formatDate(order.getOrderDate())).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0;\"><strong>Status:</strong></td>");
        content.append("<td style=\"padding: 8px 0; text-align: right;\">").append(order.getStatus() != null ? order.getStatus().name().toUpperCase() : "PENDING").append("</td></tr>");
        content.append("</table>");
        content.append("</div>");
        
        // Items Table
        content.append("<h3 style=\"color: #000000; font-size: 18px; font-weight: bold;\">Items Ordered</h3>");
        content.append("<table style=\"width: 100%; border-collapse: collapse; margin-bottom: 20px;\">");
        content.append("<tr style=\"background-color: #000000; color: white;\">");
        content.append("<th style=\"padding: 12px; text-align: left;\">#</th>");
        content.append("<th style=\"padding: 12px; text-align: left;\">Product Name</th>");
        content.append("<th style=\"padding: 12px; text-align: center;\">Qty</th>");
        content.append("<th style=\"padding: 12px; text-align: right;\">Price</th>");
        content.append("<th style=\"padding: 12px; text-align: right;\">Total</th>");
        content.append("</tr>");
        
        if (order.getItems() != null) {
            int index = 1;
            for (Order.OrderItem item : order.getItems()) {
                double itemTotal = item.getQuantity() * item.getPrice();
                String bgColor = index % 2 == 0 ? "#f8f9fa" : "#ffffff";
                
                // Fetch product name
                String productName = "Product " + item.getProductId();
                try {
                    Product product = productRepository.findById(item.getProductId()).orElse(null);
                    if (product != null && product.getName() != null && !product.getName().isBlank()) {
                        productName = product.getName();
                    }
                } catch (Exception e) {
                    // Keep default product name if fetch fails
                }
                
                content.append("<tr style=\"background-color: ").append(bgColor).append(";\">");
                content.append("<td style=\"padding: 12px; border-bottom: 1px solid #dee2e6;\">").append(index).append("</td>");
                content.append("<td style=\"padding: 12px; border-bottom: 1px solid #dee2e6; font-weight: 500;\">").append(productName).append("</td>");
                content.append("<td style=\"padding: 12px; border-bottom: 1px solid #dee2e6; text-align: center;\">").append(item.getQuantity()).append("</td>");
                content.append("<td style=\"padding: 12px; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(formatCurrency(item.getPrice())).append("</td>");
                content.append("<td style=\"padding: 12px; border-bottom: 1px solid #dee2e6; text-align: right; font-weight: bold;\">").append(formatCurrency(itemTotal)).append("</td>");
                content.append("</tr>");
                index++;
            }
        }
        
        // Total Row
        content.append("<tr style=\"background-color: #e9ecef; font-weight: bold;\">");
        content.append("<td colspan=\"4\" style=\"padding: 12px; text-align: right;\">Order Total:</td>");
        content.append("<td style=\"padding: 12px; text-align: right; font-size: 18px; color: #000000; font-weight: bold;\">").append(formatCurrency(order.getTotalAmount())).append("</td>");
        content.append("</tr>");
        content.append("</table>");
        
        // Shipping Address
        if (order.getAddress() != null) {
            Order.Address address = order.getAddress();
            content.append("<div style=\"background-color: #f8f9fa; padding: 15px; border-radius: 8px; border: 1px solid #dee2e6; margin: 20px 0;\">");
            content.append("<h4 style=\"margin-top: 0; color: #000000; font-size: 16px; font-weight: bold;\">📦 Shipping Address</h4>");
            content.append("<p style=\"margin: 0; line-height: 1.6; color: #333333;\">");
            
            // Build address with proper formatting
            StringBuilder addressText = new StringBuilder();
            if (address.getAddressLine() != null && !address.getAddressLine().isBlank()) {
                addressText.append(address.getAddressLine()).append("<br>");
            }
            
            // City and State
            if (address.getCity() != null && !address.getCity().isBlank()) {
                if (address.getState() != null && !address.getState().isBlank()) {
                    addressText.append(address.getCity()).append(", ").append(address.getState()).append("<br>");
                } else {
                    addressText.append(address.getCity()).append("<br>");
                }
            } else if (address.getState() != null && !address.getState().isBlank()) {
                addressText.append(address.getState()).append("<br>");
            }
            
            // Postal Code
            if (address.getPostalCode() != null && !address.getPostalCode().isBlank()) {
                addressText.append(address.getPostalCode());
                if (address.getCountry() != null && !address.getCountry().isBlank()) {
                    addressText.append(", ").append(address.getCountry());
                }
            } else if (address.getCountry() != null && !address.getCountry().isBlank()) {
                addressText.append(address.getCountry());
            }
            
            content.append(addressText.toString());
            content.append("</p>");
            content.append("</div>");
        }
        
        content.append("<p>We will notify you once your order is shipped.</p>");
        content.append("<p style=\"margin-top: 30px;\">Best Regards,<br><strong>").append(BRAND_NAME).append("</strong></p>");
        
        return buildBaseTemplate("Order Confirmation - " + BRAND_NAME, content.toString());
    }


    /**
     * Builds the admin order notification email HTML content with enhanced product details.
     * @param order the order details
     * @param user the customer who placed the order
     * @return HTML email content
     */
    public String buildAdminOrderNotificationEmail(Order order, User user) {
        StringBuilder content = new StringBuilder();
        
        content.append("<h2 style=\"color: #000000; margin-top: 0; font-size: 24px; font-weight: bold;\">🔔 New Order Received!</h2>");
        content.append("<p>A new paid order has been placed and requires processing.</p>");
        
        // Order Date and Time - Prominent Display
        content.append("<div style=\"background-color: #f8f9fa; padding: 15px; border-radius: 8px; border: 1px solid #dee2e6; margin: 20px 0;\">");
        content.append("<h4 style=\"margin-top: 0; color: #000000; font-size: 16px; font-weight: bold;\">📅 Order Date & Time</h4>");
        content.append("<p style=\"margin: 0; color: #333333; font-size: 16px; font-weight: bold;\">").append(formatDate(order.getOrderDate())).append("</p>");
        content.append("</div>");
        
        // Customer Information Section - Enhanced
        content.append("<div style=\"background-color: #f8f9fa; padding: 15px; border-radius: 8px; border: 1px solid #dee2e6; margin: 20px 0;\">");
        content.append("<h4 style=\"margin-top: 0; color: #000000; font-size: 16px; font-weight: bold;\">👤 Customer Information</h4>");
        content.append("<table style=\"width: 100%;\">");
        content.append("<tr><td style=\"padding: 4px 0;\"><strong>Full Name:</strong></td><td>").append(safeGet(user.getFullName(), "N/A")).append("</td></tr>");
        content.append("<tr><td style=\"padding: 4px 0;\"><strong>Email:</strong></td><td>").append(safeGet(user.getEmail(), "N/A")).append("</td></tr>");
        content.append("<tr><td style=\"padding: 4px 0;\"><strong>Phone Number:</strong></td><td>").append(safeGet(user.getPhone(), "N/A")).append("</td></tr>");
        content.append("<tr><td style=\"padding: 4px 0;\"><strong>User ID:</strong></td><td>").append(safeGet(user.getId(), "N/A")).append("</td></tr>");
        content.append("</table>");
        content.append("</div>");
        
        // Order Details Box
        content.append("<div style=\"background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0; border: 1px solid #dee2e6;\">");
        content.append("<h3 style=\"margin-top: 0; color: #000000; font-size: 18px; font-weight: bold;\">Order Details</h3>");
        content.append("<table style=\"width: 100%; border-collapse: collapse;\">");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Order ID:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(safeGet(order.getId(), "N/A")).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Status:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(order.getStatus() != null ? order.getStatus().name().toUpperCase() : "PENDING").append("</td></tr>");
        content.append("</table>");
        content.append("</div>");
        
        // Enhanced Product Details Section
        content.append("<h3 style=\"color: #000000; font-size: 18px; font-weight: bold;\">📦 Product Details</h3>");
        
        double subtotal = 0;
        if (order.getItems() != null) {
            int index = 1;
            for (Order.OrderItem item : order.getItems()) {
                double itemTotal = item.getQuantity() * item.getPrice();
                subtotal += itemTotal;
                
                // Fetch product details from database
                Product product = productRepository.findById(item.getProductId()).orElse(null);
                
                content.append(buildProductDetailCard(product, item, index));
                index++;
            }
        }
        
        // Order Summary Section
        content.append("<div style=\"background-color: #e9ecef; padding: 20px; border-radius: 8px; margin: 20px 0;\">");
        content.append("<h3 style=\"margin-top: 0; color: #000000; font-size: 18px; font-weight: bold;\">💰 Order Summary</h3>");
        content.append("<table style=\"width: 100%; border-collapse: collapse;\">");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Subtotal:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(formatCurrency(subtotal)).append("</td></tr>");
        
        // Tax calculation (if applicable)
        double tax = order.getTotalAmount() - subtotal;
        if (Math.abs(tax) > 0.01) { // Only show if tax is non-zero
            content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Tax:</strong></td>");
            content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(formatCurrency(tax)).append("</td></tr>");
        }
        
        content.append("<tr><td style=\"padding: 12px 0; font-size: 18px;\"><strong>Grand Total:</strong></td>");
        content.append("<td style=\"padding: 12px 0; text-align: right; font-size: 20px; color: #000000; font-weight: bold;\">").append(formatCurrency(order.getTotalAmount())).append("</td></tr>");
        content.append("</table>");
        content.append("</div>");
        
        // Complete Delivery Address Section
        if (order.getAddress() != null) {
            Order.Address address = order.getAddress();
            content.append("<div style=\"background-color: #f8f9fa; padding: 15px; border-radius: 8px; border: 1px solid #dee2e6; margin: 20px 0;\">");
            content.append("<h4 style=\"margin-top: 0; color: #000000; font-size: 16px; font-weight: bold;\">📦 Complete Delivery Address</h4>");
            content.append("<p style=\"margin: 0; color: #333333; line-height: 1.6;\">");
            
            // Build address with proper formatting
            StringBuilder addressText = new StringBuilder();
            if (address.getAddressLine() != null && !address.getAddressLine().isBlank()) {
                addressText.append(address.getAddressLine()).append("<br>");
            }
            
            // City and State
            if (address.getCity() != null && !address.getCity().isBlank()) {
                if (address.getState() != null && !address.getState().isBlank()) {
                    addressText.append(address.getCity()).append(", ").append(address.getState()).append("<br>");
                } else {
                    addressText.append(address.getCity()).append("<br>");
                }
            } else if (address.getState() != null && !address.getState().isBlank()) {
                addressText.append(address.getState()).append("<br>");
            }
            
            // Postal Code
            if (address.getPostalCode() != null && !address.getPostalCode().isBlank()) {
                addressText.append(address.getPostalCode());
                if (address.getCountry() != null && !address.getCountry().isBlank()) {
                    addressText.append(", ").append(address.getCountry());
                }
            } else if (address.getCountry() != null && !address.getCountry().isBlank()) {
                addressText.append(address.getCountry());
            }
            
            content.append(addressText.toString());
            content.append("</p>");
            content.append("</div>");
        }
        
        content.append("<p style=\"background-color: #f8f9fa; padding: 15px; border-radius: 8px; color: #333333; border: 1px solid #dee2e6;\"><strong>⚠️ Action Required:</strong> Please process this order promptly.</p>");
        content.append("<p style=\"margin-top: 20px; font-size: 12px; color: #6c757d;\">---<br>Automated notification from ").append(BRAND_NAME).append(" System</p>");
        
        return buildBaseTemplate("New Order - " + BRAND_NAME, content.toString());
    }

    /**
     * Builds the order cancellation email HTML content for customers.
     * @param order the cancelled order
     * @param user the customer
     * @return HTML email content
     */
    public String buildOrderCancellationEmail(Order order, User user) {
        StringBuilder content = new StringBuilder();
        
        content.append("<h2 style=\"color: #000000; margin-top: 0; font-size: 24px; font-weight: bold;\">Order Cancelled</h2>");
        content.append("<p>Dear ").append(safeGet(user.getFullName(), "Customer")).append(",</p>");
        content.append("<p>We're sorry to inform you that your order has been cancelled.</p>");
        
        // Order Details Box
        content.append("<div style=\"background-color: #f8f9fa; padding: 20px; border-radius: 8px; border: 1px solid #dee2e6; margin: 20px 0;\">");
        content.append("<h3 style=\"margin-top: 0; color: #000000; font-size: 18px; font-weight: bold;\">Cancelled Order Details</h3>");
        content.append("<table style=\"width: 100%; border-collapse: collapse;\">");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #f5c6cb;\"><strong>Order ID:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #f5c6cb; text-align: right;\">").append(safeGet(order.getId(), "N/A")).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #f5c6cb;\"><strong>Order Date:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #f5c6cb; text-align: right;\">").append(formatDate(order.getOrderDate())).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #f5c6cb;\"><strong>Cancellation Date:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #f5c6cb; text-align: right;\">").append(formatDate(LocalDateTime.now())).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #f5c6cb;\"><strong>Order Total:</strong></td>");
        content.append("<td style=\"padding: 8px 0; text-align: right; font-size: 18px; color: #000000; font-weight: bold;\">").append(formatCurrency(order.getTotalAmount())).append("</td></tr>");
        content.append("</table>");
        content.append("</div>");
        
        // Items that were in the order
        content.append("<h3 style=\"color: #000000; font-size: 18px; font-weight: bold;\">Items in Cancelled Order</h3>");
        content.append("<table style=\"width: 100%; border-collapse: collapse; margin-bottom: 20px;\">");
        content.append("<tr style=\"background-color: #000000; color: white;\">");
        content.append("<th style=\"padding: 12px; text-align: left;\">Product Name</th>");
        content.append("<th style=\"padding: 12px; text-align: center;\">Qty</th>");
        content.append("<th style=\"padding: 12px; text-align: right;\">Price</th>");
        content.append("</tr>");
        
        if (order.getItems() != null) {
            for (Order.OrderItem item : order.getItems()) {
                // Fetch product name
                String productName = "Product " + item.getProductId();
                try {
                    Product product = productRepository.findById(item.getProductId()).orElse(null);
                    if (product != null && product.getName() != null && !product.getName().isBlank()) {
                        productName = product.getName();
                    }
                } catch (Exception e) {
                    // Keep default product name if fetch fails
                }
                
                content.append("<tr style=\"background-color: #f8f9fa;\">");
                content.append("<td style=\"padding: 12px; border-bottom: 1px solid #dee2e6; font-weight: 500;\">").append(productName).append("</td>");
                content.append("<td style=\"padding: 12px; border-bottom: 1px solid #dee2e6; text-align: center;\">").append(item.getQuantity()).append("</td>");
                content.append("<td style=\"padding: 12px; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(formatCurrency(item.getPrice())).append("</td>");
                content.append("</tr>");
            }
        }
        content.append("</table>");
        
        content.append("<p>If you paid for this order, a refund will be processed within 5-7 business days.</p>");
        content.append("<p>If you have any questions or concerns, please don't hesitate to contact our support team.</p>");
        content.append("<p style=\"margin-top: 30px;\">Best Regards,<br><strong>").append(BRAND_NAME).append("</strong></p>");
        
        return buildBaseTemplate("Order Cancelled - " + BRAND_NAME, content.toString());
    }


    /**
     * Builds the admin cancellation notification email HTML content.
     * @param order the cancelled order
     * @param user the customer who cancelled
     * @return HTML email content
     */
    public String buildAdminCancellationEmail(Order order, User user) {
        StringBuilder content = new StringBuilder();
        
        content.append("<h2 style=\"color: #000000; margin-top: 0; font-size: 24px; font-weight: bold;\">⚠️ Order Cancellation Notice</h2>");
        content.append("<p>An order has been cancelled and may require inventory updates.</p>");
        
        // Customer Details Box
        content.append("<div style=\"background-color: #f8f9fa; padding: 15px; border-radius: 8px; border: 1px solid #dee2e6; margin: 20px 0;\">");
        content.append("<h4 style=\"margin-top: 0; color: #000000; font-size: 16px; font-weight: bold;\">👤 Customer Details</h4>");
        content.append("<table style=\"width: 100%;\">");
        content.append("<tr><td style=\"padding: 4px 0;\"><strong>Name:</strong></td><td>").append(safeGet(user.getFullName(), "N/A")).append("</td></tr>");
        content.append("<tr><td style=\"padding: 4px 0;\"><strong>Email:</strong></td><td>").append(safeGet(user.getEmail(), "N/A")).append("</td></tr>");
        content.append("<tr><td style=\"padding: 4px 0;\"><strong>Phone:</strong></td><td>").append(safeGet(user.getPhone(), "N/A")).append("</td></tr>");
        content.append("<tr><td style=\"padding: 4px 0;\"><strong>User ID:</strong></td><td>").append(safeGet(user.getId(), "N/A")).append("</td></tr>");
        content.append("</table>");
        content.append("</div>");
        
        // Order Details Box
        content.append("<div style=\"background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0; border: 1px solid #dee2e6;\">");
        content.append("<h3 style=\"margin-top: 0; color: #000000; font-size: 18px; font-weight: bold;\">Cancelled Order Details</h3>");
        content.append("<table style=\"width: 100%; border-collapse: collapse;\">");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Order ID:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(safeGet(order.getId(), "N/A")).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Order Date:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(formatDate(order.getOrderDate())).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Cancellation Date:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(formatDate(LocalDateTime.now())).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0;\"><strong>Order Total:</strong></td>");
        content.append("<td style=\"padding: 8px 0; text-align: right; font-size: 18px; color: #000000; font-weight: bold;\"><strong>").append(formatCurrency(order.getTotalAmount())).append("</strong></td></tr>");
        content.append("</table>");
        content.append("</div>");
        
        // Items Table
        content.append("<h3 style=\"color: #000000; font-size: 18px; font-weight: bold;\">Items to Return to Inventory</h3>");
        content.append("<table style=\"width: 100%; border-collapse: collapse; margin-bottom: 20px;\">");
        content.append("<tr style=\"background-color: #000000; color: white;\">");
        content.append("<th style=\"padding: 12px; text-align: left;\">Product Name</th>");
        content.append("<th style=\"padding: 12px; text-align: center;\">Qty</th>");
        content.append("<th style=\"padding: 12px; text-align: right;\">Price</th>");
        content.append("</tr>");
        
        if (order.getItems() != null) {
            for (Order.OrderItem item : order.getItems()) {
                // Fetch product name
                String productName = "Product " + item.getProductId();
                try {
                    Product product = productRepository.findById(item.getProductId()).orElse(null);
                    if (product != null && product.getName() != null && !product.getName().isBlank()) {
                        productName = product.getName();
                    }
                } catch (Exception e) {
                    // Keep default product name if fetch fails
                }
                
                content.append("<tr style=\"background-color: #f8f9fa;\">");
                content.append("<td style=\"padding: 12px; border-bottom: 1px solid #dee2e6; font-weight: 500;\">").append(productName).append("</td>");
                content.append("<td style=\"padding: 12px; border-bottom: 1px solid #dee2e6; text-align: center;\">").append(item.getQuantity()).append("</td>");
                content.append("<td style=\"padding: 12px; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(formatCurrency(item.getPrice())).append("</td>");
                content.append("</tr>");
            }
        }
        content.append("</table>");
        
        content.append("<p style=\"background-color: #f8f9fa; padding: 15px; border-radius: 8px; color: #333333; border: 1px solid #dee2e6;\"><strong>⚠️ Action Required:</strong> Please update inventory and process any refunds if applicable.</p>");
        content.append("<p style=\"margin-top: 20px; font-size: 12px; color: #6c757d;\">---<br>Automated notification from ").append(BRAND_NAME).append(" System</p>");
        
        return buildBaseTemplate("Order Cancelled - " + BRAND_NAME, content.toString());
    }

    /**
     * Builds the invoice email HTML content for customers.
     * @param invoice the invoice details
     * @param user the customer
     * @return HTML email content
     */
    public String buildInvoiceEmail(Invoice invoice, User user) {
        StringBuilder content = new StringBuilder();
        
        content.append("<h2 style=\"color: #000000; margin-top: 0; font-size: 24px; font-weight: bold;\">📄 Your Invoice is Ready!</h2>");
        content.append("<p>Dear ").append(safeGet(user.getFullName(), "Customer")).append(",</p>");
        content.append("<p>Thank you for your order! Please find your invoice attached to this email.</p>");
        
        // Invoice Details Box
        content.append("<div style=\"background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0; border: 1px solid #dee2e6;\">");
        content.append("<h3 style=\"margin-top: 0; color: #000000; font-size: 18px; font-weight: bold;\">Invoice Details</h3>");
        content.append("<table style=\"width: 100%; border-collapse: collapse;\">");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Invoice Number:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(safeGet(invoice.getInvoiceNumber(), "N/A")).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Invoice Date:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(formatDate(invoice.getInvoiceDate())).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Order ID:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(safeGet(invoice.getOrderId(), "N/A")).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Subtotal:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(formatCurrency(invoice.getSubtotal())).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Tax:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(formatCurrency(invoice.getTaxAmount())).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0;\"><strong>Total Amount:</strong></td>");
        content.append("<td style=\"padding: 8px 0; text-align: right; font-size: 18px; color: #000000; font-weight: bold;\"><strong>").append(formatCurrency(invoice.getTotalAmount())).append("</strong></td></tr>");
        content.append("</table>");
        content.append("</div>");
        
        content.append("<p>📎 The invoice PDF is attached to this email for your records.</p>");
        content.append("<p>If you have any questions about your invoice, please don't hesitate to contact us.</p>");
        content.append("<p style=\"margin-top: 30px;\">Best Regards,<br><strong>").append(BRAND_NAME).append("</strong></p>");
        
        return buildBaseTemplate("Invoice - " + BRAND_NAME, content.toString());
    }

    /**
     * Builds the admin invoice notification email HTML content.
     * @param invoice the invoice details
     * @param user the customer
     * @return HTML email content
     */
    public String buildAdminInvoiceEmail(Invoice invoice, User user) {
        StringBuilder content = new StringBuilder();
        
        content.append("<h2 style=\"color: #000000; margin-top: 0; font-size: 24px; font-weight: bold;\">📄 Invoice Generated</h2>");
        content.append("<p>A new invoice has been generated and sent to the customer.</p>");
        
        // Customer Details Box
        content.append("<div style=\"background-color: #f8f9fa; padding: 15px; border-radius: 8px; border: 1px solid #dee2e6; margin: 20px 0;\">");
        content.append("<h4 style=\"margin-top: 0; color: #000000; font-size: 16px; font-weight: bold;\">👤 Customer Details</h4>");
        content.append("<table style=\"width: 100%;\">");
        content.append("<tr><td style=\"padding: 4px 0;\"><strong>Name:</strong></td><td>").append(safeGet(user.getFullName(), "N/A")).append("</td></tr>");
        content.append("<tr><td style=\"padding: 4px 0;\"><strong>Email:</strong></td><td>").append(safeGet(user.getEmail(), "N/A")).append("</td></tr>");
        content.append("<tr><td style=\"padding: 4px 0;\"><strong>User ID:</strong></td><td>").append(safeGet(user.getId(), "N/A")).append("</td></tr>");
        content.append("</table>");
        content.append("</div>");
        
        // Invoice Details Box
        content.append("<div style=\"background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0; border: 1px solid #dee2e6;\">");
        content.append("<h3 style=\"margin-top: 0; color: #000000; font-size: 18px; font-weight: bold;\">Invoice Details</h3>");
        content.append("<table style=\"width: 100%; border-collapse: collapse;\">");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Invoice Number:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(safeGet(invoice.getInvoiceNumber(), "N/A")).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Invoice Date:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(formatDate(invoice.getInvoiceDate())).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Order ID:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(safeGet(invoice.getOrderId(), "N/A")).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6;\"><strong>Generated At:</strong></td>");
        content.append("<td style=\"padding: 8px 0; border-bottom: 1px solid #dee2e6; text-align: right;\">").append(formatDate(invoice.getGeneratedAt())).append("</td></tr>");
        content.append("<tr><td style=\"padding: 8px 0;\"><strong>Total Amount:</strong></td>");
        content.append("<td style=\"padding: 8px 0; text-align: right; font-size: 18px; color: #000000; font-weight: bold;\"><strong>").append(formatCurrency(invoice.getTotalAmount())).append("</strong></td></tr>");
        content.append("</table>");
        content.append("</div>");
        
        content.append("<p>📎 The invoice PDF is attached to this email.</p>");
        content.append("<p style=\"margin-top: 20px; font-size: 12px; color: #6c757d;\">---<br>Automated notification from ").append(BRAND_NAME).append(" System</p>");
        
        return buildBaseTemplate("Invoice Generated - " + BRAND_NAME, content.toString());
    }


    /**
     * Builds the cart reminder email HTML content.
     * @param user the customer
     * @param product the product in cart
     * @return HTML email content
     */
    public String buildCartReminderEmail(User user, Product product) {
        StringBuilder content = new StringBuilder();
        
        content.append("<h2 style=\"color: #000000; margin-top: 0; font-size: 24px; font-weight: bold;\">🛒 Don't Forget Your Cart!</h2>");
        content.append("<p>Dear ").append(safeGet(user.getFullName(), "Customer")).append(",</p>");
        content.append("<p>We noticed you left something amazing in your cart. Don't miss out!</p>");
        
        // Product Box
        content.append("<div style=\"background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0; border: 2px solid #000000;\">");
        content.append("<h3 style=\"margin-top: 0; color: #000000; font-size: 18px; font-weight: bold;\">").append(safeGet(product.getName(), "Product")).append("</h3>");
        content.append("<p style=\"font-size: 24px; color: #000000; margin: 10px 0; font-weight: bold;\"><strong>").append(formatCurrency(product.getPrice())).append("</strong></p>");
        
        // Stock Status
        if (product.getStockQuantity() > 0 && product.getStockQuantity() <= 5) {
            content.append("<p style=\"background-color: #f8f9fa; padding: 10px; border-radius: 4px; color: #333333; border: 1px solid #dee2e6;\">⚠️ <strong>Hurry!</strong> Only ").append(product.getStockQuantity()).append(" left in stock!</p>");
        } else if (product.getStockQuantity() > 0) {
            content.append("<p style=\"background-color: #f8f9fa; padding: 10px; border-radius: 4px; color: #333333; border: 1px solid #dee2e6;\">✅ In Stock - Available Now!</p>");
        } else {
            content.append("<p style=\"background-color: #f8f9fa; padding: 10px; border-radius: 4px; color: #333333; border: 1px solid #dee2e6;\">❌ Currently Out of Stock</p>");
        }
        content.append("</div>");
        
        // Delivery Address if available
        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            User.Address deliveryAddress = user.getAddresses().stream()
                    .filter(User.Address::isDefault)
                    .findFirst()
                    .orElse(user.getAddresses().get(0));
            
            content.append("<div style=\"background-color: #f8f9fa; padding: 15px; border-radius: 8px; border: 1px solid #dee2e6; margin: 20px 0;\">");
            content.append("<h4 style=\"margin-top: 0; color: #000000; font-size: 16px; font-weight: bold;\">📦 Your Delivery Address</h4>");
            content.append("<p style=\"margin: 0; color: #333333; line-height: 1.6;\">");
            content.append(safeGet(deliveryAddress.getAddressLine(), "")).append("<br>");
            content.append(safeGet(deliveryAddress.getCity(), "")).append(", ").append(safeGet(deliveryAddress.getState(), "")).append("<br>");
            content.append(safeGet(deliveryAddress.getPostalCode(), "")).append(", ").append(safeGet(deliveryAddress.getCountry(), "India"));
            content.append("</p>");
            content.append("</div>");
        }
        
        content.append("<p style=\"text-align: center; margin: 30px 0;\">");
        content.append("<span style=\"background-color: #000000; color: white; padding: 15px 30px; border-radius: 8px; font-size: 16px; font-weight: bold;\">Complete Your Purchase Now!</span>");
        content.append("</p>");
        
        content.append("<p>Your cart is waiting for you. Visit our store and checkout today!</p>");
        content.append("<p>If you have any questions, feel free to reach out to us.</p>");
        content.append("<p style=\"margin-top: 30px;\">Happy Shopping!<br><strong>").append(BRAND_NAME).append("</strong></p>");
        content.append("<p style=\"font-size: 12px; color: #6c757d;\">P.S. This is a friendly reminder. If you've already completed your purchase, please ignore this email.</p>");
        
        return buildBaseTemplate("Complete Your Purchase - " + BRAND_NAME, content.toString());
    }

    /**
     * Builds the wishlist reminder email HTML content.
     * @param user the customer
     * @param product the product in wishlist
     * @return HTML email content
     */
    public String buildWishlistReminderEmail(User user, Product product) {
        StringBuilder content = new StringBuilder();
        
        content.append("<h2 style=\"color: #000000; margin-top: 0; font-size: 24px; font-weight: bold;\">💝 Your Wishlist Item is Calling!</h2>");
        content.append("<p>Dear ").append(safeGet(user.getFullName(), "Customer")).append(",</p>");
        content.append("<p>Remember this product you loved? It's still waiting for you!</p>");
        
        // Product Box
        content.append("<div style=\"background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0; border: 2px solid #000000;\">");
        content.append("<h3 style=\"margin-top: 0; color: #000000; font-size: 18px; font-weight: bold;\">").append(safeGet(product.getName(), "Product")).append("</h3>");
        content.append("<p style=\"font-size: 24px; color: #000000; margin: 10px 0; font-weight: bold;\"><strong>").append(formatCurrency(product.getPrice())).append("</strong></p>");
        
        // Stock Status
        if (product.getStockQuantity() > 0 && product.getStockQuantity() <= 5) {
            content.append("<p style=\"background-color: #f8f9fa; padding: 10px; border-radius: 4px; color: #333333; border: 1px solid #dee2e6;\">⚠️ <strong>Hurry!</strong> Only ").append(product.getStockQuantity()).append(" left in stock!</p>");
        } else if (product.getStockQuantity() > 0) {
            content.append("<p style=\"background-color: #f8f9fa; padding: 10px; border-radius: 4px; color: #333333; border: 1px solid #dee2e6;\">✅ In Stock - Available Now!</p>");
        } else {
            content.append("<p style=\"background-color: #f8f9fa; padding: 10px; border-radius: 4px; color: #333333; border: 1px solid #dee2e6;\">❌ Currently Out of Stock - We'll notify you when it's back!</p>");
        }
        content.append("</div>");
        
        // Delivery Address if available
        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            User.Address deliveryAddress = user.getAddresses().stream()
                    .filter(User.Address::isDefault)
                    .findFirst()
                    .orElse(user.getAddresses().get(0));
            
            content.append("<div style=\"background-color: #f8f9fa; padding: 15px; border-radius: 8px; border: 1px solid #dee2e6; margin: 20px 0;\">");
            content.append("<h4 style=\"margin-top: 0; color: #000000; font-size: 16px; font-weight: bold;\">📦 Your Delivery Address</h4>");
            content.append("<p style=\"margin: 0; color: #333333; line-height: 1.6;\">");
            content.append(safeGet(deliveryAddress.getAddressLine(), "")).append("<br>");
            content.append(safeGet(deliveryAddress.getCity(), "")).append(", ").append(safeGet(deliveryAddress.getState(), "")).append("<br>");
            content.append(safeGet(deliveryAddress.getPostalCode(), "")).append(", ").append(safeGet(deliveryAddress.getCountry(), "India"));
            content.append("</p>");
            content.append("</div>");
        }
        
        if (product.getStockQuantity() > 0) {
            content.append("<p style=\"text-align: center; margin: 30px 0;\">");
            content.append("<span style=\"background-color: #000000; color: white; padding: 15px 30px; border-radius: 8px; font-size: 16px; font-weight: bold;\">Turn Your Wish Into Reality!</span>");
            content.append("</p>");
            content.append("<p>Why wait? Move it to your cart and make it yours today!</p>");
        } else {
            content.append("<p>This item is currently out of stock, but we'll notify you when it's back!</p>");
        }
        
        content.append("<p>Don't let this opportunity slip away.</p>");
        content.append("<p style=\"margin-top: 30px;\">Happy Shopping!<br><strong>").append(BRAND_NAME).append("</strong></p>");
        content.append("<p style=\"font-size: 12px; color: #6c757d;\">P.S. You can manage your wishlist anytime by visiting our store.</p>");
        
        return buildBaseTemplate("Your Wishlist Awaits - " + BRAND_NAME, content.toString());
    }

    /**
     * Builds a product detail card HTML for admin order notifications.
     * This method encapsulates per-product HTML generation to avoid string concatenation in loops.
     *
     * @param product the product entity (may be null)
     * @param item the order item
     * @param index the item index for alternating row colors
     * @return HTML string for the product detail card
     */
    private String buildProductDetailCard(Product product, Order.OrderItem item, int index) {
        StringBuilder card = new StringBuilder();
        double itemTotal = item.getQuantity() * item.getPrice();
        
        String bgColor = index % 2 == 0 ? "#f8f9fa" : "#ffffff";
        card.append("<div style=\"background-color: ").append(bgColor)
            .append("; padding: 15px; border-radius: 8px; margin-bottom: 15px; border: 1px solid #dee2e6;\">");
        
        // Product Image (if available)
        if (product != null && product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            String imageUrl = product.getImageUrls().get(0);
            card.append("<div style=\"text-align: center; margin-bottom: 10px;\">")
                .append("<img src=\"").append(imageUrl)
                .append("\" alt=\"Product Image\" style=\"max-width: 200px; max-height: 200px; border-radius: 8px;\" />")
                .append("</div>");
        }
        
        // Product Name (bold and prominent)
        if (product != null) {
            card.append("<h4 style=\"margin: 10px 0; color: #000000; font-size: 16px; font-weight: bold;\">").append(safeGet(product.getName(), "Product")).append("</h4>");
        } else {
            card.append("<h4 style=\"margin: 10px 0; color: #dc3545; font-size: 16px; font-weight: bold;\">Product ID: ")
                .append(item.getProductId()).append("</h4>");
            card.append("<p style=\"color: #666666; font-style: italic;\">⚠️ Note: Product details not found in database. Please check manually.</p>");
        }
        
        // Product Details Table
        card.append("<table style=\"width: 100%; margin-top: 10px;\">");
        
        // Product ID
        card.append("<tr><td style=\"padding: 4px 0; width: 40%;\"><strong>Product ID:</strong></td><td>")
            .append(item.getProductId()).append("</td></tr>");
        
        // Product Name (if available)
        if (product != null && product.getName() != null && !product.getName().isBlank()) {
            card.append("<tr><td style=\"padding: 4px 0;\"><strong>Product Name:</strong></td><td>")
                .append(product.getName()).append("</td></tr>");
        }
        
        // Category Name (fetch from database)
        if (product != null && product.getCategoryId() != null && !product.getCategoryId().isBlank()) {
            Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
            if (category != null && category.getName() != null && !category.getName().isBlank()) {
                card.append("<tr><td style=\"padding: 4px 0;\"><strong>Category:</strong></td><td>")
                    .append(category.getName()).append("</td></tr>");
            }
        }
        
        // Color Information
        if (product != null && product.getColor() != null && !product.getColor().isBlank()) {
            card.append("<tr><td style=\"padding: 4px 0;\"><strong>Color:</strong></td><td>")
                .append(product.getColor()).append("</td></tr>");
        }
        
        // Stock Quantity
        if (product != null) {
            String stockStatus = product.getStockQuantity() > 0 
                ? product.getStockQuantity() + " units" 
                : "Out of Stock";
            String stockColor = product.getStockQuantity() > 0 ? "#000000" : "#dc3545";
            card.append("<tr><td style=\"padding: 4px 0;\"><strong>Stock:</strong></td><td style=\"color: ")
                .append(stockColor).append("; font-weight: bold;\">").append(stockStatus).append("</td></tr>");
        }
        
        // Quantity and Price
        card.append("<tr><td style=\"padding: 4px 0;\"><strong>Quantity:</strong></td><td>")
            .append(item.getQuantity()).append("</td></tr>");
        card.append("<tr><td style=\"padding: 4px 0;\"><strong>Unit Price:</strong></td><td>")
            .append(formatCurrency(item.getPrice())).append("</td></tr>");
        card.append("<tr><td style=\"padding: 4px 0;\"><strong>Line Total:</strong></td><td style=\"font-size: 16px; color: #000000; font-weight: bold;\">")
            .append(formatCurrency(itemTotal)).append("</td></tr>");
        card.append("</table>");
        
        // Product Description (if available)
        if (product != null && product.getDescription() != null && !product.getDescription().isBlank()) {
            card.append("<div style=\"margin-top: 10px; padding: 10px; background-color: #f8f9fa; border-radius: 4px; border: 1px solid #dee2e6;\">")
                .append("<strong style=\"color: #000000;\">Description:</strong><br>")
                .append("<span style=\"font-size: 14px; color: #666666;\">").append(product.getDescription()).append("</span>")
                .append("</div>");
        }
        
        card.append("</div>");
        return card.toString();
    }
}
