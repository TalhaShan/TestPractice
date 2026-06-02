package com.mine.built.it.service.interfaces;

import com.mine.built.it.dto.ProductRequest;
import com.mine.built.it.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    ProductResponse getById(Long id);

    Page<ProductResponse> getAll(Pageable pageable);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);
}
