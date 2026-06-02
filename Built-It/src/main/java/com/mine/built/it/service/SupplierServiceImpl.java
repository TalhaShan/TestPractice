package com.mine.built.it.service;

import com.mine.built.it.dto.SupplierRequest;
import com.mine.built.it.dto.SupplierResponse;
import com.mine.built.it.entity.Supplier;
import com.mine.built.it.repository.ProductRepository;
import com.mine.built.it.repository.SupplierRepository;
import com.mine.built.it.service.interfaces.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    @Override
    @CachePut(value = "suppliers", key = "#result.id")
    public SupplierResponse create(SupplierRequest request) {
            Supplier supplier = new Supplier();
            supplier.setContactEmail(request.getContactEmail());
            supplier.setCompanyName(request.getCompanyName());
        return map(supplierRepository.save(supplier));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "suppliers", key = "#id")
    public SupplierResponse getById(Long id) {

        return supplierRepository.findById(id)
                .map(this::map)
                .orElseThrow(() ->
                        new RuntimeException("Supplier not found"));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "suppliers")
    public List<SupplierResponse> getAll() {

        return supplierRepository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @CachePut(value = "suppliers", key = "#id")
    public SupplierResponse update(Long id, SupplierRequest request) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        supplier.setCompanyName(request.getCompanyName());
        supplier.setContactEmail(request.getContactEmail());

        return map(supplierRepository.save(supplier));
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "suppliers", key = "#id")
            }
    )
    public void delete(Long id) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        boolean hasProducts = productRepository.findAll()
                .stream()
                .anyMatch(product ->
                        product.getSupplier().getId().equals(id)
                );

        if (hasProducts) {
            throw new RuntimeException(
                    "Cannot delete supplier with associated products"
            );
        }

        supplierRepository.delete(supplier);
    }

    private SupplierResponse map(Supplier supplier) {

        return SupplierResponse.builder()
                .id(supplier.getId())
                .companyName(supplier.getCompanyName())
                .contactEmail(supplier.getContactEmail())
                .build();
    }
}
