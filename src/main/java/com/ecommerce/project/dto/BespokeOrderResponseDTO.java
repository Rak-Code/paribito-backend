package com.ecommerce.project.dto;

import com.ecommerce.project.entity.BespokeOrder;
import java.time.LocalDateTime;

public record BespokeOrderResponseDTO(
        String id,
        String userId,
        String productId,
        String selectedColor,
        String selectedDesign,
        BespokeOrder.MeasurementOption measurementOption,
        CustomMeasurementDTO customMeasurements,
        String sampleShippingTrackingId,
        double price,
        BespokeOrder.OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String customerNotes
) {}
