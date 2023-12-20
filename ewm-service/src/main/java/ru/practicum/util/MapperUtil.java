package ru.practicum.util;

import org.modelmapper.ModelMapper;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.compilation.dto.CompilationCreationDto;
import ru.practicum.model.compilation.dto.CompilationDto;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;
import ru.practicum.model.location.Location;
import ru.practicum.model.event.dto.EventCreationDto;
import ru.practicum.model.event.dto.EventDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.location.dto.LocationCreationDto;
import ru.practicum.model.location.dto.LocationDto;
import ru.practicum.model.location.dto.LocationForEventDto;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestState;
import ru.practicum.model.request.dto.RequestDto;
import ru.practicum.model.user.User;
import ru.practicum.model.user.dto.UserCreationDto;
import ru.practicum.model.user.dto.UserDto;
import ru.practicum.model.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapperUtil {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static <R, E> List<R> convertList(List<E> list, Function<E, R> converter) {
        return list.stream().map(converter).collect(Collectors.toList());
    }

    public static User convertToUser(UserCreationDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public static UserShortDto convertToUserShortDto(User user) {
        return modelMapper.map(user, UserShortDto.class);
    }

    public static Request convertToRequest(User requester, Event event, RequestState state) {
        return Request.builder()
                .requester(requester)
                .event(event)
                .created(LocalDateTime.now())
                .state(state)
                .build();
    }

    public static Event convertToEvent(Location loc, Category cat, User user, EventCreationDto eventDto) {
        return Event.builder()
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .category(cat)
                .description(eventDto.getDescription())
                .eventDate(eventDto.getEventDate())
                .initiator(user)
                .location(loc)
                .paid(eventDto.isPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.isRequestModeration())
                .createdOn(LocalDateTime.now())
                .publishedOn(null)
                .state(EventState.PENDING)
                .views(0)
                .build();
    }

    public static EventDto convertToEventDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(convertToCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(convertToUserShortDto(event.getInitiator()))
                .location(convertToLocationForEventDto(event.getLocation()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .publishedOn(event.getPublishedOn())
                .state(event.getState())
                .views(event.getViews())
                .build();
    }

    public static Location convertToLocation(LocationDto locationDto) {
        return modelMapper.map(locationDto, Location.class);
    }

    public static Location convertToLocation(LocationCreationDto locationDto) {
        return modelMapper.map(locationDto, Location.class);
    }

    public static LocationForEventDto convertToLocationForEventDto(Location location) {
        return new LocationForEventDto(location.getLat(), location.getLon());
    }

    public static LocationForEventDto convertToLocationForEventDto(LocationDto location) {
        return new LocationForEventDto(location.getLat(), location.getLon());
    }

    public static EventDto convertToEventDto(CategoryDto cat, UserShortDto initiator, LocationDto loc, Event event) {
        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(cat)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(initiator)
                .location(convertToLocationForEventDto(loc))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .publishedOn(event.getPublishedOn())
                .state(event.getState())
                .confirmedRequests(0)
                .views(event.getViews())
                .build();
    }

    public static EventDto convertToEventDto(Event event, int confReq) {
        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(convertToCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(convertToUserShortDto(event.getInitiator()))
                .location(convertToLocationForEventDto(event.getLocation()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .publishedOn(event.getPublishedOn())
                .state(event.getState())
                .confirmedRequests(confReq)
                .views(event.getViews())
                .build();
    }

    public static EventShortDto convertToEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .category(convertToCategoryDto(event.getCategory()))
                .initiator(convertToUserShortDto(event.getInitiator()))
                .paid(event.isPaid())
                .views(event.getViews())
                .build();
    }

    public static CategoryDto convertToCategoryDto(Category category) {
        return modelMapper.map(category, CategoryDto.class);
    }

    public static Category convertToCategory(CategoryDto categoryDto) {
        return modelMapper.map(categoryDto, Category.class);
    }

    public static LocationDto convertToLocationDto(Location location) {
        return modelMapper.map(location, LocationDto.class);
    }

    public static UserDto convertToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public static RequestDto convertToRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .status(request.getState())
                .created(request.getCreated())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .build();
    }

    public static Compilation convertToCompilation(List<Event> events, CompilationCreationDto compilationDto) {
        return Compilation.builder()
                .title(compilationDto.getTitle())
                .pinned(compilationDto.isPinned())
                .events(events)
                .build();
    }

    public static CompilationDto convertToCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(convertList(compilation.getEvents(), MapperUtil::convertToEventShortDto))
                .build();
    }
}
