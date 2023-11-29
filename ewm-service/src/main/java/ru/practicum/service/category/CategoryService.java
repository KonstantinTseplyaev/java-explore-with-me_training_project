package ru.practicum.service.category;

import ru.practicum.model.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);

    void deleteCategory(long catId);

    CategoryDto updateCategory(long catId, CategoryDto categoryDto);

    CategoryDto getCategoryById(long catId);

    List<CategoryDto> getCategories(int from, int size);
}
