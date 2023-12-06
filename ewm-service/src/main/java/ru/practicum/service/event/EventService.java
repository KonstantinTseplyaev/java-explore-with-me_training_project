package ru.practicum.service.event;

import ru.practicum.model.event.EventRequestParam;
import ru.practicum.model.event.EventShortRequestParam;
import ru.practicum.model.event.dto.EventCreationDto;
import ru.practicum.model.event.dto.EventDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.event.dto.UpdatedEventDto;

import java.util.List;

public interface EventService {

    EventDto createEvent(long userId, EventCreationDto eventCreationDto);

    List<EventDto> getEventsByUserId(long userId, int from, int size);

    EventDto getEventById(long userId, long eventId);

    EventDto updateEvent(long userId, long eventId, UpdatedEventDto eventDto);

    List<EventDto> getEventByParam(EventRequestParam param);

    EventDto adminUpdateEvent(long eventId, UpdatedEventDto eventDto);

    EventDto getPublicEventById(long id, long views);

    List<EventShortDto> getPublicEventsByParam(EventShortRequestParam param);

    List<EventDto> getEventsByLocationZone(long zoneId);
}
