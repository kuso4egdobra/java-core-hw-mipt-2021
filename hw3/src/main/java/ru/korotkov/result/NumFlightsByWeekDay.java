package ru.korotkov.result;

import lombok.Data;

@Data
public class NumFlightsByWeekDay {
    private final Integer dayOfWeek;
    private final Integer countFlights;
}
