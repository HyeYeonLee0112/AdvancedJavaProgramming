package cinema.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import cinema.dao.DbConnection;

public class DbCheck {
    public static void main(String[] args) throws SQLException {
        try (Connection connection = DbConnection.open();
             PreparedStatement statement = connection.prepareStatement("SELECT 1");
             ResultSet result = statement.executeQuery()) {
            if (!result.next() || result.getInt(1) != 1) {
                throw new SQLException("SELECT 1의 결과를 확인할 수 없습니다.");
            }
            System.out.println("MySQL 연결 성공 · SELECT 1 = 1");
        }
    }
}
