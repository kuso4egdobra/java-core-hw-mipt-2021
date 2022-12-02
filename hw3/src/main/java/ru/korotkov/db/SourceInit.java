package ru.korotkov.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.sql.SQLException;

public class SourceInit {
    public static SimpleJdbcTemplate initSource() throws SQLException, IOException {
        String host = "localhost";
        String port = "5432";
        String database = "postgres";
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;

        HikariConfig config = new HikariConfig();
        HikariDataSource ds;
        config.setJdbcUrl(url);
        config.setUsername("postgres");
        config.setPassword("postgres");
        ds = new HikariDataSource(config);

        return new SimpleJdbcTemplate(ds);
    }
}
