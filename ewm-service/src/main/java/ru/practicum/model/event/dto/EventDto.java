package ru.practicum.model.event.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.model.event.EventState;
import ru.practicum.model.location.dto.LocationForEventDto;
import ru.practicum.model.user.dto.UserShortDto;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    private long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private long confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private LocationForEventDto location;
    private boolean paid;
    private int participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private EventState state;
    private long views;
}
