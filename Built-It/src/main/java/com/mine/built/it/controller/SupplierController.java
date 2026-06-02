package com.mine.built.it.controller;

import com.mine.built.it.dto.SupplierRequest;
import com.mine.built.it.dto.SupplierResponse;
import com.mine.built.it.service.interfaces.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<SupplierResponse> create(@RequestBody SupplierRequest supplierRequest) {

        return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.create(supplierRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> getById(@PathVariable Long id) {

        return ResponseEntity.ok(supplierService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponse>> getAll() {

        return ResponseEntity.ok(supplierService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponse> update(@PathVariable Long id, @RequestBody SupplierRequest request) {

        return ResponseEntity.ok(supplierService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        supplierService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
