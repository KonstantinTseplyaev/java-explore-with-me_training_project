package ru.practicum.model.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventShortRequestParam {
    private String text;
    @Builder.Default
    private Long[] categories = {};
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
