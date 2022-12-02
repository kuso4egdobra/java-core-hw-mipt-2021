package ru.korotkov;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import ru.korotkov.dao.*;
import ru.korotkov.db.SimpleJdbcTemplate;
import ru.korotkov.domain.*;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static ru.korotkov.db.SourceInit.initSource;

public class TaskA {

    public static void main(String[] args) throws IOException, SQLException {
        SimpleJdbcTemplate source = initSource();
//        new DbInit(source).create();
        AircraftDao aircraftDao = new AircraftDao(source);
        AirportDao airportDao = new AirportDao(source);
        BoardingPassDao boardingPassDao = new BoardingPassDao(source);
        FlightDao flightDao = new FlightDao(source);
        TicketDao ticketDao = new TicketDao(source);
        TicketFlightDao ticketFlightDao = new TicketFlightDao(source);
        BookingDao bookingDao = new BookingDao(source);
        SeatDao seatDao = new SeatDao(source);

        ArrayList<String> tableNames = new ArrayList<>(Arrays.asList(
                "aircrafts", "airports", "flights", "tickets",
                "ticket_flights", "boarding_passes", "bookings", "seats"
        ));
        for (String tableName : tableNames) {
            URL url = new URL("https://storage.yandexcloud.net/airtrans-small/" + tableName + ".csv");
            CSVFormat csvFormat = CSVFormat.DEFAULT;
            System.out.println(url);
            try (CSVParser csvParser = CSVParser.parse(url, StandardCharsets.UTF_8, csvFormat)) {
                for (CSVRecord csvRecord : csvParser) {
                    switch (tableName) {
                        case "aircrafts": {
                            String aircraftCode = csvRecord.get(0);
                            String model = csvRecord.get(1);
                            Integer range = Integer.valueOf(csvRecord.get(2));

                            Aircraft aircraft = new Aircraft(aircraftCode, model, range);
                            aircraftDao.saveAircraft(aircraft);
                            break;
                        }
                        case "airports": {
                            String airportCode = csvRecord.get(0);
                            String airportName = csvRecord.get(1);
                            String city = csvRecord.get(2);
                            String coordinates = csvRecord.get(3);
                            String timezone = csvRecord.get(4);

                            Airport airport = new Airport(airportCode, airportName, city, coordinates, timezone);
                            airportDao.saveAirport(airport);

                            break;
                        }
                        case "flights": {
                            Integer flightId = Integer.valueOf(csvRecord.get(0));
                            String flightNo = csvRecord.get(1);
                            String scheduledDeparture = csvRecord.get(2);
                            String scheduledArrival = csvRecord.get(3);
                            String departureAirport = csvRecord.get(4);
                            String arrivalAirport = csvRecord.get(5);
                            String status = csvRecord.get(6);
                            String aircraftCode = csvRecord.get(7);
                            String actualDeparture = !csvRecord.get(8).isEmpty() ? csvRecord.get(8) : null;
                            String actualArrival = !csvRecord.get(9).isEmpty() ? csvRecord.get(9) : null;

                            Flight flight = new Flight(
                                    flightId, flightNo, scheduledDeparture, scheduledArrival, departureAirport,
                                    arrivalAirport, status, aircraftCode, actualDeparture, actualArrival);
                            flightDao.saveFlight(flight);
                            break;
                        }
                        case "tickets": {
                            String ticketNo = csvRecord.get(0);
                            String bookRef = csvRecord.get(1);
                            String passengerId = csvRecord.get(2);
                            String passengerName = csvRecord.get(3);
                            String contactData = !csvRecord.get(4).isEmpty() ? csvRecord.get(4) : null;

                            Ticket ticket = new Ticket(ticketNo, bookRef, passengerId, passengerName, contactData);
                            ticketDao.saveTicket(ticket);
                            break;
                        }
                        case "ticket_flights": {
                            String ticketNo = csvRecord.get(0);
                            Integer flightId = Integer.valueOf(csvRecord.get(1));
                            String fareConditions = csvRecord.get(2);
                            Double amount = Double.valueOf(csvRecord.get(3));

                            TicketFlight ticketFlight = new TicketFlight(ticketNo, flightId, fareConditions, amount);
                            ticketFlightDao.saveTicketFlight(ticketFlight);
                            break;
                        }
                        case "bookings": {
                            String bookRef = csvRecord.get(0);
                            String bookDate = csvRecord.get(1);
                            Double totalAmount = Double.valueOf(csvRecord.get(2));

                            Booking booking = new Booking(bookRef, bookDate, totalAmount);
                            bookingDao.saveBooking(booking);
                            break;
                        }
                        case "seats" : {
                            String aircraftCode = csvRecord.get(0);
                            String seatNo = csvRecord.get(1);
                            String fareConditions = csvRecord.get(2);

                            Seat seat = new Seat(aircraftCode, seatNo, fareConditions);
                            seatDao.saveSeat(seat);
                            break;
                        }
                        case "boarding_passes": {
                            String ticketNo = csvRecord.get(0);
                            Integer flightId = Integer.valueOf(csvRecord.get(1));
                            Integer boardingNo = Integer.valueOf(csvRecord.get(2));
                            String seatNo = csvRecord.get(3);

                            BoardingPass boardingPass = new BoardingPass(ticketNo, flightId, boardingNo, seatNo);
                            boardingPassDao.saveBoardingPass(boardingPass);
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}



