package ru.practicum.model.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EventShortRequestParam {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    @Builder.Default
    private LocalDateTime rangeStart = null;
    @Builder.Default
    private LocalDateTime rangeEnd = null;
    private boolean onlyAvailable;
    private String sort;
    private int from;
    private int size;
}
