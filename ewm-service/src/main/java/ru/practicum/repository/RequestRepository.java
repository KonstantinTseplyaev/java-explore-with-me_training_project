package ru.practicum.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestState;
import ru.practicum.model.request.dto.RequestDto;

import java.util.List;
import java.util.Optional;


@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    boolean existsByRequesterIdAndEventId(long userId, long eventId);

    List<Request> findByRequesterId(long userId);

    List<Request> findByEventId(long eventId);

    @Modifying
    @Query(value = "update requests set state = :actualState where event_id = :eventId " +
            "and state = 'PENDING' " +
            "and id in :requestIds", nativeQuery = true)
    int updateRequestByIds(long eventId, List<Long> requestIds, String actualState);


    int countAllByEventIdAndState(long eventId, RequestState state);

    @Query("select new ru.practicum.model.request.dto.RequestDto(" +
            "r.id, " +
            "r.event.id, " +
            "r.requester.id, " +
            "r.created, " +
            "r.state) " +
            "from Request as r where r.state = 'CONFIRMED'")
    List<RequestDto> findAllConfirmRequest();

    @Query("select new ru.practicum.model.request.dto.RequestDto(" +
            "r.id, " +
            "r.event.id, " +
            "r.requester.id, " +
            "r.created, " +
            "r.state) " +
            "from Request as r where r.state = 'CONFIRMED' and r.event.id in :ids")
    List<RequestDto> findConfirmRequestByEventsId(List<Long> ids);

    Optional<Request> findByRequesterIdAndId(long userId, long requestId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"event", "requester"})
    List<Request> findByEventIdAndIdIn(long eventId, List<Long> ids);
}
