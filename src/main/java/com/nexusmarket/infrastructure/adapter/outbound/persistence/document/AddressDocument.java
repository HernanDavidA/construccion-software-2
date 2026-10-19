package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDocument {

    private String line;
    private String city;
    private String region;
    private String country;
}
