package com.mine.built.it.unit.integration;

import com.mine.built.it.entity.Category;
import com.mine.built.it.entity.Product;
import com.mine.built.it.entity.Supplier;
import com.mine.built.it.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void shouldDecreaseStockAtomically() {

        Category category = new Category();
        category.setName("Electronics");

        Supplier supplier = new Supplier();
        supplier.setCompanyName("Apple");

        testEntityManager.persist(category);
        testEntityManager.persist(supplier);

        Product product = new Product();
        product.setName("Macbook");
        product.setSku("SKU-1");
        product.setPrice(BigDecimal.TEN);
        product.setAvailableQuantity(10);
        product.setCategory(category);
        product.setSupplier(supplier);

        testEntityManager.persist(product);
        testEntityManager.flush();

        int updated = productRepository.decreaseStock(
                product.getId(),
                5
        );

        testEntityManager.clear();

        Product updatedProduct =
                productRepository.findById(product.getId()).orElseThrow();

        assertThat(updated).isEqualTo(1);
        assertThat(updatedProduct.getAvailableQuantity())
                .isEqualTo(5);
    }
}
