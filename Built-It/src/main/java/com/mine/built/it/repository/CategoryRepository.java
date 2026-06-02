package com.mine.built.it.repository;

import com.mine.built.it.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("""
    SELECT DISTINCT c
    FROM Category c
    LEFT JOIN FETCH c.products
""")
    List<Category> findAllWithProducts();



    @Query("""
    SELECT DISTINCT c
    FROM Category c
    LEFT JOIN FETCH c.products
    WHERE c.id = :id
""")
    List<Category> findByIdWithProducts(Long id);

}


