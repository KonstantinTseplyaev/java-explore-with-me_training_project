package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.service.category.CategoryService;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createNewCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Post-запрос: создание новой категории {}", categoryDto);
        return categoryService.createCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable long catId) {
        log.info("Delete-запрос: удаление категории с id {}", catId);
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategoryById(@PathVariable long catId,
                                          @RequestBody @Valid CategoryDto categoryDto) {
        categoryDto.setId(catId);
        log.info("Patch-запрос: обновление категории с id {}. Новые данные - {}", catId, categoryDto);
        return categoryService.updateCategory(categoryDto);
    }
}
