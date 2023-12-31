package ru.practicum.controller.opened;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.model.location.dto.LocationDto;
import ru.practicum.service.location.LocationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/locations")
public class PublicLocationController {
    private final LocationService locationService;

    @GetMapping
    public List<LocationDto> getAllLocations(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get-запрос: получение всех добавленных админом локаций");
        return locationService.getAllLocations(from, size);
    }

    @GetMapping("/{locId}")
    public LocationDto getLocationById(@PathVariable long locId) {
        log.info("Get-запрос: получение добавленной админом локации по id {}", locId);
        return locationService.getLocationById(locId);
    }
}
