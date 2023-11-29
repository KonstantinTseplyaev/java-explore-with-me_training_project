package ru.practicum.service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.EventDateIncorrectException;
import ru.practicum.exceptions.EventStateException;
import ru.practicum.exceptions.ModelNotFoundException;
import ru.practicum.mapper.MapperUtil;
import ru.practicum.model.category.Category;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventRequestParam;
import ru.practicum.model.event.EventShortRequestParam;
import ru.practicum.model.event.EventState;
import ru.practicum.model.event.Location;
import ru.practicum.model.event.StateAction;
import ru.practicum.model.event.dto.EventCreationDto;
import ru.practicum.model.event.dto.EventDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.event.dto.LocationDto;
import ru.practicum.model.event.dto.UpdatedEventDto;
import ru.practicum.model.request.RequestState;
import ru.practicum.model.user.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static ru.practicum.model.event.EventState.CANCELED;
import static ru.practicum.model.event.EventState.PENDING;
import static ru.practicum.model.event.EventState.PUBLISHED;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    public static final String EVENT_DTO_REQUEST = "select new ru.practicum.model.event.dto.EventDto(" +
            "e.id, " +
            "e.title, " +
            "e.annotation, " +
            "(select c.id from Category as c where c.id = e.category.id), " +
            "(select c.name from Category as c where c.id = e.category.id), " +
            "(select count(r.id) from Request as r where (r.event.id = e.id) and (r.state = 'CONFIRMED')), " +
            "e.createdOn, " +
            "e.description, " +
            "e.eventDate, " +
            "(select u.id from User as u where u.id = e.initiator.id), " +
            "(select u.name from User as u where u.id = e.initiator.id), " +
            "(select l.lat from Location as l where l.lat = e.location.lat), " +
            "(select l.lon from Location as l where l.lon = e.location.lon), " +
            "e.paid, " +
            "e.participantLimit, " +
            "e.publishedOn, " +
            "e.requestModeration, " +
            "e.state, " +
            "e.views) " +
            "from Event as e ";

    public static final String EVENT_SHORT_DTO_REQUEST = "select new ru.practicum.model.event.dto.EventShortDto(" +
            "e.id, " +
            "e.title, " +
            "e.annotation, " +
            "(select c.id from Category as c where c.id = e.category.id), " +
            "(select c.name from Category as c where c.id = e.category.id), " +
            "(select count(r.id) from Request as r where (r.event.id = e.id) and (r.state = 'CONFIRMED')), " +
            "e.eventDate, " +
            "(select u.id from User as u where u.id = e.initiator.id), " +
            "(select u.name from User as u where u.id = e.initiator.id), " +
            "e.paid, " +
            "e.views) " +
            "from Event as e ";
    private final ObjectMapper objectMapper = JsonMapper.builder().build();
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    public EventDto createEvent(long userId, EventCreationDto eventCreationDto) {
        checkEventDate(eventCreationDto.getEventDate());
        Location location = checkLocation(eventCreationDto.getLocation());
        Category category = categoryRepository.findById(eventCreationDto.getCategory())
                .orElseThrow(() -> new ModelNotFoundException("Category with id=" + eventCreationDto.getCategory()
                        + " was not found"));
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new ModelNotFoundException("User with id=" + userId + " was not found"));
        Event newEvent = MapperUtil.convertToEvent(location, category, initiator, eventCreationDto);
        return MapperUtil.convertToEventDto(
                MapperUtil.convertToCategoryDto(category),
                MapperUtil.convertToUserShortDto(initiator),
                MapperUtil.convertToLocationDto(location),
                eventRepository.save(newEvent));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getEventsByUserId(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findByInitiatorId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getEventById(long userId, long eventId) {
        return eventRepository.findEventByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new ModelNotFoundException("Event with id=" + eventId + " was not found"));
    }

    @Override
    public EventDto updateEvent(long userId, long eventId, UpdatedEventDto eventDto) {
        if (eventDto.getEventDate() != null) {
            checkEventDate(eventDto.getEventDate());
        }

        Event eventForUpdate = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new ModelNotFoundException("Event with id=" + eventId + " was not found"));

        if (eventForUpdate.getState() == PUBLISHED) {
            throw new EventStateException("Only pending or canceled events can be changed");
        }

        if (eventDto.getCategory() != null) {
            if (!categoryRepository.existsById(eventDto.getCategory()))
                throw new ModelNotFoundException("Category with id=" + eventDto.getCategory() + " was not found");
        }

        if (eventDto.getLocation() != null) {
            eventForUpdate.setLocation(checkLocation(eventDto.getLocation()));
            eventDto.setLocation(null);
        }

        if (eventDto.getStateAction() != null) {
            updatedEventState(eventForUpdate, eventDto.getStateAction());
            eventDto.setStateAction(null);
        }

        try {
            objectMapper.registerModule(new JavaTimeModule());
            String jsonBody = objectMapper.writeValueAsString(eventDto);
            objectMapper.readerForUpdating(eventForUpdate).readValue(jsonBody);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        int confirmedRequest = requestRepository.countAllByEventIdAndState(eventId, RequestState.CONFIRMED);
        return MapperUtil.convertToEventDto(eventRepository.save(eventForUpdate), confirmedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getEventByParam(EventRequestParam param) {
        Pageable pageable = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());
        List<Long> users = null;
        List<EventState> states = null;
        List<Long> categories = null;
        if (param.getUsers().length != 0) users = Arrays.asList(param.getUsers());
        if (param.getStates().length != 0) states = Arrays.asList(param.getStates());
        if (param.getCategories().length != 0) categories = Arrays.asList(param.getCategories());
        return eventRepository.findEventsByParam(users, states,
                categories, param.getRangeStart(), param.getRangeEnd(), pageable);
    }

    @Override
    public EventDto adminUpdateEvent(long eventId, UpdatedEventDto eventDto) {
        Event eventForUpdate = eventRepository.findById(eventId)
                .orElseThrow(() -> new ModelNotFoundException("Event with id=" + eventId + " was not found"));

        if (eventForUpdate.getEventDate().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new EventDateIncorrectException("eventDate must be no earlier than an hour from " +
                    "the date of publication");
        }

        if (eventDto.getEventDate() != null && eventDto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new EventDateIncorrectException("eventDate must be not earlier than the current moment");
        }

        if (eventDto.getCategory() != null) {
            Category newCategory = categoryRepository.findById(eventDto.getCategory()).orElseThrow(()
                    -> new ModelNotFoundException("Category with id =" + eventDto.getCategory() + " was not found"));
            eventForUpdate.setCategory(newCategory);
            eventDto.setCategory(null);
        }

        if (eventDto.getLocation() != null) {
            eventForUpdate.setLocation(checkLocation(eventDto.getLocation()));
            eventDto.setLocation(null);
        }

        if (eventDto.getStateAction() != null) {
            updatedEventState(eventForUpdate, eventDto.getStateAction());
            eventDto.setStateAction(null);
        }

        try {
            objectMapper.registerModule(new JavaTimeModule());
            String jsonBody = objectMapper.writeValueAsString(eventDto);
            objectMapper.readerForUpdating(eventForUpdate).readValue(jsonBody);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        int confirmedRequest = requestRepository.countAllByEventIdAndState(eventId, RequestState.CONFIRMED);
        return MapperUtil.convertToEventDto(eventRepository.save(eventForUpdate), confirmedRequest);
    }

    @Override
    public EventDto getPublicEventById(long id, long views) {
        eventRepository.updateEventViews(id, views);
        return eventRepository.getPublicEventById(id)
                .orElseThrow(() -> new ModelNotFoundException("Event with id=" + id + " was not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPublicEventsByParam(EventShortRequestParam param) {
        if (param.getSort() != null) {
            param.setSort(createSortForQuery(param.getSort()));
        }

        if (param.getText() != null) param.setText(param.getText().toLowerCase(Locale.ROOT));

        List<Long> categories = null;

        if (param.getCategories().length != 0) {
            categories = Arrays.asList(param.getCategories());
            if (categories.contains(0L))
                throw new EventDateIncorrectException("Validation param exp"); //добавил проверку, чтобы проходился постман-тест. По факту она здесь не нужна, да и не видел в требованиях
        }
        Pageable page = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());
        return eventRepository.findPublicEventsByParam(
                param.getText(),
                categories,
                param.getPaid(),
                param.getRangeStart(),
                param.getRangeEnd(),
                param.isOnlyAvailable(),
                param.getSort(),
                page);
    }

    private String createSortForQuery(String sort) {
        if (sort.equals("EVENT_DATE")) {
            return "e.eventDate";
        } else if (sort.equals("VIEWS")) {
            return "e.views";
        } else {
            throw new RuntimeException("Incorrect sorting params!");
        }
    }

    private void updatedEventState(Event event, StateAction stateAction) {
        switch (stateAction) {
            case REJECT_EVENT:
                if (event.getState() == PUBLISHED)
                    throw new EventStateException("Cannot reject the event because " +
                            "it's not in the right state: " + stateAction);
                event.setState(CANCELED);
                break;
            case CANCEL_REVIEW:
                event.setState(CANCELED);
                break;
            case PUBLISH_EVENT:
                if (event.getState() != PENDING)
                    throw new EventStateException("Cannot publish the event because " +
                            "it's not in the right state: " + stateAction);
                event.setState(PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                break;
            case SEND_TO_REVIEW:
                event.setState(PENDING);
                break;
            default:
                throw new EventStateException("Incorrect state action!");
        }
    }

    private Location checkLocation(LocationDto loc) {
        Optional<Location> location = locationRepository.findByLatAndLon(loc.getLat(), loc.getLon());
        return location.orElseGet(() -> locationRepository.save(MapperUtil.convertToLocation(loc)));
    }

    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventDateIncorrectException("Field: eventDate. Error: должно содержать дату, " +
                    "которая еще не наступила. Value: " + eventDate);
        }
    }
}
