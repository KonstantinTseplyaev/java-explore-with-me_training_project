package ru.practicum.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.ModelNotFoundException;
import ru.practicum.exceptions.RequestEventException;
import ru.practicum.mapper.MapperUtil;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.dto.EventDto;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestState;
import ru.practicum.model.request.dto.RequestDto;
import ru.practicum.model.request.dto.UpdatedEventRequestStatusesDto;
import ru.practicum.model.request.dto.UpdatedEventRequestStatusesResultDto;
import ru.practicum.model.user.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.model.event.EventState.PUBLISHED;
import static ru.practicum.model.request.RequestState.CANCELED;
import static ru.practicum.model.request.RequestState.CONFIRMED;
import static ru.practicum.model.request.RequestState.PENDING;
import static ru.practicum.model.request.RequestState.REJECTED;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {
    public static final String REQUEST_DTO_REQUEST = "select new ru.practicum.model.request.dto.RequestDto(" +
            "r.id, " +
            "r.event.id, " +
            "r.requester.id, " +
            "r.created, " +
            "r.state) " +
            "from Request as r ";
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public RequestDto createRequest(long userId, long eventId) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new RequestEventException("Can't resend request!");
        }

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new ModelNotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository
                .findByIdAndStateAndInitiatorIdIsNot(eventId, PUBLISHED, userId)
                .orElseThrow(() -> new RequestEventException("Event with id=" + eventId + " was not found"));

        int confirmedRequest = requestRepository.countAllByEventIdAndState(event.getId(), CONFIRMED);
        if (confirmedRequest == event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new RequestEventException("The participant limit has been reached");
        }

        RequestState state = PENDING;

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            state = CONFIRMED;
        }

        Request request = MapperUtil.convertToRequest(requester, event, state);
        return MapperUtil.convertToRequestDto(requestRepository.save(request));
    }

    @Override
    public RequestDto cancelRequest(long userId, long requestId) {
        RequestDto request = requestRepository.findRequestForCancel(requestId, userId)
                .orElseThrow(() -> new ModelNotFoundException("Request with id=" + requestId + " was not found"));
        request.setStatus(CANCELED);
        requestRepository.updateRequestById(requestId, CANCELED.name());
        return request;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequestsByUserId(long userId) {
        return requestRepository.findByRequesterId(userId);
    }

    @Override
    public UpdatedEventRequestStatusesResultDto updateRequestsStates(long userId, long eventId,
                                                                     UpdatedEventRequestStatusesDto requestUpdatedDto) {
        EventDto event = eventRepository.findEventByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new ModelNotFoundException("User with id=" + userId + " doesn't have an event " +
                        "with id=" + eventId));

        RequestState actualState = requestUpdatedDto.getStatus();
        List<Long> ids = requestUpdatedDto.getRequestIds();
        if (actualState == REJECTED) {
            int result = requestRepository.updateRequestByIds(eventId, ids, REJECTED.name());
            if (result == 0) {
                throw new RequestEventException("Request must have status PENDING");
            }
        } else if (actualState == CONFIRMED) {
            if (event.getParticipantLimit() == event.getConfirmedRequests()) {
                throw new RequestEventException("The participant limit has been reached");
            }
            long vacant = event.getParticipantLimit() - event.getConfirmedRequests();
            if (vacant >= ids.size()) {
                requestRepository.updateRequestByIds(eventId, ids, CONFIRMED.name());
            } else {
                List<Long> confIds = ids.subList(0, (int) (vacant - 1));
                List<Long> rejIds = ids.subList((int) vacant, ids.size() - 1);
                requestRepository.updateRequestByIds(eventId, confIds, CONFIRMED.name());
                requestRepository.updateRequestByIds(eventId, rejIds, REJECTED.name());
            }
        } else {
            throw new RuntimeException("Unsupported state!");
        }
        List<RequestDto> requests = requestRepository.findUpdatedRequestsByEvent(eventId, ids);
        Map<RequestState, List<RequestDto>> requestMap = requests.stream()
                .collect(Collectors.groupingBy(RequestDto::getStatus));
        return new UpdatedEventRequestStatusesResultDto(requestMap.get(CONFIRMED), requestMap.get(REJECTED));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequestsByEventId(long userId, long eventId) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId))
            throw new ModelNotFoundException("User with id=" + userId + " doesn't have an event with id=" + eventId);
        return requestRepository.findByEventId(eventId);
    }
}
