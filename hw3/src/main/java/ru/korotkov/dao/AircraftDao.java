package ru.korotkov.dao;

import lombok.AllArgsConstructor;
import org.postgresql.util.PGobject;
import ru.korotkov.db.SimpleJdbcTemplate;
import ru.korotkov.domain.Aircraft;

import java.sql.SQLException;

@AllArgsConstructor
public class AircraftDao {
    private final SimpleJdbcTemplate source;
    private final PGobject jsonObject = new PGobject();

    public final void saveAircraft(Aircraft aircraft) throws SQLException {
        source.preparedStatement("insert into aircrafts(aircraft_code, model, range) values (?, ?, ?)",
                insertAircraft -> {
                    insertAircraft.setString(1, aircraft.getAircraftCode());

                    jsonObject.setType("json");
                    jsonObject.setValue(aircraft.getModel());
                    insertAircraft.setObject(2, jsonObject);

                    insertAircraft.setInt(3, aircraft.getRange());
                    insertAircraft.execute();
                });
    }
}
