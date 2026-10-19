package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VariantDocument {

    private String id;
    private String sku;
    private String color;
    private String size;
    private String model;
}
