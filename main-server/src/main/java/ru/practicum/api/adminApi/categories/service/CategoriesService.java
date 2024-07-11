package ru.practicum.api.adminApi.categories.service;

import ru.practicum.dto.categories.CategoryDto;
import ru.practicum.dto.categories.NewCategoryDto;

public interface CategoriesService {

    CategoryDto saveCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(Long id);

    CategoryDto updateCategory(NewCategoryDto newCategoryDto, Long catId);
}
