package cinema.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbConnection {
    private DbConnection() {
    }

    public static Connection open() throws SQLException {
        return DriverManager.getConnection(
                requiredEnv("CINEMA_DB_URL"),
                requiredEnv("CINEMA_DB_USER"),
                requiredEnv("CINEMA_DB_PASSWORD"));
    }

    private static String requiredEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("환경 변수 " + name + "을 설정해 주세요.");
        }
        return value;
    }
}
