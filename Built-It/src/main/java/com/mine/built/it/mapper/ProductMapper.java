package com.mine.built.it.mapper;

import com.mine.built.it.dto.ProductRequest;
import com.mine.built.it.dto.ProductResponse;
import com.mine.built.it.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductRequest request);

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "supplier.companyName", target = "supplierName") //response
    ProductResponse toResponse(Product product);
}
