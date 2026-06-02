package com.mine.built.it.repository;

import com.mine.built.it.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    @Query("""
            SELECT p
            FROM Product p
            JOIN FETCH p.category
            JOIN FETCH p.supplier
            WHERE p.id = :id
            """)
    Optional<Product> findDetailedProduct(@Param("id") Long id);

//    @EntityGraph(attributePaths = {"category", "supplier"})
//    List<Product> findAllByCategoryId(Long categoryId);
//
//
//    @EntityGraph(attributePaths = {"category", "supplier"})
//    Page<Product> findAllProducts(Pageable pageable);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT p
    FROM Product p
    WHERE p.id = :id
""")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    @Modifying
    @Query("""
    UPDATE Product p
    SET p.availableQuantity = p.availableQuantity - :qty
    WHERE p.id = :id
    AND p.availableQuantity >= :qty
""")
    int decreaseStock(
            @Param("id") Long id,
            @Param("qty") Integer qty
    );

    boolean existsByCategoryId(Long categoryId);
}
