package com.mine.built.it.unit;


import com.mine.built.it.dto.ProductRequest;
import com.mine.built.it.dto.ProductResponse;
import com.mine.built.it.entity.Category;
import com.mine.built.it.entity.Product;
import com.mine.built.it.entity.Supplier;
import com.mine.built.it.repository.CategoryRepository;
import com.mine.built.it.repository.ProductRepository;
import com.mine.built.it.service.CategoryServiceImpl;
import com.mine.built.it.service.ProductServiceImpl;
import com.mine.built.it.service.interfaces.CategoryService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestYourSelf {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void shouldThrowExceptionWhenDeleteCategoryWith() {
        Category category = new Category();
        category.setId(1L);

        Product product = new Product();
        product.setId(1L);
        //when
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(category.getId())).thenReturn(true);

        //then
        assertThatThrownBy(()->categoryService.delete(1L)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("associated products");

    }

    @Test
    void shouldDeleteCategorySuccessfully() {

        Category category = new Category();
        category.setId(1L);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.existsByCategoryId(1L))
                .thenReturn(false);

        categoryService.delete(1L);

        verify(categoryRepository).delete(category);
        verify(categoryRepository, times(1)).delete(Mockito.any());

    }

    @Test
    void shouldThrowWhenSkuAlreadyExists2() {
        ProductRequest request = new ProductRequest();
        request.setName("Macbook");
        request.setSku("MAC-001");
        request.setPrice(BigDecimal.valueOf(2000));
        request.setAvailableQuantity(10);
        request.setCategoryId(1L);
        request.setSupplierId(1L);

        when(productRepository.existsBySku("MAC-001")).thenReturn(true);
        Assertions.assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SKU already exists");
    }

    @Test
    void shouldReturnProductSuccessfully() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Macbook");
        product.setSku("MAC-001");
        product.setPrice(BigDecimal.valueOf(2000));
        product.setAvailableQuantity(10);
        product.setCategory(new Category());
        product.setSupplier(new Supplier());

        when(productRepository.findDetailedProduct(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getById(1L);
        Assertions.assertThat(response.getId()).isEqualTo(1L);

    }
}
