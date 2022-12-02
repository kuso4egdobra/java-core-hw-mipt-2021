package ru.korotkov.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ru.korotkov.dao.AirportDao;
import ru.korotkov.dao.FlightDao;
import ru.korotkov.dao.TicketDao;
import ru.korotkov.db.SimpleJdbcTemplate;
import ru.korotkov.domain.Airport;
import ru.korotkov.domain.Ticket;
import ru.korotkov.result.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainController {
    private final AirportDao airportDao;
    private final FlightDao flightDao;
    private final TicketDao ticketDao;

    public MainController(SimpleJdbcTemplate source) {
        airportDao = new AirportDao(source);
        flightDao = new FlightDao(source);
        ticketDao = new TicketDao(source);
    }

    public final Map<String, ArrayList<String>> getCitiesHavingSeveralAirports() throws SQLException {
        Set<Airport> airportSet = airportDao.getAirports();
        Gson g = new Gson();
        Map<String, ArrayList<String>> citiesDict = new HashMap<>();
        for (Airport airport : airportSet) {
            JsonObject cityJson = g.fromJson(airport.getCity(), JsonObject.class);
            String city = cityJson.get("ru").toString();

            ArrayList<String> airportCodes;
            if (citiesDict.containsKey(city)) {
                airportCodes = citiesDict.get(city);
            } else {
                airportCodes = new ArrayList<>();
            }
            airportCodes.add(airport.getAirportCode());
            citiesDict.put(city, airportCodes);
        }

        return citiesDict;
    }

    public final ArrayList<CityNumCanceledFlights> getCitiesNumCanceledFLights() throws SQLException {
        return airportDao.getCitiesNumCanceledFLights();
    }

    public final ArrayList<CityShortestRoute> getCitiesWithShortestRoute() throws SQLException {
        return airportDao.getCitiesWithShortestRoute();
    }

    public final ArrayList<CancelledFlightsByMonth> getCancelledFlightsByMonth() throws SQLException {
        return flightDao.getCancelledFlightsByMonth();
    }

    public final ArrayList<NumFlightsByWeekDay> getFlightsToMoscowByWeekDay() throws SQLException {
        return flightDao.getFlightsToMoscowByWeekDay();
    }

    public final ArrayList<NumFlightsByWeekDay> getFlightsFromMoscowByWeekDay() throws SQLException {
        return flightDao.getFlightsFromMoscowByWeekDay();
    }

    public final void cancelFlightsByAircraftModel(String model) throws SQLException {
        flightDao.cancelFlightsByAircraftModel(model);
    }

    public final ArrayList<CancelledCovidFlightsWithLossMoney> getCancelledCovidFlightsWithLossMoney(
            String fromDate, String toDate
    ) throws SQLException {
        return flightDao.getCancelledCovidFlightsWithLossMoney(fromDate, toDate);
    }

    public final void addNewTicket(Ticket ticket, String flightNo, String seatNo) throws SQLException {
        ArrayList<String> flightSeats = flightDao.getFlightSeats(flightNo);
        if (!flightSeats.isEmpty() && flightSeats.contains(seatNo)) {
            ticketDao.saveTicket(ticket);
        }
    }
}
