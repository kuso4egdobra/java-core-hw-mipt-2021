package ru.korotkov.dao;

import lombok.AllArgsConstructor;
import org.postgresql.util.PGobject;
import ru.korotkov.db.SimpleJdbcTemplate;
import ru.korotkov.domain.Flight;
import ru.korotkov.result.CancelledCovidFlightsWithLossMoney;
import ru.korotkov.result.CancelledFlightsByMonth;
import ru.korotkov.result.NumFlightsByWeekDay;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@AllArgsConstructor
public class FlightDao {
    private final SimpleJdbcTemplate source;
    private final PGobject pgObject = new PGobject();

    public final void saveFlight(Flight flight) throws SQLException {
        source.preparedStatement("insert into flights("
                        + "flight_id, flight_no, scheduled_departure, scheduled_arrival, departure_airport, "
                        + "arrival_airport, status, aircraft_code, actual_departure, actual_arrival"
                        + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                insertFlight -> {
                    insertFlight.setInt(1, flight.getFlightId());
                    insertFlight.setString(2, flight.getFlightNo());

                    pgObject.setType("timestamp");
                    pgObject.setValue(flight.getScheduledDeparture());
                    insertFlight.setObject(3, pgObject);

                    pgObject.setValue(flight.getScheduledArrival());
                    insertFlight.setObject(4, pgObject);

                    insertFlight.setString(5, flight.getDepartureAirport());
                    insertFlight.setString(6, flight.getArrivalAirport());
                    insertFlight.setString(7, flight.getStatus());
                    insertFlight.setString(8, flight.getAircraftCode());

                    pgObject.setValue(flight.getActualDeparture());
                    insertFlight.setObject(9, pgObject);

                    pgObject.setValue(flight.getActualArrival());
                    insertFlight.setObject(10, pgObject);

                    insertFlight.execute();
                });
    }

