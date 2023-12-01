package ru.practicum.controller.closed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.model.request.dto.RequestDto;
import ru.practicum.service.request.RequestService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class PrivateRequestController {
    private final RequestService requestService;

    @GetMapping()
    public List<RequestDto> getRequestsByUserId(@PathVariable long userId) {
        log.info("Get-запрос: получение информации о заявках текущего пользователя {} " +
                "на участие в чужих событиях", userId);
        return requestService.getRequestsByUserId(userId);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable long userId,
                                    @RequestParam long eventId) {
        log.info("Post-запрос: добавление запроса от текущего пользователя с id {} " +
                "на участие в чужом событии с id {}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequestByRequestId(@PathVariable long userId,
                                               @PathVariable long requestId) {
        log.info("Patch-запрос: отмена запроса пользователя {} на участие в событии {}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}
