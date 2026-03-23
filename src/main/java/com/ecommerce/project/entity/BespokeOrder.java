package com.ecommerce.project.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represents a made-to-measure/bespoke order for custom shirts
 */
@Document(collection = "bespoke_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BespokeOrder {
    
    @Id
    private String id;
    
    private String userId; // Customer who placed the order
    private String productId; // Base product (shirt design)
    private String selectedColor; // Color chosen by customer
    private String selectedDesign; // Design pattern chosen
    
    // Measurement option
    private MeasurementOption measurementOption;
    private CustomMeasurement customMeasurements; // If customer provides measurements
    private String sampleShippingTrackingId; // If customer sends sample shirt
    
    // Order details
    private double price;
    private OrderStatus status;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Customer notes
    private String customerNotes;
    
    public enum MeasurementOption {
        PROVIDE_MEASUREMENTS, // Customer provides body measurements
        SEND_SAMPLE_SHIRT     // Customer sends a sample shirt
    }
    
    public enum OrderStatus {
        PENDING_MEASUREMENTS,  // Waiting for measurements or sample
        MEASUREMENTS_RECEIVED, // Measurements received
        SAMPLE_RECEIVED,       // Sample shirt received
        IN_PRODUCTION,         // Being manufactured
        COMPLETED,             // Ready to ship
        SHIPPED,               // Shipped to customer
        DELIVERED,             // Delivered
        CANCELLED
    }
}
