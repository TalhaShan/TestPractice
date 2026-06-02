package com.mine.built.it.service;

import com.mine.built.it.ResourceNotFoundException;
import com.mine.built.it.dto.ProductRequest;
import com.mine.built.it.dto.ProductResponse;
import com.mine.built.it.entity.Category;
import com.mine.built.it.entity.Product;
import com.mine.built.it.entity.Supplier;
import com.mine.built.it.repository.CategoryRepository;
import com.mine.built.it.repository.ProductRepository;
import com.mine.built.it.repository.SupplierRepository;
import com.mine.built.it.service.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {

        if (productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("SKU already exists: " + request.getSku());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        Product product = new Product();

        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setAvailableQuantity(request.getAvailableQuantity());
        product.setCategory(category);
        product.setSupplier(supplier);

        // No @CachePut here — let getById() populate the cache on first read
        //looks - > @CachePut(value = "products", key = "#result.id")

        return map(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductResponse getById(Long id) {
        // Returns ProductResponse directly — cleaner cache entries, no Optional wrapping issues
        return productRepository.findDetailedProduct(id)
                .map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("products")
    public Page<ProductResponse> getAll(Pageable pageable) {

        return productRepository.findAll(pageable)
                .map(this::map);
    }

    @Override
    @CachePut(value = "products", key = "#id")
    public ProductResponse update(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setAvailableQuantity(request.getAvailableQuantity());

        // Return type matches what @Cacheable stores — no type mismatch
        return map(productRepository.save(product));
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    public void delete(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

        productRepository.delete(product);
    }

    private ProductResponse map(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .price(product.getPrice())
                .availableQuantity(product.getAvailableQuantity())
                .categoryName(product.getCategory().getName())
                .supplierName(product.getSupplier().getCompanyName())
                .build();
    }
    /*
    1. Bulk updates or batch operations

If many products are changed at once:

CSV import
bulk price update
nightly sync from external system
admin “update all products” feature

Example:

@CacheEvict(value = "products", allEntries = true)
public void importProducts(List<ProductDto> products) {
    repository.saveAll(products);
}
     */
}
