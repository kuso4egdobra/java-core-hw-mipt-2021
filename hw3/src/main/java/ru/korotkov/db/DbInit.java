package ru.korotkov.db;
import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * Initializes database
 */
@AllArgsConstructor
public class DbInit {
    private final SimpleJdbcTemplate source;

    private String getSQL(String name) throws IOException, SQLException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(name),
                        StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining("\n"));
        }
    }

    public final void create() throws SQLException, IOException {
        String sql = getSQL("src/main/resources/dbcreate.sql");
        source.statement(stmt -> {
            stmt.execute(sql);
        });
    }
}
