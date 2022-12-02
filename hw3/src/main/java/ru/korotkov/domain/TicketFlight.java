package ru.korotkov.domain;

import lombok.Data;

@Data
public class TicketFlight {
    private final String ticketNo;
    private final Integer flightId;
    private final String fareConditions;
    private final Double amount;
}
