package ru.practicum.model.event.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.model.event.EventState;
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
    @JsonIgnore
    private long catId;
    @JsonIgnore
    private String catName;
    private long confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    @JsonIgnore
    private long userId;
    @JsonIgnore
    private String userName;
    private LocationDto location;
    @JsonIgnore
    private double locLat;
    @JsonIgnore
    private double locLon;
    private boolean paid;
    private int participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private EventState state;
    private long views;

    public EventDto(long id, String title, String annotation, long catId, String catName, long confirmedRequests,
                    LocalDateTime createdOn, String description, LocalDateTime eventDate, long userId,
                    String userName, double locLat, double locLon, boolean paid, int participantLimit,
                    LocalDateTime publishedOn, boolean requestModeration, EventState state, long views) {
        this.id = id;
        this.title = title;
        this.annotation = annotation;
        this.catId = catId;
        this.catName = catName;
        this.confirmedRequests = confirmedRequests;
        this.createdOn = createdOn;
        this.description = description;
        this.eventDate = eventDate;
        this.userId = userId;
        this.userName = userName;
        this.locLat = locLat;
        this.locLon = locLon;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.requestModeration = requestModeration;
        this.state = state;
        this.views = views;
        this.initiator = new UserShortDto(userId, userName);
        this.category = new CategoryDto(catId, catName);
        this.location = new LocationDto(locLat, locLon);
    }
}
