package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.model.compilation.dto.CompilationCreationDto;
import ru.practicum.model.compilation.dto.CompilationDto;
import ru.practicum.model.compilation.dto.CompilationForUpdateDto;
import ru.practicum.model.event.EventRequestParam;
import ru.practicum.model.event.EventState;
import ru.practicum.model.event.dto.EventDto;
import ru.practicum.model.event.dto.UpdatedEventDto;
import ru.practicum.model.user.dto.UserCreationDto;
import ru.practicum.model.user.dto.UserDto;
import ru.practicum.service.compilation.CompilationService;
import ru.practicum.service.category.CategoryService;
import ru.practicum.service.event.EventService;
import ru.practicum.service.user.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
public class AdminController {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;

    @GetMapping("/users")
    //1 запрос к бд
    public List<UserDto> getAllUsers(@RequestParam(required = false) Long[] ids,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {
        log.info("Get-запрос: получение информации обо всех пользователях");
        return userService.getAllUsers(ids, from, size);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    //1 запрос к бд
    public UserDto createNewUser(@RequestBody @Valid UserCreationDto userCreationDto) {
        log.info("Post-запрос: создание нового пользователя {}", userCreationDto);
        return userService.createUser(userCreationDto);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //2 запроса к бд (непонятно почему hibernate сначала достаёт пользователя по id. Это нигде не прописано)
    public void deleteUserById(@PathVariable long userId) {
        log.info("Delete-запрос: удаление пользователя с id {}", userId);
        userService.deleteUserById(userId);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    //1 запрос к бд
    public CategoryDto createNewCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Post-запрос: создание новой категории {}", categoryDto);
        return categoryService.createCategory(categoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //2 запроса к бд
    public void deleteCategoryById(@PathVariable long catId) {
        log.info("Delete-запрос: удаление категории с id {}", catId);
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    //2 запроса к бд
    public CategoryDto updateCategoryById(@PathVariable long catId,
                                          @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Patch-запрос: обновление категории с id {}. Новые данные - {}", catId, categoryDto);
        return categoryService.updateCategory(catId, categoryDto);
    }

    @GetMapping("/events")
    //1 запрос к бд
    public List<EventDto> getEvents(@RequestParam(defaultValue = "") Long[] users,
                                    @RequestParam(defaultValue = "") EventState[] states,
                                    @RequestParam(defaultValue = "") Long[] categories,
                                    @RequestParam(defaultValue = "1900-01-01 01:01:01") String rangeStart,
                                    @RequestParam(defaultValue = "2199-12-31 23:59:59") String rangeEnd,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {
        EventRequestParam param = EventRequestParam.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(LocalDateTime.parse(rangeStart, formatter))
                .rangeEnd(LocalDateTime.parse(rangeEnd, formatter))
                .from(from)
                .size(size)
                .build();
        log.info("Get-запрос: получение информации обо всех событиях, подходящих под переданные условия: {}", param);
        return eventService.getEventByParam(param);
    }

    @PatchMapping("/events/{eventId}")
    //4 (в худшем случае 5 запросов к бд)
    public EventDto adminUpdateEvent(@PathVariable long eventId,
                                     @RequestBody @Valid UpdatedEventDto eventDto) {
        log.info("Patch-запрос: обновление события с id {}. Новые данные - {}", eventId, eventDto);
        return eventService.adminUpdateEvent(eventId, eventDto);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    //1 (в худшем случае 2 запроса к бд)
    public CompilationDto createNewCompilation(@RequestBody @Valid CompilationCreationDto compilationDto) {
        log.info("Post-запрос: создание новой подборки {}", compilationDto);
        return compilationService.createCompilation(compilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //2 запроса к бд
    public void deleteCompilationById(@PathVariable long compId) {
        log.info("Delete-запрос: удаление подборки с id {}", compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}")
    //2 (в худшем случае 3 запроса к бд)
    public CompilationDto updateCompilation(@PathVariable long compId,
                                            @RequestBody @Valid CompilationForUpdateDto compilationDto) {
        log.info("Patch-запрос: обновление подборки с id {}. Новые данные - {}", compId, compilationDto);
        return compilationService.updateCompilation(compId, compilationDto);
    }
}
