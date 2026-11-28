# Java와 MySQL 연결 기초

영화 예매 앱에서 회원을 저장하려면, Java 서버가 **어느 MySQL에 어떤 계정으로 접속할지** 먼저 알아야 합니다. 접속에 성공하면 Java는 그 연결을 나타내는 `Connection` 객체를 받습니다. 그 연결에서 SQL을 실행하면 조회 결과나 변경한 행 수를 얻습니다.

가입과 로그인 코드의 흐름은 **접속 정보 읽기 → 연결 열기 → SQL 준비·실행 → 결과 읽기 → 자원 닫기**입니다.

## 1. Java 코드가 MySQL까지 가는 과정

```text
Java 서버의 MemberDao
  → 프로젝트의 DbConnection.open()
  → JDBC의 DriverManager.getConnection(...)
  → MySQL Connector/J 드라이버
  → MySQL 서버의 cinema 데이터베이스
```

| 구성 요소 | 역할 |
| --- | --- |
| JDBC | Java에서 DB 연결과 SQL 실행을 다루는 표준 API입니다. `java.sql` 패키지에서 `Connection`, `PreparedStatement`, `ResultSet` 등을 제공해요. |
| MySQL Connector/J | JDBC 호출을 받아 MySQL과 실제로 통신하는 드라이버입니다. 프로젝트의 Maven 설정을 통해 설치됩니다. |
| `DbConnection` | 프로젝트에서 만든 클래스입니다. `open()`이 실행 설정에서 정보를 읽어 `DriverManager.getConnection(url, user, password)`을 호출합니다. |
| `MemberDao` | 프로젝트에서 회원 SQL을 다루는 클래스입니다. `Connection`으로 `members` 테이블의 행을 저장하거나 조회합니다. |

Swing에서 가입 버튼을 누르면 요청은 Java 서버로 갑니다. 서버가 위 경로를 통해 DB에 접속합니다. [프로젝트의 DbConnection](../../../cinema/src/main/java/cinema/server/db/DbConnection.java), [프로젝트의 MemberDao](../../../cinema/src/main/java/cinema/server/auth/MemberDao.java), [프로젝트 Maven 설정](../../../cinema/pom.xml)

## 2. 연결을 열기 전에 필요한 정보

서버 실행 설정의 `CINEMA_DB_URL`은 다음 형식입니다.

```text
jdbc:mysql://127.0.0.1:3306/cinema?connectTimeout=5000&socketTimeout=5000
```

| URL 부분 | 읽는 방법 |
| --- | --- |
| `jdbc:mysql:` | MySQL용 JDBC URL입니다. |
| `127.0.0.1` | MySQL 서버 주소입니다. 이 값은 **Java 서버 프로그램이 실행되는 PC 자신**을 가리킵니다. |
| `3306` | MySQL이 연결을 받는 포트 번호입니다. |
| `/cinema` | 연결 직후 사용할 데이터베이스 이름입니다. `members` 테이블은 여기에 둡니다. |
| `?connectTimeout=5000&socketTimeout=5000` | `?` 뒤부터는 드라이버에 전달하는 접속 옵션입니다. 옵션이 여러 개면 `&`로 이어 씁니다. 숫자 단위는 밀리초입니다. |

