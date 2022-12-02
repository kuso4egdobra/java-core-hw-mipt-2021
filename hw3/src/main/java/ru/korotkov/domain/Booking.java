package ru.korotkov.domain;

import lombok.Data;

@Data
public class Booking {
    private final String bookRef;
    private final String bookDate;
    private final Double totalAmount;
}
