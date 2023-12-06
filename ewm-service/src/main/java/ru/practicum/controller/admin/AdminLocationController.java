package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.model.location.dto.LocationDto;
import ru.practicum.service.location.LocationService;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/locations")
public class AdminLocationController {
    private final LocationService locationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDto addNewLocation(@RequestBody @Valid LocationDto locationDto) {
        log.info("Post-запрос: добавление админом новой локации - {}", locationDto);
        return locationService.addLocation(locationDto);
    }

    @PatchMapping("/{locId}/public")
    /*
    админ добавляет пользовательской локации имя и она переходит в разряд публичных,
    её могут видеть зарегистрированные пользователи и использовать в дальнейшем.
    Админ может добавить пользовательской локации только имя!
    */
    public LocationDto addLocationNameForAdmin(@PathVariable long locId,
                                               @RequestBody LocationDto updatedLocationDto) {
        log.info("Patch-запрос: добавление имени для локации с id {} для перевода её в публичный статус. " +
                "Новые данные - {}", locId, updatedLocationDto);
        updatedLocationDto.setId(locId);
        return locationService.addLocationNameForAdmin(updatedLocationDto);
    }

    @PatchMapping("/{locId}")
    /*
    полноценное обновление публичной локации
    */
    public LocationDto updatePublicLocation(@PathVariable long locId,
                                            @RequestBody LocationDto updatedLocationDto) {
        updatedLocationDto.setId(locId);
        log.info("Patch-запрос: обновление публичной локации, новые данные - {}", updatedLocationDto);
        return locationService.updatePublicLocation(updatedLocationDto);
    }

    @DeleteMapping("/{locId}")
    public void deleteLocationById(@PathVariable long locId) {
        log.info("Delete-запрос: удаление локации {} админом", locId);
        locationService.deleteLocationById(locId);
    }
}
