package com.mine.built.it.service.interfaces;

import com.mine.built.it.dto.CategoryRequest;
import com.mine.built.it.dto.CategoryResponse;
import com.mine.built.it.entity.Category;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CategoryRequest category);

    CategoryResponse getById(Long id);

    List<CategoryResponse> getAll();

    CategoryResponse update(Long id, CategoryRequest request);

    void delete(Long id);
}
