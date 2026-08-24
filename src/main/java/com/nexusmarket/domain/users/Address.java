package com.nexusmarket.domain.users;

import java.util.Objects;

/**
 * Delivery location of a buyer (primary or additional).
 */
public class Address {

    private final String id;
    private String line;
    private String city;
    private String region;
    private String postalCode;
    private String country;

    public Address(String id, String line, String city, String region,
                   String postalCode, String country) {
        this.id = requireText(id, "id");
        this.line = requireText(line, "line");
        this.city = requireText(city, "city");
        this.region = region == null ? "" : region.trim();
        this.postalCode = postalCode == null ? "" : postalCode.trim();
        this.country = requireText(country, "country");
    }

    public String getId() {
        return id;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = requireText(line, "line");
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = requireText(city, "city");
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region == null ? "" : region.trim();
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode == null ? "" : postalCode.trim();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = requireText(country, "country");
    }

    private static String requireText(String value, String field) {
        Objects.requireNonNull(value, field + " is required");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
        return trimmed;
    }
}
