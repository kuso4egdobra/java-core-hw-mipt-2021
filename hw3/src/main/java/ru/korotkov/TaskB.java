package ru.korotkov;

import ru.korotkov.controller.MainController;
import ru.korotkov.db.SimpleJdbcTemplate;
import ru.korotkov.domain.Ticket;

import java.io.IOException;
import java.sql.SQLException;

import static ru.korotkov.db.SourceInit.initSource;

public class TaskB {
    public static void main(String[] args) throws IOException, SQLException {
        SimpleJdbcTemplate source = initSource();
        MainController controller = new MainController(source);

        System.out.println(controller.getCitiesHavingSeveralAirports());
        System.out.println(controller.getCitiesNumCanceledFLights());
        System.out.println(controller.getCitiesWithShortestRoute());
        System.out.println(controller.getCancelledFlightsByMonth());
        System.out.println(controller.getFlightsToMoscowByWeekDay());
        System.out.println(controller.getFlightsFromMoscowByWeekDay());
        controller.cancelFlightsByAircraftModel("Airbus A319-100");
        controller.getCancelledCovidFlightsWithLossMoney("2017-08-01", "2017-08-15");
        controller.addNewTicket(
                new Ticket(
                        "0",
                        "0",
                        "0",
                        "Name",
                        "{\"phone\": \"+70127117011\"}"
                ),
                "PG0134",
                "20D");
    }
}
