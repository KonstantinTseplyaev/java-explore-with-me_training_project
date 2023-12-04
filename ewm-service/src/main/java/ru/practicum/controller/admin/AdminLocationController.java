package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.model.location.dto.LocationDto;
import ru.practicum.service.event.EventService;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/locations")
public class AdminLocationController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDto addNewLocation(@RequestBody @Valid LocationDto locationDto) {
        log.info("Post-запрос: добавление админом новой локации - {}", locationDto);
        return eventService.addLocation(locationDto);
    }

    @PatchMapping("/{locId}")
    /*
    админ добавляет пользовательской локации имя и она переходит в разряд публичных,
    её могут видеть зарегистрированные пользователи и использовать в дальнейшем
    */
    public LocationDto updateLocation(@PathVariable long locId, @RequestBody LocationDto updatedLocationDto) {
        log.info("Patch-запрос: обновление локации с id {} для перевода её в публичный статус. " +
                "Новые данные - {}", locId, updatedLocationDto);
        updatedLocationDto.setId(locId);
        return eventService.updateLocation(updatedLocationDto);
    }
}
