package ru.practicum.controller;

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
import ru.practicum.service.request.RequestService;
import ru.practicum.service.event.EventService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}")
public class PrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping("/events")
    //1 запрос к бд
    public List<EventDto> getEventsByUserId(@PathVariable long userId,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) {
        log.info("Get-запрос: получение событий, добавленных текущим пользователем с id {}", userId);
        return eventService.getEventsByUserId(userId, from, size);
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    //4 (в худшем случае 5 запросов к бд)
    public EventDto createEvent(@PathVariable long userId,
                                @RequestBody @Valid EventCreationDto eventCreationDto) {
        log.info("Post-запрос: добавление нового события {} пользователем {}", eventCreationDto, userId);
        return eventService.createEvent(userId, eventCreationDto);
    }

    @GetMapping("/events/{eventId}")
    //1 запрос к бд
    public EventDto getEventById(@PathVariable long userId,
                                 @PathVariable long eventId) {
        log.info("Get-запрос: получение полной информации о событии {}, добавленном текущим пользователем с id {}", eventId, userId);
        return eventService.getEventById(userId, eventId);
    }

    @PatchMapping("/events/{eventId}")
    //3 (в худшем случае 4 запроса к бд)
    public EventDto updateEventById(@PathVariable long userId,
                                    @PathVariable long eventId,
                                    @RequestBody @Valid UpdatedEventDto eventDto) {
        log.info("Patch-запрос: изменение события {}, добавленного текущим пользователем {}. Новые данные - {}", eventId, userId, eventDto);
        return eventService.updateEvent(userId, eventId, eventDto);
    }

    @GetMapping("/events/{eventId}/requests")
    //2 запроса к бд
    public List<RequestDto> getRequestsByEventId(@PathVariable long userId,
                                                 @PathVariable long eventId) {
        log.info("Get-запрос: получение информации о запросах на участие в событии {} текущего пользователя с id {}", eventId, userId);
        return requestService.getRequestsByEventId(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    //3 (в худшем случае 5 запросов к бд)
    public UpdatedEventRequestStatusesResultDto confirmRequestByEventId(@PathVariable long userId,
                                                                        @PathVariable long eventId,
                                                                        @RequestBody UpdatedEventRequestStatusesDto
                                                                                requestUpdatedDto) {
        log.info("Patch-запрос: изменение статуса(подтверждение/отмена) заявок на участие в событии {} текущего пользователя {}. {}", eventId, userId, requestUpdatedDto);
        return requestService.updateRequestsStates(userId, eventId, requestUpdatedDto);
    }

    @GetMapping("/requests")
    //1 запрос к бд
    public List<RequestDto> getRequestsByUserId(@PathVariable long userId) {
        log.info("Get-запрос: получение информации о заявках текущего пользователя {} на участие в чужих событиях", userId);
        return requestService.getRequestsByUserId(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    //7 (в худшем случае 8 запросов к бд)
    public RequestDto createRequest(@PathVariable long userId,
                                    @RequestParam long eventId) {
        log.info("Post-запрос: добавление запроса от текущего пользователя с id {} на участие в чужом событии с id {}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    //2 (в худшем случае 3 запроса к бд)
    public RequestDto cancelRequestByRequestId(@PathVariable long userId,
                                               @PathVariable long requestId) {
        log.info("Patch-запрос: отмена запроса пользователя {} на участие в событии {}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}
