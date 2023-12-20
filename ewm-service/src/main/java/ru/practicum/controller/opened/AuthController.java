package ru.practicum.controller.opened;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.model.user.dto.JwtRequest;
import ru.practicum.model.user.dto.UserCreationDto;
import ru.practicum.service.user.AuthService;
import ru.practicum.service.user.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        log.info("попытка входа пользователя с данными: {}", authRequest);
        return authService.createNewToken(authRequest);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody UserCreationDto userDto) {
        log.info("регистрация пользователя с данными {}", userDto);
        userService.saveUser(userDto);
        return authService.createNewToken(new JwtRequest(userDto.getEmail(), userDto.getPassword()));
    }
}

