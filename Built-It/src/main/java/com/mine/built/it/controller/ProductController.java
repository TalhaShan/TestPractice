package com.mine.built.it.controller;

import com.mine.built.it.dto.ProductRequest;
import com.mine.built.it.dto.ProductResponse;
import com.mine.built.it.service.interfaces.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public ProductResponse addProduct(@Valid @RequestBody ProductRequest request) {

        return productService.create(request);
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ProductResponse> getAll(Pageable pageable) {
        return productService.getAll(pageable);
    }

    @GetMapping("/getAll")
    public Page<ProductResponse> getAll2(
            @PageableDefault(size = 20, sort = "name")
            Pageable pageable
    ) {
        return productService.getAll(pageable);
    }

    //GET http://localhost:8080/api/v1/products/getAll?page=0&size=10&sort=name,asc

    @GetMapping("{id}" )
    public  ProductResponse getProductById(@PathVariable  Long id){
        return productService.getById(id);
    }

}
