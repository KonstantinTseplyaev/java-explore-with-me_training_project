package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.statistic.EwmStatisticClient;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.model.compilation.dto.CompilationDto;
import ru.practicum.model.event.EventShortRequestParam;
import ru.practicum.model.event.dto.EventDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.service.compilation.CompilationService;
import ru.practicum.service.category.CategoryService;
import ru.practicum.service.event.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class PublicController {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final CompilationService compilationService;
    private final CategoryService categoryService;
    private final EventService eventService;
    private final EwmStatisticClient statisticClient;

    @GetMapping("/compilations")
    //2 запроса к бд + стримы
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) boolean pinned,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        log.info("Get-запрос: получение подборок pinned = {} событий", pinned);
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    //2 запроса к бд
    public CompilationDto getCompilationById(@PathVariable long compId) {
        log.info("Get-запрос: получение подборки по id {}", compId);
        return compilationService.getCompilationById(compId);
    }

    @GetMapping("/categories")
    //1 запрос к бд
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        log.info("Get-запрос: получение категорий");
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    //1 запрос к бд
    public CategoryDto getCategoryById(@PathVariable long catId) {
        log.info("Get-запрос: получение категории по id {}", catId);
        return categoryService.getCategoryById(catId);
    }

    @GetMapping("/events")
    //1 запрос к бд + 1 в сервис статистики
    public List<EventShortDto> getAllEvents(@RequestParam(required = false) String text,
                                            @RequestParam(required = false) Long[] categories,
                                            @RequestParam(required = false) Boolean paid,
                                            @RequestParam(required = false) String rangeStart,
                                            @RequestParam(required = false) String rangeEnd,
                                            @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                            @RequestParam(required = false) String sort,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size,
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
        log.info("ответ от сервера статистики: {}", statResponse);
        return eventService.getPublicEventsByParam(param);
    }

    @GetMapping("/events/{id}")
    //2 запроса к бд + 2 в сервис статистики
    public EventDto getEventById(@PathVariable long id, HttpServletRequest request) {
        log.info("Get-запрос: получение публичного события с id {}", id);
        String statResponse = String.valueOf(statisticClient
                .saveStatistic(request.getRemoteAddr(), request.getRequestURI()).getStatusCode());
        log.info("статус ответа от сервера статистики: {}", statResponse);
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
