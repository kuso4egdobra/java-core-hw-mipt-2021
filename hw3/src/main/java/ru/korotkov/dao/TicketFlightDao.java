package ru.korotkov.dao;

import lombok.AllArgsConstructor;
import ru.korotkov.db.SimpleJdbcTemplate;
import ru.korotkov.domain.TicketFlight;

import java.sql.SQLException;

@AllArgsConstructor
public class TicketFlightDao {
    private final SimpleJdbcTemplate source;

    public final void saveTicketFlight(TicketFlight ticketFlight) throws SQLException {
        source.preparedStatement(
                "insert into ticket_flights(ticket_no, flight_id, fare_conditions, amount) values (?, ?, ?, ?)",
                insertTicketFlight -> {
                    insertTicketFlight.setString(1, ticketFlight.getTicketNo());
                    insertTicketFlight.setInt(2, ticketFlight.getFlightId());
                    insertTicketFlight.setString(3, ticketFlight.getFareConditions());
                    insertTicketFlight.setDouble(4, ticketFlight.getAmount());

                    insertTicketFlight.execute();
                });
    }
}
