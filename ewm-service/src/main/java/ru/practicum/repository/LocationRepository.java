package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.location.Location;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByNameIsNotNull(Pageable page);

    Optional<Location> findByIdAndNameIsNotNull(long locId);

    boolean existsByLatAndLonAndRadius(double lat, double lon, double radius);
}
