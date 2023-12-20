package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.ModelNotFoundException;
import ru.practicum.util.MapperUtil;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category cat = categoryRepository.save(MapperUtil.convertToCategory(categoryDto));
        return MapperUtil.convertToCategoryDto(cat);
    }

    @Override
    public void deleteCategory(long catId) {
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryDto.getId()).orElseThrow(() ->
                new ModelNotFoundException("Category with id=" + categoryDto.getId() + " was not found"));
        category.setName(categoryDto.getName());
        return MapperUtil.convertToCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(long catId) {
        return MapperUtil.convertToCategoryDto(categoryRepository.findById(catId)
                .orElseThrow(() -> new ModelNotFoundException("Category with id=" + catId + " was not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        List<Category> categories = categoryRepository.findAll(page).getContent();
        return MapperUtil.convertList(categories, MapperUtil::convertToCategoryDto);
    }
}
