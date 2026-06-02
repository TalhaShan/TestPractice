package com.mine.built.it.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupplierResponse {

    private Long id;
    private String companyName;
    private String contactEmail;
}
