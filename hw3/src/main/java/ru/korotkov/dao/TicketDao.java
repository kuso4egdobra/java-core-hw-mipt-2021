package ru.korotkov.dao;

import lombok.AllArgsConstructor;
import org.postgresql.util.PGobject;
import ru.korotkov.db.SimpleJdbcTemplate;
import ru.korotkov.domain.Ticket;

import java.sql.SQLException;

@AllArgsConstructor
public class TicketDao {
    private final SimpleJdbcTemplate source;
    private final PGobject pgObject = new PGobject();

    public final void saveTicket(Ticket ticket) throws SQLException {
        source.preparedStatement(
                "insert into tickets("
                        + "ticket_no, book_ref, passenger_id, passenger_name, contact_data"
                        + ") values (?, ?, ?, ?, ?)",
                insertTicket -> {
                    insertTicket.setString(1, ticket.getTicketNo());
                    insertTicket.setString(2, ticket.getBookRef());
                    insertTicket.setString(3, ticket.getPassengerId());
                    insertTicket.setString(4, ticket.getPassengerName());

                    pgObject.setType("jsonb");
                    pgObject.setValue(ticket.getContactData());
                    insertTicket.setObject(5, pgObject);

                    insertTicket.execute();
        });
    }
}
