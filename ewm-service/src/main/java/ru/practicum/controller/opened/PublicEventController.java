package ru.practicum.controller.opened;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.model.event.EventShortRequestParam;
import ru.practicum.model.event.dto.EventDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.service.event.EventService;
import ru.practicum.statistic.EwmStatisticClient;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class PublicEventController {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventService eventService;
    private final EwmStatisticClient statisticClient;

    @GetMapping()
    public List<EventShortDto> getAllEvents(@RequestParam(required = false) String text,
                                            @RequestParam(required = false) List<Long> categories,
                                            @RequestParam(required = false) Boolean paid,
                                            @RequestParam(required = false) String rangeStart,
                                            @RequestParam(required = false) String rangeEnd,
                                            @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                            @RequestParam(required = false) String sort,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size,
                                            HttpServletRequest request) {
        EventShortRequestParam param = EventShortRequestParam.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();
        checkRange(param, rangeStart, rangeEnd);
        log.info("Get-запрос: получение публичных событий по параметрам: {}", param);
        String statResponse = String.valueOf(statisticClient
                .saveStatistic(request.getRemoteAddr(), request.getRequestURI()));
        log.debug("ответ от сервера статистики: {}", statResponse);
        return eventService.getPublicEventsByParam(param);
    }

    @GetMapping("/{id}")
    public EventDto getEventById(@PathVariable long id, HttpServletRequest request) {
        log.info("Get-запрос: получение публичного события с id {}", id);
        String statResponse = String.valueOf(statisticClient
                .saveStatistic(request.getRemoteAddr(), request.getRequestURI()).getStatusCode());
        log.debug("статус ответа от сервера статистики: {}", statResponse);
        long views = statisticClient.getStats(request.getRequestURI());
        return eventService.getPublicEventById(id, views);
    }

    private void checkRange(EventShortRequestParam param, String start, String end) {
        LocalDateTime defaultStart = LocalDateTime.parse("1199-12-31 23:59:59", formatter);
        LocalDateTime defaultEnd = LocalDateTime.parse("2199-12-31 23:59:59", formatter);
        if (start != null) {
            defaultStart = LocalDateTime.parse(start, formatter);
        }

        if (end != null) {
            defaultEnd = LocalDateTime.parse(end, formatter);
        }

        if (start == null && end == null) {
            defaultStart = LocalDateTime.now().plusSeconds(1);
        }

        param.setRangeStart(defaultStart);
        param.setRangeEnd(defaultEnd);
    }
}
