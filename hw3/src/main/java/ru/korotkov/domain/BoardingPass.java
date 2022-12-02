package ru.korotkov.domain;

import lombok.Data;

@Data
public class BoardingPass {
    private final String ticketNo;
    private final Integer flightId;
    private final Integer boardingNo;
    private final String seatNo;
}
