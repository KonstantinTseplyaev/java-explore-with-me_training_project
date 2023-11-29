package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;
import ru.practicum.model.event.dto.EventDto;
import ru.practicum.model.event.dto.EventShortDto;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.service.event.EventServiceImpl.EVENT_DTO_REQUEST;
import static ru.practicum.service.event.EventServiceImpl.EVENT_SHORT_DTO_REQUEST;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(EVENT_DTO_REQUEST + "where e.initiator.id = :userId")
    List<EventDto> findByInitiatorId(long userId, Pageable pageable);

    @Query(EVENT_DTO_REQUEST + "where e.id = :eventId and e.initiator.id = :userId")
    Optional<EventDto> findEventByIdAndInitiatorId(long eventId, long userId);

    boolean existsByIdAndInitiatorId(long eventId, long userId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"initiator", "category", "location"})
    Optional<Event> findByIdAndInitiatorId(long eventId, long userId);

    Optional<Event> findByIdAndStateAndInitiatorIdIsNot(long eventId, EventState state, long userId);

    @Query(EVENT_DTO_REQUEST +
            "where (e.initiator.id in :users or :users is null) " +
            "and (e.state in :states or :states is null) " +
            "and (e.category.id in :categories or :categories is null) " +
            "and (e.eventDate between :start and :end)")
    List<EventDto> findEventsByParam(List<Long> users, List<EventState> states, List<Long> categories,
                                     LocalDateTime start, LocalDateTime end, Pageable pageable);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"initiator", "category", "location"})
    List<Event> findByIdIn(List<Long> events);

    @Query(EVENT_SHORT_DTO_REQUEST +
            "where (e.state = 'PUBLISHED') " +
            "and ((lower(e.annotation) like %:text% or lower(e.description) like %:text%) or :text = null) " +
            "and (e.category.id in :categories or :categories = null) " +
            "and (e.paid = :paid or :paid = null) " +
            "and (e.eventDate between :rangeStart and :rangeEnd) " +
            "and ((e.participantLimit - (select count(r.id) from Request as r where (r.event.id = e.id) " +
            "and (r.state = 'CONFIRMED'))) > 0 or :onlyAvailable = false) " +
            "order by :sort")
    List<EventShortDto> findPublicEventsByParam(String text, List<Long> categories, Boolean paid,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable,
                                                String sort, Pageable page);

    @Query(EVENT_SHORT_DTO_REQUEST + "where e.id in :ids")
    List<EventShortDto> findEventShortByIdIn(List<Long> ids);

    @Query(EVENT_DTO_REQUEST + "where e.id = :id and e.state = 'PUBLISHED'")
    Optional<EventDto> getPublicEventById(long id);

    @Modifying
    @Query(value = "update events set views = :currentViews where id = :id", nativeQuery = true)
    void updateEventViews(long id, long currentViews);
}
