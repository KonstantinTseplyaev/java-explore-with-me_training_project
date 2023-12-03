package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.ModelNotFoundException;
import ru.practicum.mapper.MapperUtil;
import ru.practicum.model.user.User;
import ru.practicum.model.user.dto.UserCreationDto;
import ru.practicum.model.user.dto.UserDto;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserCreationDto userCreationDto) {
        User newUser = userRepository.save(MapperUtil.convertToUser(userCreationDto));
        return MapperUtil.convertToUserDto(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<User> users;
        if (ids != null) users = userRepository.findByIdIn(ids, pageable);
        else users = userRepository.findAll(pageable).getContent();
        return MapperUtil.convertList(users, MapperUtil::convertToUserDto);
    }

    @Override
    public void deleteUserById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ModelNotFoundException("User with id=" + userId + " was not found");
        }
        userRepository.deleteById(userId);
    }
}
