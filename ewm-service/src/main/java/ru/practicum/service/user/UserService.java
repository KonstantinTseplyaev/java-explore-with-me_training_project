package ru.practicum.service.user;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.practicum.model.user.dto.UserCreationDto;
import ru.practicum.model.user.dto.UserDto;

import java.util.List;

public interface UserService extends UserDetailsService {

    void saveUser(UserCreationDto userCreationDto);

    List<UserDto> getAllUsers(List<Long> ids, int from, int size);

    void deleteUserById(long userId);
}
