package ru.korotkov.dao;

import lombok.AllArgsConstructor;
import org.postgresql.util.PGobject;
import ru.korotkov.db.SimpleJdbcTemplate;
import ru.korotkov.domain.Booking;

import java.sql.SQLException;

@AllArgsConstructor
public class BookingDao {
    private final SimpleJdbcTemplate source;
    private final PGobject pgObject = new PGobject();

    public final void saveBooking(Booking booking) throws SQLException {
        source.preparedStatement("insert into bookings(book_ref, book_date, total_amount) values (?, ?, ?)",
                insertBooking -> {
                    insertBooking.setString(1, booking.getBookRef());

                    pgObject.setType("timestamp");
                    pgObject.setValue(booking.getBookDate());
                    insertBooking.setObject(2, pgObject);

                    insertBooking.setDouble(3, booking.getTotalAmount());

                    insertBooking.execute();
                });
    }
}
