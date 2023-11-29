package ru.practicum.model.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.model.user.dto.UserShortDto;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
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
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    @JsonIgnore
    private long userId;
    @JsonIgnore
    private String userName;
    private boolean paid;
    private long views;

    public EventShortDto(long id, String title, String annotation, long catId,
                         String catName, long confirmedRequests, LocalDateTime eventDate,
                         long userId, String userName, boolean paid, long views) {
        this.id = id;
        this.title = title;
        this.annotation = annotation;
        this.catId = catId;
        this.catName = catName;
        this.confirmedRequests = confirmedRequests;
        this.eventDate = eventDate;
        this.userId = userId;
        this.userName = userName;
        this.paid = paid;
        this.views = views;
        this.category = new CategoryDto(catId, catName);
        this.initiator = new UserShortDto(userId, userName);
    }
}
