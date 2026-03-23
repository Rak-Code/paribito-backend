package com.ecommerce.project.dto;

import com.ecommerce.project.entity.BespokeOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BespokeOrderRequestDTO(
        @NotBlank(message = "Product ID is required")
        String productId,
        
        @NotBlank(message = "Selected color is required")
        String selectedColor,
        
        String selectedDesign,
        
        @NotNull(message = "Measurement option is required")
        BespokeOrder.MeasurementOption measurementOption,
        
        CustomMeasurementDTO customMeasurements,
        
        String customerNotes
) {}
