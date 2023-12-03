package ru.practicum;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.dto.StatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.service.StatsServiceImpl;
import stat.dto.StatCreationDto;
import stat.dto.StatParams;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatisticController {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsServiceImpl service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveStats(@RequestBody StatCreationDto stat) {
        log.info("статистику принял: {}", stat);
        service.saveStatistic(stat);
    }

    @GetMapping("/stats")
    public List<StatDto> getStats(@RequestParam String start,
                                  @RequestParam String end,
                                  @RequestParam(required = false) String[] uris,
                                  @RequestParam(defaultValue = "false") boolean unique) {
        StatParams params = StatParams.builder()
                .start(LocalDateTime.parse(start, formatter))
                .end(LocalDateTime.parse(end, formatter))
                .uris(uris)
                .unique(unique)
                .build();
        log.info("статистику отдаю по заданным параметрам: {}", params);
        return service.getStatistic(params);
    }

    @ExceptionHandler(value = StatParamsException.class)
    public ResponseEntity<Map<String, String>> handleStatParamsExp(final StatParamsException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(400).body((Map.of("error", "Ошибка при указании параметров запроса",
                "errorMessage", exp.getMessage())));
    }
}
