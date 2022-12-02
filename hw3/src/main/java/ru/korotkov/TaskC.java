package ru.korotkov;

import ru.korotkov.controller.ChartBuilder;
import ru.korotkov.controller.ExcelBuilder;
import ru.korotkov.controller.MainController;
import ru.korotkov.db.SimpleJdbcTemplate;

import java.io.IOException;
import java.sql.SQLException;

import static ru.korotkov.db.SourceInit.initSource;

public class TaskC {
    public static void main(String[] args) throws IOException, SQLException {
        SimpleJdbcTemplate source = initSource();
        MainController controller = new MainController(source);
        ExcelBuilder excel = new ExcelBuilder();
        ChartBuilder chart = new ChartBuilder();

        excel.buildTableForCitiesWithSeveralAirports(controller.getCitiesHavingSeveralAirports());
        excel.buildTableForCitiesMostFreqCancelledFlights(controller.getCitiesNumCanceledFLights());
        excel.buildTableForShortestRoute(controller.getCitiesWithShortestRoute());
        excel.buildTableForNumCancels(controller.getCancelledFlightsByMonth());
        excel.buildTableForFlightsMoscow(controller.getFlightsToMoscowByWeekDay(), true);
        excel.buildTableForFlightsMoscow(controller.getFlightsFromMoscowByWeekDay(), false);

        chart.createBarChartForNumCancels(controller.getCancelledFlightsByMonth());
        chart.createTwoGraph(controller.getFlightsFromMoscowByWeekDay(), controller.getFlightsToMoscowByWeekDay());
        chart.createBarChartForCovidFlights(
                controller.getCancelledCovidFlightsWithLossMoney("2017-08-01", "2017-08-15")
        );
    }
}
