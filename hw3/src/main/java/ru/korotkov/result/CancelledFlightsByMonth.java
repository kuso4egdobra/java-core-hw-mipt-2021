package ru.korotkov.result;

import lombok.Data;

@Data
public class CancelledFlightsByMonth {
    private final Integer month;
    private final Integer numCancels;
}
