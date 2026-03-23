package com.ecommerce.project.dto;

import com.ecommerce.project.entity.CustomMeasurement;

public record CustomMeasurementDTO(
        Double chest,
        Double waist,
        Double shoulder,
        Double sleeveLength,
        Double shirtLength,
        Double neck,
        Double bicep,
        Double wrist,
        CustomMeasurement.MeasurementUnit unit,
        String notes
) {
    public CustomMeasurement toEntity() {
        CustomMeasurement measurement = new CustomMeasurement();
        measurement.setChest(chest);
        measurement.setWaist(waist);
        measurement.setShoulder(shoulder);
        measurement.setSleeveLength(sleeveLength);
        measurement.setShirtLength(shirtLength);
        measurement.setNeck(neck);
        measurement.setBicep(bicep);
        measurement.setWrist(wrist);
        measurement.setUnit(unit);
        measurement.setNotes(notes);
        return measurement;
    }
    
    public static CustomMeasurementDTO fromEntity(CustomMeasurement measurement) {
        if (measurement == null) return null;
        return new CustomMeasurementDTO(
            measurement.getChest(),
            measurement.getWaist(),
            measurement.getShoulder(),
            measurement.getSleeveLength(),
            measurement.getShirtLength(),
            measurement.getNeck(),
            measurement.getBicep(),
            measurement.getWrist(),
            measurement.getUnit(),
            measurement.getNotes()
        );
    }
}
