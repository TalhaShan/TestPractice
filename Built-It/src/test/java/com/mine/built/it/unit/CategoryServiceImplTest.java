package com.mine.built.it.unit;


import com.mine.built.it.dto.ProductRequest;
import com.mine.built.it.dto.ProductResponse;
import com.mine.built.it.entity.Category;
import com.mine.built.it.entity.Product;
import com.mine.built.it.entity.Supplier;
import com.mine.built.it.repository.CategoryRepository;
import com.mine.built.it.repository.ProductRepository;
import com.mine.built.it.repository.SupplierRepository;
import com.mine.built.it.service.CategoryServiceImpl;
import com.mine.built.it.service.ProductServiceImpl;
import com.mine.built.it.service.SupplierServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;
    private Supplier supplier;
    private Product product;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("test");

         supplier = new Supplier();
        supplier.setId(1L);

        product = new Product();
        product.setId(1L);
        product.setName("Macbook");
        product.setSku("MAC-001");
        product.setPrice(BigDecimal.valueOf(2000));
        product.setAvailableQuantity(10);
        product.setCategory(category);
        product.setSupplier(supplier);

        productRequest = new ProductRequest();
        productRequest.setCategoryId(1L);
        productRequest.setSupplierId(1L);
        productRequest.setSku("A");
        productRequest.setName("sd");
        productRequest.setPrice(BigDecimal.valueOf(100));
        productRequest.setAvailableQuantity(10);
    }

    @Test
    void shouldThrowWhenDeletingCategoryWithProducts() {

        category.setId(1L);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.existsByCategoryId(1L))
                .thenReturn(true);

        assertThatThrownBy(() -> categoryService.delete(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("associated products");
    }

    @Test
    void shouldDeleteCategorySuccessfully() {


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
    void shouldCreateProductSuccessfully() {

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(productRepository.existsBySku(productRequest.getSku())).thenReturn(false);
        when(productRepository.save(Mockito.any(Product.class)))
                .thenReturn(product);

        ProductResponse productResponse = productService.create(productRequest);
        assertThat(productResponse).isNotNull();
        assertThat(productResponse.getCategoryName()).isEqualTo("test");
        verify(productRepository, times(1)).save(Mockito.any());


    }
}

