package com.mine.built.it.dto;

import com.mine.built.it.entity.Product;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Data
public class SupplierRequest {
    private String companyName;

    private String contactEmail;

    @OneToMany(mappedBy = "supplier")
    private List<Product> products;
}
