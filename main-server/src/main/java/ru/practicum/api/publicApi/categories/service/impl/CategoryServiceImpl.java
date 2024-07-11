package ru.practicum.api.publicApi.categories.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.api.publicApi.categories.service.CategoryService;
import ru.practicum.dto.categories.CategoryDto;
import ru.practicum.exceptions.NotFoundEntity;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findAll(int from, int size) {
        PageRequest pageable = PageRequest.of(from, size);
        Page<Category> compilations = categoryRepository.findAll(pageable);
        if (compilations.getContent().isEmpty()) {
            return List.of();
        }
        return CategoryMapper.convertToListCategoryDto(compilations.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto findCategoryById(Long catId) {
        return CategoryMapper.convertToCategoryDto(categoryRepository
                .findById(catId)
                .orElseThrow(() -> new NotFoundEntity(String.format("Category whit id=%d", catId))));
    }
}
