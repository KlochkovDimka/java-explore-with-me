package ru.practicum.api.adminApi.categories.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.api.adminApi.categories.service.CategoriesService;
import ru.practicum.dto.categories.CategoryDto;
import ru.practicum.dto.categories.NewCategoryDto;
import ru.practicum.exceptions.NotFoundEntity;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto saveCategory(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.convertToCategory(newCategoryDto);
        return CategoryMapper.convertToCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long id) {
        isCategory(id);
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(NewCategoryDto newCategoryDto, Long catId) {
        isCategory(catId);
        Category category = Category.builder()
                .id(catId)
                .name(newCategoryDto.getName())
                .build();
        return CategoryMapper.convertToCategoryDto(categoryRepository.save(category));
    }

    private void isCategory(Long catId) {
        categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundEntity("Category id=" + catId));
    }

}
