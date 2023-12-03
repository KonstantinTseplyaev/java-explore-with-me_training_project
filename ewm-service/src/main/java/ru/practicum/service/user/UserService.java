package ru.practicum.service.user;

import ru.practicum.model.user.dto.UserCreationDto;
import ru.practicum.model.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserCreationDto userCreationDto);

    List<UserDto> getAllUsers(List<Long> ids, int from, int size);

    void deleteUserById(long userId);
}
