package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"initiator", "category", "location"})
    List<Event> findByInitiatorId(long userId, Pageable pageable);

    Optional<Event> findEventByIdAndInitiatorId(long eventId, long userId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"initiator", "category", "location"})
    Optional<Event> findByIdAndInitiatorId(long eventId, long userId);

    Optional<Event> findByIdAndStateAndInitiatorIdIsNot(long eventId, EventState state, long userId);

    @Query("from Event as e " +
            "where (e.initiator.id in :users or :users is null) " +
            "and (e.state in :states or :states is null) " +
            "and (e.category.id in :categories or :categories is null) " +
            "and (e.eventDate between :start and :end)")
    List<Event> findEventsByParam(List<Long> users,
                                  List<EventState> states,
                                  List<Long> categories,
                                  LocalDateTime start,
                                  LocalDateTime end, Pageable pageable);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"initiator", "category", "location"})
    List<Event> findByIdIn(List<Long> events);

    @Query("from Event as e " +
            "where (e.state = 'PUBLISHED') " +
            "and ((lower(e.annotation) like %:text% or lower(e.description) like %:text%) or :text is null) " +
            "and (e.category.id in :categories or :categories is null) " +
            "and (e.paid = :paid or :paid is null) " +
            "and (e.eventDate between :rangeStart and :rangeEnd) " +
            "and ((e.participantLimit - (select count(r.id) from Request as r where (r.event.id = e.id) " +
            "and (r.state = 'CONFIRMED'))) > 0 or :onlyAvailable = false) " +
            "order by :sort")
    List<Event> findPublicEventsByParam(String text, List<Long> categories, Boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable,
                                        String sort, Pageable page);

    Optional<Event> findEventByIdAndState(long id, EventState state);

    @Modifying
    @Query(value = "update events set views = :currentViews where id = :id", nativeQuery = true)
    void updateEventViews(long id, long currentViews);
}
