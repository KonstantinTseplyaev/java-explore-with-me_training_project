package ru.practicum.model.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.model.location.dto.LocationCreationDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventCreationDto {
    @Length(min = 3, max = 120)
    @NotNull
    private String title;
    @Length(min = 20, max = 2000)
    @NotNull
    private String annotation;
    @PositiveOrZero
    @NotNull
    private long category;
    @Length(min = 20, max = 7000)
    @NotNull
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Future
    @NotNull
    private LocalDateTime eventDate;
    @NotNull
    private LocationCreationDto location;
    @Builder.Default
    private boolean paid = false;
    @Builder.Default
    private int participantLimit = 0;
    @Builder.Default
    private boolean requestModeration = true;
}
