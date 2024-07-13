package ru.practicum.api.adminApi.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.adminApi.categories.service.CategoriesService;
import ru.practicum.dto.categories.CategoryDto;
import ru.practicum.dto.categories.NewCategoryDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@Slf4j
public class ControllerAdminCategories {

    private final CategoriesService categoriesService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postNewCategories(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        return categoriesService.saveCategory(newCategoryDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoriesById(@PathVariable long id) {
        categoriesService.deleteCategoryById(id);
    }

    @PatchMapping("{id}")
    public CategoryDto patchCategoriesById(@RequestBody @Valid NewCategoryDto newCategoryDto,
                                           @PathVariable Long id) {
        return categoriesService.updateCategory(newCategoryDto, id);


    }
}