`connectTimeout`은 연결을 맺을 때 기다리는 제한 시간입니다. `socketTimeout`은 연결한 뒤 소켓에서 데이터를 읽을 때의 제한 시간입니다. URL은 어느 DB에 어떻게 연결할지, `CINEMA_DB_USER`와 `CINEMA_DB_PASSWORD`는 어떤 MySQL 계정으로 인증할지를 정합니다. 서버 실행 설정에 넣는 비밀번호는 `cinema_app`의 MySQL 접속 비밀번호입니다. 영화 예매 회원 비밀번호는 별도로 가입 화면에서 입력합니다. [Eclipse 실행 설정](../../eclipse-setup.md), [MySQL JDBC URL 형식](https://dev.mysql.com/doc/connector-j/en/connector-j-reference-jdbc-url-format.html), [Connector/J 네트워크 옵션](https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-networking.html)

## 3. Connection은 무엇이고 어떤 상태를 가지나요?

`Connection connection = DbConnection.open();`에서 **왼쪽의 `Connection`은 Java 타입**, **소문자 `connection`은 방금 연 연결을 가리키는 변수**입니다. 접속이 성공하면 이 연결을 통해 SQL을 보낼 수 있습니다. 여러 SQL을 같은 작업 단위로 묶고 싶으면 같은 `Connection`에서 실행합니다.

| 연결의 상태·설정 | 의미 | 확인·변경 방법 |
| --- | --- | --- |
| 닫힘 여부 | 이 연결 객체가 닫혔는지 나타냅니다. 현재 통신이 가능한지 확인할 때는 연결 검사를 사용합니다. | `isClosed()`, `isValid(2)` |
| 자동 커밋 | 기본값은 `true`입니다. SQL 한 문장 처리가 완료될 때마다 DB가 변경을 자동으로 확정합니다. | `getAutoCommit()`, `setAutoCommit(false)` |
| 읽기 전용 모드 | 조회용 연결이라는 정보를 드라이버에 전달합니다. 실제 조회·저장 권한은 MySQL 계정에 부여한 권한으로 관리합니다. | `isReadOnly()`, `setReadOnly(true)` |

이 가이드의 회원 저장은 자동 커밋 상태로 `INSERT` 한 번을 실행합니다. 예매 확정에서 **예매 행 저장과 좌석 점유를 함께 성공시켜야 한다면**, 같은 연결에서 자동 커밋을 끄고 두 작업이 모두 끝난 뒤 `commit()`으로 확정할 수 있습니다. 중간에 실패하면 `rollback()`으로 그 작업 단위의 변경을 되돌립니다. 이 작업 단위를 **트랜잭션**이라고 합니다. [Java 21 Connection](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/Connection.html)

## 4. 데이터베이스, 테이블, 열, 행의 관계

```text
MySQL 서버
└─ cinema        데이터베이스
   └─ members    테이블
      ├─ 열      id, email, display_name, ...
      └─ 행      회원 번호 42, demo@example.test, 예시 회원, ...
```

| 열 예시 | MySQL에서 정한 속성 | Java에서 읽는 방법 |
| --- | --- | --- |
| `id` | `BIGINT` 정수, `PRIMARY KEY`로 회원 식별, `AUTO_INCREMENT`로 자동 번호 발급 | `getLong("id")` |
| `email` | `VARCHAR` 문자열, `UNIQUE`로 중복 저장 방지 | `getString("email")` |
| `display_name` | `VARCHAR` 문자열, `NOT NULL`로 값이 있어야 함 | `getString("display_name")` |
| `password_hash` | `VARBINARY` 바이트 데이터 | `getBytes("password_hash")` |

조회 결과에는 `SELECT`에 써 둔 열만 들어옵니다. 예를 들어 `SELECT display_name FROM members`의 결과에는 회원마다 표시 이름 열 하나가 있어요. 반면 `SELECT id, email ...`에서는 `getLong(1)`이 `id`, `getString(2)`가 `email`을 읽습니다. 숫자는 **조회 목록의 열 순서**입니다. [Java 21 ResultSet](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/ResultSet.html)

## 5. JDBC 객체가 맡는 일과 자주 쓰는 메서드

| 객체 | 무엇을 나타내나요? | 자주 쓰는 메서드 |
| --- | --- | --- |
| `Connection` | SQL을 실행할 DB 연결 한 개 | `prepareStatement(sql)`로 그 연결에서 실행할 SQL을 준비합니다. |
| `PreparedStatement` | 실행할 SQL과 `?`에 전달할 값 | `setString()`, `setInt()`, `setBytes()`로 값을 전달합니다. `executeQuery()`는 조회 결과를, `executeUpdate()`는 INSERT·UPDATE·DELETE로 영향받은 행 수를 돌려줍니다. |
| `ResultSet` | `SELECT` 결과나 자동 생성 키처럼 행과 열로 읽는 결과 | `next()`로 다음 행으로 이동하고, `getLong()`이나 `getString()`으로 그 행의 열을 읽습니다. |

`PreparedStatement`는 JDBC의 `Statement`를 확장한 타입입니다. 그래서 `Statement`에 정의된 `getGeneratedKeys()`를 `PreparedStatement`에서도 호출할 수 있습니다. 이 세 객체의 `close()`는 JDBC에 정의돼 있고, 아래 예제에서는 `try (...)`가 자동으로 호출합니다. [Java 21 PreparedStatement](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/PreparedStatement.html), [Java 21 Statement](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/Statement.html)

## 6. SELECT로 회원 이름 조회하기

이 예시에서는 이메일로 회원 한 명을 찾아 표시 이름을 읽습니다. `members.email`에는 `UNIQUE`가, `display_name`에는 `NOT NULL`이 적용돼 있다고 가정합니다. 아래 예제의 `findDisplayNameByEmail()`은 이 문서에서 붙인 메서드 이름입니다.

```java
import cinema.server.db.DbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
```

```java
static Optional<String> findDisplayNameByEmail(String email) throws SQLException {
    String sql = "SELECT display_name FROM members WHERE email = ?";

    // DB에 접속하고, ?가 포함된 SQL을 준비합니다.
    try (Connection connection = DbConnection.open();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, email); // 첫 번째 ?에 이메일을 전달합니다.

        // 값이 모두 준비됐으므로 여기서 SELECT를 실행합니다.
        try (ResultSet rows = statement.executeQuery()) {
            if (rows.next()) {
                return Optional.of(rows.getString("display_name"));
            }
            return Optional.empty();
        }
    }
}
```

`findDisplayNameByEmail("demo@example.test")`를 호출했다고 가정해 봅시다.

1. `prepareStatement()`가 실행할 SQL을 준비합니다. `?`은 전달할 값의 자리입니다.
2. `setString(1, email)`이 왼쪽에서 첫 번째 `?`에 이메일을 전달합니다. Java 배열은 0부터 세는 경우가 많지만, JDBC의 **입력 자리 번호와 결과 열 번호는 1부터** 셉니다.
3. `executeQuery()`가 실제 조회 결과인 `rows`를 돌려줍니다. 결과를 처음 받았을 때는 읽을 행이 아직 선택되지 않은 상태입니다.
4. `rows.next()`가 `true`이면 현재 행의 `display_name` 값을 읽습니다. 조회가 정상적으로 끝났고 일치하는 회원이 없으면 `Optional.empty()`를 반환합니다. `Optional<String>`은 문자열이 담겼거나 비어 있을 수 있는 반환값입니다.

[SELECT 실행과 값 전달](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/PreparedStatement.html), [ResultSet.next()의 동작](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/ResultSet.html#next())

## 7. INSERT로 회원 저장하고 자동 번호 받기

실제 `MemberDao.insert()`와 같은 순서입니다. 서비스가 이미 준비한 이메일, 표시 이름, 비밀번호 계산에 사용한 무작위 값인 `salt`, 계산 결과인 `hash`, 반복 횟수를 받습니다. DB의 `id` 열은 `AUTO_INCREMENT`로 회원 번호를 발급한다고 가정합니다.

```java
static long insertMember(String email, String name, byte[] salt,
                         byte[] hash, int iterations) throws SQLException {
    String sql = """
            INSERT INTO members
                (email, display_name, password_salt, password_hash, password_iterations)
            VALUES (?, ?, ?, ?, ?)
            """;

    // INSERT를 준비하면서 DB가 생성한 번호를 받을 수 있게 요청합니다.
    try (Connection connection = DbConnection.open();
         PreparedStatement statement =
                 connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, email);
        statement.setString(2, name);
        statement.setBytes(3, salt);
        statement.setBytes(4, hash);
        statement.setInt(5, iterations);

        statement.executeUpdate(); // 영향을 받은 행 수를 반환합니다.

        // 자동 발급된 번호는 생성 키 결과에서 읽습니다.
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (!keys.next()) {
                throw new SQLException("회원 번호를 읽지 못했습니다.");
            }
            return keys.getLong(1);
        }
    }
}
```

회원 한 명을 추가하고 DB가 회원 번호 **42**를 발급했다고 가정합니다. `executeUpdate()`는 **변경한 행 수 1**을 돌려주고, `getGeneratedKeys()`는 **번호 42가 들어 있는 결과 표**를 돌려줍니다.

결과 표를 처음 받으면 값은 이미 들어 있고, **어느 행을 읽을지는 아직 선택되지 않은 상태**입니다. `keys.next()`를 한 번 호출하면 읽을 행이 1행으로 정해지고 `true`가 반환됩니다.

```text
getGeneratedKeys() 직후
[ 1행 | 회원 번호 42 ]     현재 읽을 행: 선택된 행 없음

keys.next()를 한 번 호출한 직후
[ 1행 | 회원 번호 42 ] ◀ 현재 읽을 행     next()의 반환값: true

keys.getLong(1)을 호출한 직후
[ 1행 | 회원 번호 42 ] ◀ 현재 읽을 행     첫 번째 열의 값: 42
```

여기서 `getLong(1)`의 `1`은 **현재 행의 첫 번째 열**을 가리킵니다. 결과가 비어 있으면 첫 `next()` 호출이 `false`를 반환합니다. 여러 행을 모두 읽을 때는 `while (rows.next())`로 한 행씩 이동합니다. [생성 키 읽기](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/Statement.html#getGeneratedKeys()), [ResultSet.getLong(int)](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/ResultSet.html#getLong(int))

## 8. 연결 정리와 DB 오류 처리

- **`try (...)`는 자원 정리를 맡습니다.** 블록을 벗어나면 JDBC 객체를 자동으로 닫습니다. 위의 예제에서는 결과 → SQL 실행 객체 → DB 연결 순서입니다. `return`으로 나올 때도, 예외가 발생할 때도 정리합니다.
- **`throws SQLException`은 DB 예외를 호출한 코드에 전달할 수 있다는 선언입니다.** 예를 들어 `executeUpdate()`에서 중복 이메일 오류가 나면, 저장 메서드를 호출한 쪽이 `catch (SQLException error)`로 받아 사용자에게 보여줄 오류로 바꿀 수 있습니다.
- **`throw new SQLException(...)`은 그 줄에서 예외를 발생시킵니다.** 예제에서는 생성 키 결과가 비어서 회원 번호를 반환할 수 없을 때 사용합니다.

조회 결과가 **정상적으로 비어 있는 경우**에는 `Optional.empty()`를 반환합니다. **DB 연결이나 SQL 실행이 실패한 경우**에는 `SQLException`이 호출한 곳으로 전달됩니다. 그래서 호출하는 코드에서 두 상황에 다른 안내를 보여줄 수 있습니다. [Java 21 SQLException](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/SQLException.html)

## 확인해 보기

- URL의 `127.0.0.1`과 `3306`은 각각 무엇인가요? → Java 서버에서 바라보는 MySQL의 주소와 포트입니다.
- 예매 저장과 좌석 점유를 한꺼번에 확정하려면 무엇이 필요한가요? → 같은 `Connection`에서 두 작업을 한 트랜잭션으로 묶습니다.
- `setString(1, email)`의 `1`은 어디를 가리키나요? → SQL의 첫 번째 `?`입니다.
- `keys.getLong(1)`의 `1`은 어디를 가리키나요? → 현재 결과 행의 첫 번째 열입니다.
- `INSERT`로 한 행이 추가됐고 회원 번호가 42라면, 행 수와 회원 번호는 각각 어디서 받나요? → `executeUpdate()`와 `getGeneratedKeys()`에서 받은 결과를 각각 사용합니다.
