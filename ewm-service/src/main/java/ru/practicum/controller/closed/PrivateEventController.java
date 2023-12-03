package ru.practicum.controller.closed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.model.event.dto.EventCreationDto;
import ru.practicum.model.event.dto.EventDto;
import ru.practicum.model.event.dto.UpdatedEventDto;
import ru.practicum.model.request.dto.RequestDto;
import ru.practicum.model.request.dto.UpdatedEventRequestStatusesDto;
import ru.practicum.model.request.dto.UpdatedEventRequestStatusesResultDto;
import ru.practicum.service.event.EventService;
import ru.practicum.service.request.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping()
    public List<EventDto> getEventsByUserId(@PathVariable long userId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get-запрос: получение событий, добавленных текущим пользователем с id {}", userId);
        return eventService.getEventsByUserId(userId, from, size);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@PathVariable long userId,
                                @RequestBody @Valid EventCreationDto eventCreationDto) {
        log.info("Post-запрос: добавление нового события {} пользователем {}", eventCreationDto, userId);
        return eventService.createEvent(userId, eventCreationDto);
    }

    @GetMapping("/{eventId}")
    public EventDto getEventById(@PathVariable long userId,
                                 @PathVariable long eventId) {
        log.info("Get-запрос: получение полной информации о событии {}, добавленном текущим пользователем с id {}",
                eventId, userId);
        return eventService.getEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEventById(@PathVariable long userId,
                                    @PathVariable long eventId,
                                    @RequestBody @Valid UpdatedEventDto eventDto) {
        log.info("Patch-запрос: изменение события {}, добавленного текущим пользователем {}. Новые данные - {}",
                eventId, userId, eventDto);
        return eventService.updateEvent(userId, eventId, eventDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequestsByEventId(@PathVariable long userId,
                                                 @PathVariable long eventId) {
        log.info("Get-запрос: получение информации о запросах на участие в событии {} текущего пользователя с id {}",
                eventId, userId);
        return requestService.getRequestsByEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public UpdatedEventRequestStatusesResultDto confirmRequestByEventId(@PathVariable long userId,
                                                                        @PathVariable long eventId,
                                                                        @RequestBody UpdatedEventRequestStatusesDto
                                                                                requestUpdatedDto) {
        log.info("Patch-запрос: изменение статуса(подтверждение/отмена) заявок на участие в событии {} " +
                "текущего пользователя {}. {}", eventId, userId, requestUpdatedDto);
        return requestService.updateRequestsStates(userId, eventId, requestUpdatedDto);
    }
}
