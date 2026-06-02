package com.mine.built.it.service.interfaces;

import com.mine.built.it.dto.SupplierRequest;
import com.mine.built.it.dto.SupplierResponse;
import com.mine.built.it.entity.Supplier;

import java.util.List;

public interface SupplierService {

    SupplierResponse create(SupplierRequest supplierRequest);

    SupplierResponse getById(Long id);

    List<SupplierResponse> getAll();

    SupplierResponse update(Long id, SupplierRequest request);

    void delete(Long id);
}
