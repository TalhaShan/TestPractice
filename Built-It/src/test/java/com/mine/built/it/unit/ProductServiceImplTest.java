package com.mine.built.it.unit;



import com.mine.built.it.ResourceNotFoundException;
import com.mine.built.it.dto.ProductRequest;
import com.mine.built.it.dto.ProductResponse;
import com.mine.built.it.entity.Category;
import com.mine.built.it.entity.Product;
import com.mine.built.it.entity.Supplier;
import com.mine.built.it.repository.CategoryRepository;
import com.mine.built.it.repository.ProductRepository;
import com.mine.built.it.repository.SupplierRepository;
import com.mine.built.it.service.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequest request;
    private Category category;
    private Supplier supplier;
    private Product product;

    @BeforeEach
    void setup() {

        request = new ProductRequest();
        request.setName("Macbook");
        request.setSku("MAC-001");
        request.setPrice(BigDecimal.valueOf(2000));
        request.setAvailableQuantity(10);
        request.setCategoryId(1L);
        request.setSupplierId(1L);

        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setCompanyName("Apple");

        product = new Product();
        product.setId(1L);
        product.setName("Macbook");
        product.setSku("MAC-001");
        product.setPrice(BigDecimal.valueOf(2000));
        product.setAvailableQuantity(10);
        product.setCategory(category);
        product.setSupplier(supplier);
    }

    @Test
    void shouldCreateProductSuccessfully() {

        when(productRepository.existsBySku(request.getSku()))
                .thenReturn(false);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(supplierRepository.findById(1L))
                .thenReturn(Optional.of(supplier));

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        ProductResponse response = productService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Macbook");
        assertThat(response.getSku()).isEqualTo("MAC-001");

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldThrowWhenSkuAlreadyExists() {

        when(productRepository.existsBySku("MAC-001"))
                .thenReturn(true);

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SKU already exists");
    }

    @Test
    void shouldThrowWhenCategoryNotFound() {

        when(productRepository.existsBySku(anyString()))
                .thenReturn(false);

        when(categoryRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldReturnProductById() {

        when(productRepository.findDetailedProduct(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response = productService.getById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCategoryName())
                .isEqualTo("Electronics");
    }

    @Test
    void shouldDeleteProduct() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.delete(1L);

        verify(productRepository).delete(product);
    }
}

