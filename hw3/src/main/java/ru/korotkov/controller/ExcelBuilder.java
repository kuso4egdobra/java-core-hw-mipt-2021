package ru.korotkov.controller;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import ru.korotkov.result.CancelledFlightsByMonth;
import ru.korotkov.result.CityNumCanceledFlights;
import ru.korotkov.result.CityShortestRoute;
import ru.korotkov.result.NumFlightsByWeekDay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Creates Excel tables representing results of QueryRunner
 */
public class ExcelBuilder {
    private static final String OUTPUT_XLS_PATH = "./query_results/";

    /**
     * Creates excel table with given data
     *
     * @param heading     sheet's name
     * @param columnNames column names
     * @param data        list of rows
     * @param filePath    where to store the table
     * @throws IOException if a problem while saving the table to the filesystem occurred
     */
    private void buildExcelTable(String heading, String[] columnNames,
                                 Iterable<String[]> data, String filePath) throws IOException {
        try (Workbook book = new HSSFWorkbook()) {
            Sheet sheet = book.createSheet(heading);
            Row firstRow = sheet.createRow(0);

            CellStyle style = book.createCellStyle();
            Font font = book.createFont();
            font.setFontHeightInPoints((short) 10);
            font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
            style.setLocked(true);

            // Fill first row with columns names
            int j = 0;
            for (String rawCell : columnNames) {
                Cell currentCell = firstRow.createCell(j++);
                currentCell.setCellValue(rawCell);
                currentCell.setCellStyle(style);
            }

            // Fill all the rest rows
            int i = 1;
            for (String[] rawRow : data) {
                Row row = sheet.createRow(i++);
                j = 0;
                for (String rawCell : rawRow) {
                    Cell currentCell = row.createCell(j++);
                    currentCell.setCellValue(rawCell);
                }
            }

            // Resize columns to fit their data
            for (int x = 0; x < sheet.getRow(0).getPhysicalNumberOfCells(); x++) {
                sheet.autoSizeColumn(x);
            }

            // Save the table
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            file.createNewFile();
            book.write(new FileOutputStream(filePath));
        }
    }

    public final void buildTableForCitiesWithSeveralAirports(Map<String, ArrayList<String>> cities)
            throws IOException, SQLException {
        List<String[]> rawData = new ArrayList<>(cities.size());
        for (String key : cities.keySet()) {
            rawData.add(new String[]{key, cities.get(key).toString()});
        }
        buildExcelTable(
                "Города, в которых несколько аэропортов",
                new String[]{"Город", "Список аэропортов"},
                rawData,
                OUTPUT_XLS_PATH + "taskB1.xlsx"
        );
    }

    public final void buildTableForCitiesMostFreqCancelledFlights(ArrayList<CityNumCanceledFlights> cities)
            throws IOException, SQLException {
        List<String[]> rawData = new ArrayList<>(cities.size());
        for (CityNumCanceledFlights city : cities) {
            rawData.add(new String[]{city.getCity(), city.getNumCanceledFlights().toString()});
        }
        buildExcelTable(
                "Города, где чаще всего отменяли рейсы",
                new String[]{"Город", "Кол-во отмененных рейсов"},
                rawData,
                OUTPUT_XLS_PATH + "taskB2.xlsx"
        );
    }

    public final void buildTableForShortestRoute(ArrayList<CityShortestRoute> cities)
            throws IOException, SQLException {
        List<String[]> rawData = new ArrayList<>(cities.size());
        for (CityShortestRoute city : cities) {
            rawData.add(new String[]{city.getDepartureCity(), city.getArrivalCity(), city.getAvgTime()});
        }
        buildExcelTable(
                "Найти самый короткий маршрут",
                new String[]{"Город", "Пункт прибытия", "Средняя продолжительность полёта"},
                rawData, OUTPUT_XLS_PATH + "taskB3.xlsx"
        );
    }

    public final void buildTableForNumCancels(ArrayList<CancelledFlightsByMonth> cities)
            throws IOException, SQLException {
        List<String[]> rawData = new ArrayList<>(cities.size());
        for (CancelledFlightsByMonth city : cities) {
            rawData.add(new String[]{String.valueOf(city.getMonth()), String.valueOf(city.getNumCancels())});
        }
        buildExcelTable(
                "Найти кол-во отмен рейсов по месяцам",
                new String[]{"Месяц", "Кол-во отмен"},
                rawData,
                OUTPUT_XLS_PATH + "taskB4.xlsx"
        );
    }

    public final void buildTableForFlightsMoscow(ArrayList<NumFlightsByWeekDay> cities, Boolean isToMoscow)
            throws IOException, SQLException {
        List<String[]> rawData = new ArrayList<>(cities.size());
        for (NumFlightsByWeekDay city : cities) {
            rawData.add(new String[]{String.valueOf(city.getDayOfWeek()), String.valueOf(city.getCountFlights())});
        }
        if (isToMoscow) {
            buildExcelTable(
                    "Кол-во рейсов в Москву по дням недели",
                    new String[]{"День недели (1 - Пн; 7 - Вс)", "Кол-во рейсов"},
                    rawData,
                    OUTPUT_XLS_PATH + "taskB5-1.xlsx"
            );
        } else {
            buildExcelTable(
                    "Кол-во рейсов из Москвы по дням недели",
                    new String[]{"День недели (1 - Пн; 7 - Вс)", "Кол-во рейсов"},
                    rawData,
                    OUTPUT_XLS_PATH + "taskB5-2.xlsx"
            );
        }
    }
}
