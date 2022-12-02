package ru.korotkov.result;

import lombok.Data;

@Data
public class CancelledCovidFlightsWithLossMoney {
    private final Double sum;
    private final Integer day;
}
