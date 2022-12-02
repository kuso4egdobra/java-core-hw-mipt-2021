package ru.korotkov.result;

import lombok.Data;

@Data
public class CityShortestRoute {
    private final String departureCity;
    private final String arrivalCity;
    private final String avgTime;
}