    public final ArrayList<CancelledFlightsByMonth> getCancelledFlightsByMonth() throws SQLException {
        return source.statement(stmt -> {
            ResultSet resultSet = stmt.executeQuery(
                    "select count(scheduled_departure) as numCancels, to_char(scheduled_departure, 'MM') "
                            + "as month from flights\n"
                            + "where status = 'Cancelled'\n"
                            + "group by to_char(scheduled_departure, 'MM')"
                            + "order by month;"
            );
            ArrayList<CancelledFlightsByMonth> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(
                        new CancelledFlightsByMonth(
                                Integer.valueOf(resultSet.getString("month")),
                                Integer.valueOf(resultSet.getString("numcancels"))
                        )
                );
            }
            return result;
        });
    }

    public final ArrayList<NumFlightsByWeekDay> getFlightsToMoscowByWeekDay() throws SQLException {
        return source.statement(stmt -> {
            ResultSet resultSet = stmt.executeQuery(
                    "select extract(isodow from scheduled_arrival) as day_of_week, count(*)\n"
                            + "from flights\n"
                            + "         join airports as arrival_airport on "
                            + "arrival_airport = arrival_airport.airport_code\n"
                            + "where arrival_airport.city->>'en' = 'Moscow'\n"
                            + "group by extract(isodow from scheduled_arrival)\n"
                            + "order by day_of_week;"
            );
            ArrayList<NumFlightsByWeekDay> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(
                        new NumFlightsByWeekDay(
                                Integer.valueOf(resultSet.getString("day_of_week")),
                                Integer.valueOf(resultSet.getString("count"))
                        )
                );
            }
            return result;
        });
    }

    public final ArrayList<NumFlightsByWeekDay> getFlightsFromMoscowByWeekDay() throws SQLException {
        return source.statement(stmt -> {
            ResultSet resultSet = stmt.executeQuery(
                    "select extract(isodow from scheduled_departure) as day_of_week, count(*)\n"
                            + "from flights\n"
                            + "         join airports as depart_airport "
                            + "on departure_airport = depart_airport.airport_code\n"
                            + "where depart_airport.city->>'en' = 'Moscow'\n"
                            + "group by extract(isodow from scheduled_departure)\n"
                            + "order by day_of_week;"
            );
            ArrayList<NumFlightsByWeekDay> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(
                        new NumFlightsByWeekDay(
                                Integer.valueOf(resultSet.getString("day_of_week")),
                                Integer.valueOf(resultSet.getString("count"))
                        )
                );
            }
            return result;
        });
    }

    public final void cancelFlightsByAircraftModel(String model) throws SQLException {
        source.statement(stmt -> {
            stmt.executeQuery("with cancelled_flights as (\n"
                    + "    UPDATE flights\n"
                    + "        SET status = 'Cancelled'\n"
                    + "        WHERE flight_id in\n"
                    + "              (\n"
                    + "                  select flight_id\n"
                    + "                  from flights\n"
                    + "                           join aircrafts a on flights.aircraft_code = a.aircraft_code\n"
                    + "                  where a.model ->> 'en' = '" + model + "'\n"
                    + "                    and status != 'Cancelled'\n"
                    + "              )\n"
                    + "        RETURNING *)\n"
                    + "delete\n"
                    + "from tickets\n"
                    + "where ticket_no in (select t.ticket_no\n"
                    + "                    from cancelled_flights\n"
                    + "                          join ticket_flights tf on tf.flight_id = cancelled_flights.flight_id\n"
                    + "                          join tickets t on tf.ticket_no = t.ticket_no)");
        });
    }

    public final ArrayList<CancelledCovidFlightsWithLossMoney> getCancelledCovidFlightsWithLossMoney(
            String fromDate, String toDate
    ) throws SQLException {
        return source.statement(stmt -> {
            ResultSet resultSet = stmt.executeQuery(
                    "with cancelled_flights as (\n"
                            + "    UPDATE flights\n"
                            + "        SET status = 'Cancelled'\n"
                            + "        WHERE flight_id in\n"
                            + "              (\n"
                            + "              select flight_id\n"
                            + "              from flights\n"
                            + "                       join airports as departure_airport\n"
                            + "                         on departure_airport.airport_code = flights.departure_airport\n"
                            + "                       join airports as arrival_airport on "
                            + "arrival_airport.airport_code = flights.arrival_airport\n"
                            + "              where (departure_airport.city ->> 'en' = 'Moscow' and\n"
                            + "                     scheduled_departure >= timestamp '" + fromDate + "' and\n"
                            + "                     scheduled_departure <= timestamp '" + toDate + "'\n"
                            + "                  or arrival_airport.city ->> 'en' = 'Moscow' "
                            + "and scheduled_arrival >= timestamp '" + fromDate + "' and\n"
                            + "                     scheduled_arrival <= timestamp '" + toDate + "')\n"
                            + "                and status != 'Cancelled'\n"
                            + "              )\n"
                            + "        RETURNING *)\n"
                            + "select sum(amount), extract(doy from cancelled_flights.scheduled_departure) as day\n"
                            + "from cancelled_flights\n"
                            + "join ticket_flights as tf on tf.flight_id = cancelled_flights.flight_id\n"
                            + "group by extract(doy from cancelled_flights.scheduled_departure)\n"
                            + "order by day"
            );
            ArrayList<CancelledCovidFlightsWithLossMoney> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(
                        new CancelledCovidFlightsWithLossMoney(
                                Double.valueOf(resultSet.getString("sum")),
                                Integer.valueOf(resultSet.getString("day"))
                        )
                );
            }
            return result;
        });
    }

    public final ArrayList<String> getFlightSeats(String flightNo) throws SQLException {
        return source.statement(stmt -> {
            ResultSet resultSet = stmt.executeQuery(
                    "select * from flights\n"
                            + "join seats s on flights.aircraft_code = s.aircraft_code\n"
                            + "where flight_no = '" + flightNo + "'"
            );
            ArrayList<String> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(
                        resultSet.getString("seat_no")
                );
            }
            return result;
        });
    }
}
