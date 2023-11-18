package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Stat;
import ru.practicum.dto.StatDto;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface StatsRepository extends JpaRepository<Stat, Long> {

    @Query("select new ru.practicum.dto.StatDto(st.app, st.uri, count(st.uri) as hits) " +
            "from Stat as st " +
            "where st.visitTime between ?1 and ?2 " +
            "group by st.uri, st.app, order by hits desc")
    List<StatDto> findAllStatistic(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.StatDto(st.app, st.uri, count(distinct st.ip) as hits) " +
            "from Stat as st " +
            "where st.visitTime between ?1 and ?2 " +
            "group by st.uri, st.app order by hits desc")
    List<StatDto> findAllUniqueStatistic(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.StatDto(st.app, st.uri, count(st.uri) as hits) " +
            "from Stat as st " +
            "where st.uri in ?1 and st.visitTime between ?2 and ?3 " +
            "group by st.uri, st.app order by hits desc")
    List<StatDto> findStatisticByUris(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.StatDto(st.app, st.uri, count(distinct st.ip) as hits) " +
            "from Stat as st " +
            "where st.uri in ?1 and st.visitTime between ?2 and ?3 " +
            "group by st.uri, st.app order by hits desc")
    List<StatDto> findUniqueStatisticByUris(List<String> uris, LocalDateTime start, LocalDateTime end);
}
