package ru.practicum.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.ModelNotFoundException;
import ru.practicum.exceptions.RequestEventException;
import ru.practicum.mapper.MapperUtil;
import ru.practicum.model.event.Event;
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
        Request request = requestRepository.findByRequesterIdAndId(userId, requestId)
                .orElseThrow(() -> new ModelNotFoundException("Request with id=" + requestId + " was not found"));
        request.setState(CANCELED);
        return MapperUtil.convertToRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequestsByUserId(long userId) {
        return MapperUtil.convertList(requestRepository.findByRequesterId(userId), MapperUtil::convertToRequestDto);
    }

    @Override
    public UpdatedEventRequestStatusesResultDto updateRequestsStates(long userId, long eventId,
                                                                     UpdatedEventRequestStatusesDto requestUpdatedDto) {
        Event event = eventRepository.findEventByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new ModelNotFoundException("User with id=" + userId + " doesn't have an event " +
                        "with id=" + eventId));

        int confirmedRequests = requestRepository.countAllByEventIdAndState(eventId, CONFIRMED);

        RequestState actualState = requestUpdatedDto.getStatus();
        List<Long> ids = requestUpdatedDto.getRequestIds();
        if (actualState == REJECTED) {
            int result = requestRepository.updateRequestByIds(eventId, ids, REJECTED.name());
            if (result == 0) {
                throw new RequestEventException("Request must have status PENDING");
            }
        } else if (actualState == CONFIRMED) {
            if (event.getParticipantLimit() == confirmedRequests) {
                throw new RequestEventException("The participant limit has been reached");
            }
            long vacant = event.getParticipantLimit() - confirmedRequests;
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

        List<Request> updatedRequests = requestRepository.findByEventIdAndIdIn(eventId, ids);
        Map<RequestState, List<Request>> requestMap = updatedRequests.stream()
                .collect(Collectors.groupingBy(Request::getState));
        List<RequestDto> confReq = MapperUtil
                .convertList(requestMap.getOrDefault(CONFIRMED, List.of()), MapperUtil::convertToRequestDto);
        List<RequestDto> rejReq = MapperUtil
                .convertList(requestMap.getOrDefault(REJECTED, List.of()), MapperUtil::convertToRequestDto);
        return new UpdatedEventRequestStatusesResultDto(confReq, rejReq);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequestsByEventId(long userId, long eventId) {
        List<Request> requests = requestRepository.findByEventId(eventId);
        if (requests.isEmpty())
            throw new ModelNotFoundException("User with id=" + userId + " doesn't have an event with id=" + eventId);
        return MapperUtil.convertList(requests, MapperUtil::convertToRequestDto);
    }
}
