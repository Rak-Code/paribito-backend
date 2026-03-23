package com.ecommerce.project.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents custom body measurements for made-to-measure/bespoke orders
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomMeasurement {
    
    // Shirt measurements (in cm or inches based on unit)
    private Double chest;
    private Double waist;
    private Double shoulder;
    private Double sleeveLength;
    private Double shirtLength;
    private Double neck;
    private Double bicep;
    private Double wrist;
    
    // Measurement unit
    private MeasurementUnit unit;
    
    // Additional notes from customer
    private String notes;
    
    public enum MeasurementUnit {
        CM, INCHES
    }
}
