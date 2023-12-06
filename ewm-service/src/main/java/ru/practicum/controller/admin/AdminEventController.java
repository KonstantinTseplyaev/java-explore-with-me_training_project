package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.model.event.EventRequestParam;
import ru.practicum.model.event.EventState;
import ru.practicum.model.event.dto.EventDto;
import ru.practicum.model.event.dto.UpdatedEventDto;
import ru.practicum.service.event.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventController {
    private final EventService eventService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping()
    public List<EventDto> getEvents(@RequestParam(required = false) List<Long> users,  //добавил для админа возможность поиска по локации
                                    @RequestParam(required = false) List<EventState> states,
                                    @RequestParam(required = false) List<Long> categories,
                                    @RequestParam(required = false) List<Long> locations,
                                    @RequestParam(defaultValue = "1900-01-01 01:01:01") String rangeStart,
                                    @RequestParam(defaultValue = "2199-12-31 23:59:59") String rangeEnd,
                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                    @RequestParam(defaultValue = "10") @Positive int size) {
        EventRequestParam param = EventRequestParam.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .locations(locations)
                .rangeStart(LocalDateTime.parse(rangeStart, formatter))
                .rangeEnd(LocalDateTime.parse(rangeEnd, formatter))
                .from(from)
                .size(size)
                .build();
        log.info("Get-запрос: получение информации обо всех событиях, подходящих под переданные условия: {}", param);
        return eventService.getEventByParam(param);
    }

    @PatchMapping("/{eventId}")
    public EventDto adminUpdateEvent(@PathVariable long eventId,
                                     @RequestBody @Valid UpdatedEventDto eventDto) {
        log.info("Patch-запрос: обновление события с id {}. Новые данные - {}", eventId, eventDto);
        return eventService.adminUpdateEvent(eventId, eventDto);
    }
}
