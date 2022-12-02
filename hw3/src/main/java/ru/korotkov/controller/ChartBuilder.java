package ru.korotkov.controller;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import ru.korotkov.result.CancelledCovidFlightsWithLossMoney;
import ru.korotkov.result.CancelledFlightsByMonth;
import ru.korotkov.result.NumFlightsByWeekDay;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates charts representing the results of some QueryRunner queries
 */
public class ChartBuilder {
    private static final String OUTPUT_PNG_PATH = "./charts/";

    private static final int CHART_WIDTH = 1920;
    private static final int CHART_HEIGHT = 1080;
    private static final int SIZE_TICK_LABEL_FONT = 25;
    private static final int SIZE_LABEL_FONT = 30;
    private static final int SIZE_CHART_FONT = 40;

    /**
     * Converts List of strings to JFreeChart dataset
     *
     * @param data List of String.
     *             List[0] -- numeric values for Y axis
     *             List[1] -- string values for X axis
     * @return JFreeChart dataset
     */
    private DefaultCategoryDataset fillDataset(List<String[]> data) {
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String[] row : data) {
            dataset.addValue(Double.parseDouble(row[1]), "default", row[0]);
        }
        return dataset;
    }

    private DefaultCategoryDataset fillDatasetDouble(List<String[]> data1, List<String[]> data2) {
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String[] row : data1) {
            dataset.addValue(Double.parseDouble(row[1]), "Рейсы из Москвы", row[0]);
        }
        for (String[] row : data2) {
            dataset.addValue(Double.parseDouble(row[1]), "Рейсы в Москву", row[0]);
        }
        return dataset;
    }

    /**
     * Apply default settings to the chart and set up title
     *
     * @param barChart chart to set up
     * @param title    title
     */
    private void configureChart(JFreeChart barChart, String title) {
        CategoryPlot plot = barChart.getCategoryPlot();
        CategoryAxis axis = plot.getDomainAxis();

        Font font = new Font("Cambria", Font.BOLD, SIZE_TICK_LABEL_FONT);
        axis.setTickLabelFont(font);
        Font font3 = new Font("Cambria", Font.BOLD, SIZE_LABEL_FONT);
        barChart.setTitle(new org.jfree.chart.title.TextTitle(
                title,
                new java.awt.Font("Cambria", java.awt.Font.BOLD, SIZE_CHART_FONT))
        );

        plot.getDomainAxis().setLabelFont(font3);
        plot.getRangeAxis().setLabelFont(font3);
        CategoryPlot categoryPlot = (CategoryPlot) barChart.getPlot();
        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
    }

    /**
     * Creates a chart, representing the number of cancelled flights as a function of month
     *
     * @throws IOException if a problem while saving the chart to the filesystem occurred
     */
    public final void createBarChartForNumCancels(ArrayList<CancelledFlightsByMonth> cities)
            throws IOException, SQLException {
        List<String[]> rawData = new ArrayList<>(cities.size());
        for (CancelledFlightsByMonth city : cities) {
            rawData.add(new String[]{String.valueOf(city.getMonth()), String.valueOf(city.getNumCancels())});
        }
        DefaultCategoryDataset dataset = fillDataset(rawData);
        String title = "Кол-во отмен рейсов по месяцам";
        String categoryAxis = "Месяц";
        String valueAxis = "Кол-во отмен";

        JFreeChart barChart = ChartFactory.createBarChart(title, categoryAxis, valueAxis, dataset,
                PlotOrientation.VERTICAL, false, false, false);
        configureChart(barChart, title);
        File file = new File(OUTPUT_PNG_PATH + "chart.png");
        file.getParentFile().mkdirs();
        file.createNewFile();
        ChartUtilities.saveChartAsPNG(file, barChart, CHART_WIDTH, CHART_HEIGHT);
    }

    public final void createBarChartForCovidFlights(ArrayList<CancelledCovidFlightsWithLossMoney> cities)
            throws IOException, SQLException {
        List<String[]> rawData = new ArrayList<>(cities.size());
        for (CancelledCovidFlightsWithLossMoney city : cities) {
            rawData.add(new String[]{String.valueOf(city.getDay()), String.valueOf(city.getSum())});
        }
        DefaultCategoryDataset dataset = fillDataset(rawData);
        String title = "Убыток, который теряют компании-перевозчики по дням";
        String categoryAxis = "День";
        String valueAxis = "Убыток за день";

        JFreeChart barChart = ChartFactory.createBarChart(title, categoryAxis, valueAxis, dataset,
                PlotOrientation.VERTICAL, false, false, false);
        configureChart(barChart, title);
        File file = new File(OUTPUT_PNG_PATH + "chart3.png");
        file.getParentFile().mkdirs();
        file.createNewFile();
        ChartUtilities.saveChartAsPNG(file, barChart, CHART_WIDTH, CHART_HEIGHT);
    }

    public final void createTwoGraph(
            ArrayList<NumFlightsByWeekDay> citiesFromMoscow, ArrayList<NumFlightsByWeekDay> citiesToMoscow
    ) throws IOException {
        List<String[]> rawData1 = new ArrayList<>(citiesFromMoscow.size());
        for (NumFlightsByWeekDay city : citiesFromMoscow) {
            rawData1.add(new String[]{String.valueOf(city.getDayOfWeek()), String.valueOf(city.getCountFlights())});
        }
        List<String[]> rawData2 = new ArrayList<>(citiesToMoscow.size());
        for (NumFlightsByWeekDay city : citiesToMoscow) {
            rawData2.add(new String[]{String.valueOf(city.getDayOfWeek()), String.valueOf(city.getCountFlights())});
        }

        DefaultCategoryDataset dataset = fillDatasetDouble(rawData1, rawData2);
        String title = "Кол-во отмен рейсов по месяцам";
        String categoryAxis = "Месяц";
        String valueAxis = "Кол-во отмен";

        JFreeChart barChart = ChartFactory.createBarChart(title, categoryAxis, valueAxis, dataset,
                PlotOrientation.VERTICAL, true, true, false);
        configureChart(barChart, title);
        File file = new File(OUTPUT_PNG_PATH + "chart2.png");
        file.getParentFile().mkdirs();
        file.createNewFile();
        ChartUtilities.saveChartAsPNG(file, barChart, CHART_WIDTH, CHART_HEIGHT);
    }
}
