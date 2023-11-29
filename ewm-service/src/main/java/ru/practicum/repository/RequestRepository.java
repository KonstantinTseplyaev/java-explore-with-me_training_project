package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestState;
import ru.practicum.model.request.dto.RequestDto;

import java.util.List;
import java.util.Optional;

import static ru.practicum.service.request.RequestServiceImpl.REQUEST_DTO_REQUEST;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    boolean existsByRequesterIdAndEventId(long userId, long eventId);

    @Query(REQUEST_DTO_REQUEST + "where r.id = :requestId and r.requester.id = :userId and r.state is not 'CANCELED'")
    Optional<RequestDto> findRequestForCancel(long requestId, long userId);

    @Query(REQUEST_DTO_REQUEST + "where r.requester.id = :userId")
    List<RequestDto> findByRequesterId(long userId);

    @Query(REQUEST_DTO_REQUEST + "where r.event.id = :eventId")
    List<RequestDto> findByEventId(long eventId);

    @Modifying
    @Query(value = "update requests set state = :actualState where event_id = :eventId " +
            "and state = 'PENDING' " +
            "and id in :requestIds", nativeQuery = true)
    int updateRequestByIds(long eventId, List<Long> requestIds, String actualState);

    @Query(REQUEST_DTO_REQUEST + "where r.event.id = :eventId and r.id in :ids")
    List<RequestDto> findUpdatedRequestsByEvent(long eventId, List<Long> ids);

    @Modifying
    @Query(value = "update requests set state = :actualState " +
            "where id = :requestId", nativeQuery = true)
    void updateRequestById(long requestId, String actualState);

    int countAllByEventIdAndState(long eventId, RequestState state);

    @Query(REQUEST_DTO_REQUEST + "where r.state = 'CONFIRMED'")
    List<RequestDto> findAllConfirmRequest();
}
