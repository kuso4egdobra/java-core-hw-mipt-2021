package ru.korotkov.dao;

import lombok.AllArgsConstructor;
import ru.korotkov.db.SimpleJdbcTemplate;
import ru.korotkov.domain.BoardingPass;

import java.sql.SQLException;

@AllArgsConstructor
public class BoardingPassDao {
    private final SimpleJdbcTemplate source;

    public final void saveBoardingPass(BoardingPass boardingPass) throws SQLException {
        source.preparedStatement(
                "insert into boarding_passes(ticket_no, flight_id, boarding_no, seat_no) values (?, ?, ?, ?)",
                insertBrdPass -> {
                    insertBrdPass.setString(1, boardingPass.getTicketNo());
                    insertBrdPass.setInt(2, boardingPass.getFlightId());
                    insertBrdPass.setInt(3, boardingPass.getBoardingNo());
                    insertBrdPass.setString(4, boardingPass.getSeatNo());

                    insertBrdPass.execute();
                });
    }
}
