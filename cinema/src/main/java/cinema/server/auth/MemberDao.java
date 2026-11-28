package cinema.server.auth;

import cinema.server.db.DbConnection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class MemberDao {

    // SELECT로 읽은 회원 한 행을 담는다
    //id(), hash() 같은 읽기 메서드가 자동으로 생깁니다.
    record Member(
            long id,
            String displayName,
            byte[] salt,
            byte[] hash,
            int iterations) {}

    //저장 후 생성된 회원 번호 반환
    long insert(String email, String name, byte[] salt, byte[] hash, int iterations)
            throws SQLException {

        //sql의 VALUES을 일단 ?로 채운 후 나중에 채움
        String sql = """
                INSERT INTO members
                    (email, display_name, password_salt, password_hash, password_iterations)
                VALUES (?, ?, ?, ?, ?)
                """;

        // try 괄호에서 연 DB 자원은 메서드를 벗어날 때 자동으로 닫힘
        // INSERT 후 자동 발급된 번호까지 읽겠다고 드라이버에 요청합니다.
        try (var connection = DbConnection.open();
             var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            //아까 sql에서 비운 값을 채움
            // SQL의 ?는 왼쪽부터 1번
            statement.setString(1, email);
            statement.setString(2, name);
            statement.setBytes(3, salt);
            statement.setBytes(4, hash);
            statement.setInt(5, iterations);

            // 여기서 INSERT가 실행, 반환값은 영향을 받은 행 수
            statement.executeUpdate();

            // INSERT의 결과 행 수와 새 회원 번호는 별개라서 => 생성 키를 따로 읽기
            try (var keys = statement.getGeneratedKeys()) {
                // 결과 커서를 첫 행으로 옮깁니다. 첫 행이 없으면 반환할 번호가 없습니다.
                if (!keys.next()) {
                    // 이 줄에 도달한 순간 예외를 발생시켜 정상 반환을 중단합니다.
                    throw new SQLException("회원 번호를 읽지 못했습니다.");
                }
                return keys.getLong(1);
            }
        }

    }

    // 정상 조회 결과는 Member, 정상 조회 실패시 빈 Optional 반환
    //조회 자체의 실패는 예외로 알립니다.
    Optional<Member> findByEmail(String email) throws SQLException {
        String sql = """
                SELECT id, display_name, password_salt, password_hash, password_iterations
                FROM members
                WHERE email = ?
                """;

        try (var connection = DbConnection.open();
             var statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);

            // executeQuery()는 SELECT 결과를 반환합니다. next()로 첫 행의 존재를 확인합니다.
            try (var rows = statement.executeQuery()) {
                if (!rows.next()) {
                    return Optional.empty();
                }

                //현재 읽고 있는 행에서 열 이름으로 값을 꺼냅니다.
                long id = rows.getLong("id");                      // 예: 42
                String name = rows.getString("display_name");    // 예: "민지"
                byte[] salt = rows.getBytes("password_salt");
                byte[] hash = rows.getBytes("password_hash");
                int iterations = rows.getInt("password_iterations");

                //꺼낸 값 다섯 개로 회원 객체 하나를 만듭니다.
                Member member = new Member(id, name, salt, hash, iterations);

                // 멤버를 호출한 서비스에 반환
                return Optional.of(member);
            }
        }
    }
}
