package ru.korotkov.dao;

import lombok.AllArgsConstructor;
import org.postgresql.util.PGobject;
import ru.korotkov.db.SimpleJdbcTemplate;
import ru.korotkov.domain.Airport;
import ru.korotkov.result.CityNumCanceledFlights;
import ru.korotkov.result.CityShortestRoute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class AirportDao {
    private final SimpleJdbcTemplate source;
    private final PGobject pgObject = new PGobject();

    public final void saveAirport(Airport airport) throws SQLException {
        source.preparedStatement("insert into airports(airport_code, airport_name, city, coordinates, timezone) "
                        + "values (?, ?, ?, ?, ?)",
                insertAirport -> {
                    insertAirport.setString(1, airport.getAirportCode());

                    pgObject.setType("json");
                    pgObject.setValue(airport.getAirportName());
                    insertAirport.setObject(2, pgObject);

                    pgObject.setType("json");
                    pgObject.setValue(airport.getCity());
                    insertAirport.setObject(3, pgObject);

                    pgObject.setType("point");
                    pgObject.setValue(airport.getCoordinates());
                    insertAirport.setObject(4, pgObject);
                    insertAirport.setString(5, airport.getTimezone());

                    insertAirport.execute();
                });
    }

    public final Set<Airport> getAirports() throws SQLException {
        return source.statement(stmt -> {
            ResultSet resultSet = stmt.executeQuery("select * from airports");
            Set<Airport> result = new HashSet<>();
            while (resultSet.next()) {
                result.add(createAirport(resultSet));
            }
            return result;
        });
    }

    public final ArrayList<CityShortestRoute> getCitiesWithShortestRoute() throws SQLException {
        return source.statement(stmt -> {
            ResultSet resultSet = stmt.executeQuery(
                    "select t.airport_depart, t.airport_arrival, min\n"
                            + "from (\n"
                            + "         select airport_depart.city             as airport_depart,\n"
                            + "                airport_arrival.city            as airport_arrival,\n"
                            + "                avg(actual_arrival - actual_departure) as avgTime\n"
                            + "         from flights\n"
                            + "                  join airports airport_depart "
                            + "on airport_depart.airport_code = flights.departure_airport\n"
                            + "                  join airports airport_arrival "
                            + "on airport_arrival.airport_code = flights.arrival_airport\n"
                            + "         where status = 'Arrived'\n"
                            + "         group by airport_depart.city,\n"
                            + "                  airport_arrival.city) t\n"
                            + "         join (select min(avgTime) as min, airport_depart\n"
                            + "               from (select airport_depart.city                    as airport_depart,\n"
                            + "                            airport_arrival.city                   as airport_arrival,\n"
                            + "                            avg(actual_arrival - actual_departure) as avgTime\n"
                            + "                     from flights\n"
                            + "                              join airports airport_depart "
                            + "on airport_depart.airport_code = flights.departure_airport\n"
                            + "                              join airports airport_arrival "
                            + "on airport_arrival.airport_code = flights.arrival_airport\n"
                            + "                     where status = 'Arrived'\n"
                            + "                     group by airport_depart.city, airport_arrival.city) t\n"
                            + "               group by t.airport_depart) f on f.airport_depart = t.airport_depart\n"
                            + "where avgTime = f.min\n"
                            + "order by avgTime;"
            );
            ArrayList<CityShortestRoute> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(
                        new CityShortestRoute(
                                resultSet.getString("airport_depart"),
                                resultSet.getString("airport_arrival"),
                                resultSet.getString("min")
                        )
                );
            }
            return result;
        });
    }

    public final ArrayList<CityNumCanceledFlights> getCitiesNumCanceledFLights() throws SQLException {
        return source.statement(stmt -> {
            ResultSet resultSet = stmt.executeQuery(
                    "select city->>'ru' as city, count(city) from flights\n"
                            + "inner join airports on flights.departure_airport = airports.airport_code\n"
                            + "where status = 'Cancelled'\n"
                            + "group by city;"
            );
            ArrayList<CityNumCanceledFlights> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(
                        new CityNumCanceledFlights(
                                resultSet.getString("city"),
                                Integer.valueOf(resultSet.getString("count"))
                        )
                );
            }
            return result;
        });
    }

    private Airport createAirport(ResultSet resultSet) throws SQLException {
        return new Airport(
                resultSet.getString("airport_code"),
                resultSet.getString("airport_name"),
                resultSet.getString("city"),
                resultSet.getString("coordinates"),
                resultSet.getString("timezone")
        );
    }
}
