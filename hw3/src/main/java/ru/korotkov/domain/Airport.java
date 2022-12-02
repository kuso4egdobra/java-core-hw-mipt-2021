package ru.korotkov.domain;

import lombok.Data;

@Data
public class Airport {
    private final String airportCode;
    private final String airportName;
    private final String city;
    private final String coordinates;
    private final String timezone;
}
