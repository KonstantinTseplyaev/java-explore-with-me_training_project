package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.AuthException;
import ru.practicum.exceptions.ModelNotFoundException;
import ru.practicum.model.user.Role;
import ru.practicum.repository.RoleRepository;
import ru.practicum.util.MapperUtil;
import ru.practicum.model.user.User;
import ru.practicum.model.user.dto.UserCreationDto;
import ru.practicum.model.user.dto.UserDto;
import ru.practicum.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;

    public void saveUser(UserCreationDto userDto) {
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new AuthException("Invalid password");
        }

        String role = "ROLE_USER";

        if (userDto.getName().equals("Konstantin")) {  //чтобы создать единственного админа (логика может быть другой)
            role = "ROLE_ADMIN";
        }

        User newUser = User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .password(encoder.encode(userDto.getPassword()))
                .build();
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(role));
        newUser.setRoles(roles);
        userRepository.save(newUser);
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

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User with email=" + email + " not found!"));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream().map(role ->
                        new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }
}
