package ru.practicum.service.location;

import ru.practicum.model.location.dto.LocationDto;

import java.util.List;

public interface LocationService {

    LocationDto addLocation(LocationDto locationDto);

    LocationDto addLocationNameForAdmin(LocationDto updatedLocationDto);

    void deleteLocationById(long locId);

    List<LocationDto> getAllLocations(int from, int size);

    LocationDto getLocationById(long locId);

    LocationDto updatePublicLocation(LocationDto updatedLocationDto);
}
