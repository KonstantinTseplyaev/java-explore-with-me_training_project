package ru.practicum.service.location;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.LocationException;
import ru.practicum.exceptions.ModelNotFoundException;
import ru.practicum.mapper.MapperUtil;
import ru.practicum.model.location.Location;
import ru.practicum.model.location.dto.LocationDto;
import ru.practicum.repository.LocationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Override
    public LocationDto addLocation(LocationDto locationDto) {
        if (locationRepository.existsByLatAndLonAndRadius(locationDto.getLat(),
                locationDto.getLon(), locationDto.getRadius())) {
            throw new LocationException("Location already exists!");
        }
        Location location = locationRepository.save(MapperUtil.convertToLocation(locationDto));
        return MapperUtil.convertToLocationDto(location);
    }

    @Override
    public LocationDto addLocationNameForAdmin(LocationDto updatedLocationDto) {
        Location location = locationRepository.findByIdAndNameIsNull(updatedLocationDto.getId())
                .orElseThrow(() -> new ModelNotFoundException("Location with id " + updatedLocationDto.getId() +
                        " was not found"));
        location.setName(updatedLocationDto.getName());
        return MapperUtil.convertToLocationDto(locationRepository.save(location));
    }

    @Override
    public void deleteLocationById(long locId) {
        locationRepository.deleteById(locId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationDto> getAllLocations(int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        List<Location> locations = locationRepository.findByNameIsNotNull(page);
        return MapperUtil.convertList(locations, MapperUtil::convertToLocationDto);
    }

    @Override
    @Transactional(readOnly = true)
    public LocationDto getLocationById(long locId) {
        Location location = locationRepository.findByIdAndNameIsNotNull(locId)
                .orElseThrow(() -> new ModelNotFoundException("Location with id " + locId + " was not found"));
        return MapperUtil.convertToLocationDto(location);
    }

    @Override
    public LocationDto updatePublicLocation(LocationDto updatedLocDto) {
        Location location = locationRepository.findByIdAndNameIsNotNull(updatedLocDto.getId())
                .orElseThrow(() -> new ModelNotFoundException("Location with id " + updatedLocDto.getId() +
                        " was not found"));
        if (updatedLocDto.getName() != null) location.setName(updatedLocDto.getName());
        if (updatedLocDto.getLat() != 0.0) location.setLat(updatedLocDto.getLat());
        if (updatedLocDto.getLon() != 0.0) location.setLon(updatedLocDto.getLon());
        if (updatedLocDto.getRadius() != 0.0) location.setRadius(updatedLocDto.getRadius());
        return MapperUtil.convertToLocationDto(locationRepository.save(location));
    }
}
