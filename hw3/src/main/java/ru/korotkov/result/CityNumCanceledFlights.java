package ru.korotkov.result;

import lombok.Data;

@Data
public class CityNumCanceledFlights {
    private final String city;
    private final Integer numCanceledFlights;
}
