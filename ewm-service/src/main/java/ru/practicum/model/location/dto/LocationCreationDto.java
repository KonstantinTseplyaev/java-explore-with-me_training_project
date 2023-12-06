package ru.practicum.model.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationCreationDto {
    private Long id;
    private Double lat;
    private Double lon;
    private Double radius;

    public LocationCreationDto(Long id) {
        this.id = id;
    }

    public LocationCreationDto(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public LocationCreationDto(Double lat, Double lon, Double radius) {
        this.lat = lat;
        this.lon = lon;
        this.radius = radius;
    }
}
