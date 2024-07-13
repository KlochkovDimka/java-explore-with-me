package ru.practicum.api.publicApi.categories.service;

import ru.practicum.dto.categories.CategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> findAll(int from, int size);

    CategoryDto findCategoryById(Long catId);
}
