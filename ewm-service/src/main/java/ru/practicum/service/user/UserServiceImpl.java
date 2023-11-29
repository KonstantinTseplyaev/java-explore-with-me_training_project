package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mapper.MapperUtil;
import ru.practicum.model.user.User;
import ru.practicum.model.user.dto.UserCreationDto;
import ru.practicum.model.user.dto.UserDto;
import ru.practicum.repository.UserRepository;

import java.util.Arrays;
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
    public List<UserDto> getAllUsers(Long[] ids, int from, int size) {
        List<Long> usersId = null;
        if (ids != null && ids.length != 0) usersId = Arrays.asList(ids);
        Pageable pageable = PageRequest.of(from / size, size);
        return userRepository.findAllById(usersId, pageable);
    }

    @Override
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }
}
