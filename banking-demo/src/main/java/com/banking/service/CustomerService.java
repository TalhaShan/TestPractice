package com.banking.service;

import com.banking.dto.BankingDtos.*;
import com.banking.entity.Customer;
import com.banking.exception.BankingException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new BankingException("Email already registered: " + request.getEmail());
        }

        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .nationalId(request.getNationalId())
                .build();

        customer = customerRepository.save(customer);
        log.info("Customer created. CustomerId={}, Email={}", customer.getId(), customer.getEmail());
        return CustomerResponse.from(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .map(CustomerResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerResponse::from)
                .toList();
    }

    @Transactional
    public CustomerResponse updateCustomer(Long customerId, CreateCustomerRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));

        // Check if new email conflicts with another customer
        customerRepository.findByEmail(request.getEmail())
                .filter(existing -> !existing.getId().equals(customerId))
                .ifPresent(c -> { throw new BankingException("Email already in use"); });

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        return CustomerResponse.from(customerRepository.save(customer));
    }
}
