package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.user.User;
import ru.practicum.model.user.dto.UserDto;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select new ru.practicum.model.user.dto.UserDto(us.id, us.email, us.name) " +
            "from User as us " +
            "where (us.id in :usersId or :usersId = null)")
    List<UserDto> findAllById(List<Long> usersId, Pageable pageable);
}
