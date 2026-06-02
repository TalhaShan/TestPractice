package com.mine.built.it.service;

import com.mine.built.it.ResourceNotFoundException;
import com.mine.built.it.dto.CategoryRequest;
import com.mine.built.it.dto.CategoryResponse;
import com.mine.built.it.entity.Category;
import com.mine.built.it.repository.CategoryRepository;
import com.mine.built.it.repository.ProductRepository;
import com.mine.built.it.service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @CachePut(value = "categories", key = "#result.id")
    public CategoryResponse create(CategoryRequest request) {

        Category category = new Category();

        category.setName(request.getName());

        return map(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#id")
    public CategoryResponse getById(Long id) {

        return categoryRepository.findById(id)
                .map(this::map)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found: " + id
                        ));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories")
    public List<CategoryResponse> getAll() {

        return categoryRepository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @CachePut(value = "categories", key = "#id")
    public CategoryResponse update(Long id, CategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found: " + id
                        ));

        category.setName(request.getName());

        return map(categoryRepository.save(category));
    }

    @Override
    @CacheEvict(value = "categories", key = "#id")
    public void delete(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found: " + id
                        ));

        boolean hasProducts =
                productRepository.existsByCategoryId(id);

        if (hasProducts) {
            throw new IllegalStateException(
                    "Cannot delete category with associated products"
            );
        }

        categoryRepository.delete(category);
    }

    private CategoryResponse map(Category category) {

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}

