package ru.practicum.service.request;

import ru.practicum.model.request.dto.RequestDto;
import ru.practicum.model.request.dto.UpdatedEventRequestStatusesDto;
import ru.practicum.model.request.dto.UpdatedEventRequestStatusesResultDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(long userId, long eventId);

    RequestDto cancelRequest(long userId, long requestId);

    List<RequestDto> getRequestsByUserId(long userId);

    UpdatedEventRequestStatusesResultDto updateRequestsStates(long userId,
                                                              long eventId,
                                                              UpdatedEventRequestStatusesDto requestUpdatedDto);

    List<RequestDto> getRequestsByEventId(long userId, long eventId);
}
