package ru.practicum.model.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EventRequestParam {
    private List<Long> users;
    private List<EventState> states;
    private List<Long> categories;
    private List<Long> locations;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private int from;
    private int size;
}
