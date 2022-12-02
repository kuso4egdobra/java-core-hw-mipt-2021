package ru.korotkov.dao;

import lombok.AllArgsConstructor;
import ru.korotkov.db.SimpleJdbcTemplate;
import ru.korotkov.domain.Seat;

import java.sql.SQLException;

@AllArgsConstructor
public class SeatDao {
    private final SimpleJdbcTemplate source;

    public final void saveSeat(Seat seat) throws SQLException {
        source.preparedStatement("insert into seats(aircraft_code, seat_no, fare_conditions) values (?, ?, ?)",
                insertSeat -> {
                    insertSeat.setString(1, seat.getAircraftCode());
                    insertSeat.setString(2, seat.getSeatNo());
                    insertSeat.setString(3, seat.getFareConditions());

                    insertSeat.execute();
                });
    }
}
