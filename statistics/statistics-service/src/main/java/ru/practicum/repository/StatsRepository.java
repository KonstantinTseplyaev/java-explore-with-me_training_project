package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Stat;
import stat.dto.StatDto;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface StatsRepository extends JpaRepository<Stat, Long> {

    @Query("select new stat.dto.StatDto(st.app, st.uri, count(st.uri) as hits) " +
            "from Stat as st " +
            "where (st.uri in :uris or :uris is null) and st.visitTime between :start and :end " +
            "group by st.uri, st.app order by hits desc")
    List<StatDto> findStatistic(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("select new stat.dto.StatDto(st.app, st.uri, count(distinct st.ip) as hits) " +
            "from Stat as st " +
            "where (st.uri in :uris or :uris is null) and st.visitTime between :start and :end " +
            "group by st.uri, st.app order by hits desc")
    List<StatDto> findUniqueStatistic(List<String> uris, LocalDateTime start, LocalDateTime end);
}
