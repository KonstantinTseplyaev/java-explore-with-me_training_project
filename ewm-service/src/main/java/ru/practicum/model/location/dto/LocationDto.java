package ru.practicum.model.location.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    private long id;
    @Length(min = 1, max = 100)
    private String name;
    private double lat;
    private double lon;
    private double radius;
}
