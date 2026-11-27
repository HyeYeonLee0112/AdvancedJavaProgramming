# Eclipse에서 영화 예매 프로젝트 따라 구성하기

첫 목표는 **FlatLaf가 적용된 Swing 창을 띄우고, Java에서 MySQL에 `SELECT 1`을 보내 결과를 받는 것**입니다. 처음에는 이 두 동작에 필요한 클래스 세 개만 만듭니다. 영화·회원·좌석·예매 클래스는 실제로 필요한 데이터가 정해질 때 기능별로 추가하면 됩니다.

최종 연결은 `Swing 화면 → Java 소켓 서버 → MySQL`입니다. 아래에서 연결을 확인하는 `DbCheck`는 서버 쪽에서 실행하는 점검 프로그램입니다. 예매 화면은 소켓 서버에 요청하고, 서버가 MySQL에 접속합니다. 화면과 서버의 역할은 [프로젝트 구성](architecture.md)에서 볼 수 있습니다.

## 1. Eclipse에 새 프로젝트 만들기

이 예제는 PC에서 확인한 **JDK 21과 MySQL 8.0**을 기준으로 합니다. 팀이나 수업에서 JDK 버전을 지정했다면 팀원과 먼저 맞춥니다.

**기본 틀이 있는 경우** Eclipse에서 **File → Import… → Maven → Existing Maven Projects**를 열고, **Root Directory**에 저장소의 `cinema` 폴더를 지정합니다. 목록의 `pom.xml`을 선택하고 **Finish**를 누릅니다. 2단계에서 `ClientMain`을 실행하고, 3단계에서 로컬 MySQL을 준비해 `DbCheck`를 실행하면 됩니다.

**빈 폴더에서 직접 만들 경우** 아래 순서대로 프로젝트를 만듭니다.

1. Eclipse에서 **Window → Preferences → Java → Installed JREs**를 열고 JDK 21이 등록돼 있는지 확인합니다. 목록에 없다면 **Add… → Standard VM**에서 JDK 21 설치 폴더를 선택합니다.
2. **File → New → Other… → Maven → Maven Project**를 엽니다. Maven은 라이브러리 이름과 버전을 `pom.xml`에 적어 두면 팀원이 같은 라이브러리를 내려받도록 도와줍니다.
3. **Create a simple project (skip archetype selection)**에 체크합니다. **Use default Workspace location** 체크를 해제하고 `AdvancedJavaProgramming/cinema` 폴더를 프로젝트 위치로 선택합니다. 폴더가 아직 없다면 새로 만듭니다.
4. **Group Id**는 `cinema`, **Artifact Id**는 `cinema`, **Packaging**은 `jar`로 입력하고 **Finish**를 누릅니다.

PC에 남아 있는 `app/`, `lib/`와 경로가 겹치지 않게 영화 예매 프로젝트는 `cinema/`에 둡니다. Eclipse에 Maven 메뉴가 보이지 않으면 Maven 통합 도구인 m2e가 설치돼 있는지 확인합니다. [Eclipse m2e 공식 문서](https://eclipse.dev/m2e/)

프로젝트를 만든 다음 `cinema/pom.xml` 내용을 아래와 같이 바꿉니다.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>cinema</groupId>
    <artifactId>cinema</artifactId>
    <version>0.1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.release>21</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.formdev</groupId>
            <artifactId>flatlaf</artifactId>
            <version>3.7.2</version>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>9.7.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.14.1</version>
            </plugin>
        </plugins>
    </build>
</project>
```

파일을 저장하고 프로젝트 우클릭 → **Maven → Update Project…**를 실행합니다. **Maven Dependencies**에 FlatLaf와 MySQL Connector/J가 나타나는지 확인합니다. Swing은 JDK에 포함돼 있습니다. FlatLaf는 Swing의 버튼·입력창 스타일을 바꾸고, Connector/J는 Java가 MySQL과 통신하게 해 줍니다. [FlatLaf 공식 릴리스](https://github.com/JFormDesigner/FlatLaf/releases/tag/3.7.2), [Connector/J 공식 설치 문서](https://dev.mysql.com/doc/connector-j/en/connector-j-installing-maven.html), [Maven의 Java 버전 설정](https://maven.apache.org/guides/mini/guide-java-target.html)

## 2. Swing 창과 FlatLaf 확인하기

Package Explorer에서 `src/main/java`를 우클릭해 **New → Class**를 선택합니다. **Package**에 `cinema.client`, **Name**에 `ClientMain`을 입력하고 생성한 클래스에 아래 코드를 넣습니다. `src/main/java`가 없으면 Eclipse의 **New → Source Folder**에서 먼저 만듭니다.

이 코드는 테마를 적용하고 창을 띄운 뒤, 버튼을 클릭하면 안내 문구를 바꿉니다. 테마는 Swing 화면 요소를 만들기 전에 적용합니다. `SwingUtilities.invokeLater`는 Swing 화면을 그리는 전용 스레드에서 창을 만들게 합니다. [FlatLaf 시작 방법](https://www.formdev.com/flatlaf/)

```java
package cinema.client;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatLightLaf;

public class ClientMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (!FlatLightLaf.setup()) {
                throw new IllegalStateException("FlatLaf 설정에 실패했습니다.");
            }

            JFrame frame = new JFrame("영화 예매");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JLabel status = new JLabel("영화 예매 앱", SwingConstants.CENTER);
            JButton button = new JButton("화면 동작 확인");
            button.addActionListener(event -> status.setText("버튼 클릭 성공"));

            JPanel content = new JPanel(new BorderLayout(0, 16));
            content.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
            content.add(status, BorderLayout.CENTER);
            content.add(button, BorderLayout.SOUTH);

            frame.setContentPane(content);
            frame.setSize(480, 280);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
```

`ClientMain.java` 우클릭 → **Run As → Java Application**을 누릅니다. 창이 열리고 버튼을 눌렀을 때 **버튼 클릭 성공**으로 바뀌면 통과입니다. 예린님은 이 단계부터 화면을 만들고 버튼 동작을 확인할 수 있습니다.

## 3. MySQL 준비하고 Java에서 연결 확인하기

### 3-1. 로컬 데이터베이스와 앱 계정 준비

Windows에서 **Win + R → `services.msc`**로 서비스 목록을 열고, `MySQL80`처럼 MySQL 서버 이름이 붙은 서비스가 실행 중인지 확인합니다. 중지되어 있으면 시작합니다. Workbench의 로컬 연결에서 MySQL 관리자 계정으로 로그인한 다음, SQL 편집기에서 아래 내용을 실행합니다. `여기에_로컬_비밀번호`는 **실행 전에 내 로컬에서만 사용할 비밀번호로 바꿉니다**. 팀원이 만든 `cinema_app` 계정이 이미 있다면 자신에게 설정된 접속 정보를 확인해 사용합니다.

```sql
CREATE DATABASE IF NOT EXISTS cinema CHARACTER SET utf8mb4;
CREATE USER 'cinema_app'@'127.0.0.1' IDENTIFIED BY '여기에_로컬_비밀번호';
GRANT SELECT, INSERT, UPDATE, DELETE ON cinema.* TO 'cinema_app'@'127.0.0.1';
```

이 계정은 예매 데이터의 조회·등록·수정·삭제에 사용합니다. 테이블을 생성하거나 구조를 변경하는 SQL은 개발자가 별도로 실행합니다. [MySQL 8.0 계정 관리](https://dev.mysql.com/doc/refman/8.0/en/creating-accounts.html)

### 3-2. 연결을 열어 주는 클래스와 점검 클래스 만들기

| 위치 | 클래스 | 역할 |
| --- | --- | --- |
| `cinema.client` | `ClientMain` | Swing 앱의 시작점입니다. |
| `cinema.server.db` | `DbConnection` | JDBC URL·계정을 읽어 MySQL 연결을 엽니다. 나중에 조회·저장 코드에서 재사용합니다. |
| `cinema.server` | `DbCheck` | MySQL에 `SELECT 1`을 보내고 응답을 콘솔에 출력합니다. |

`src/main/java` 아래에 `cinema.server.db` 패키지로 **DbConnection.java**를 만듭니다. 접속 정보는 Eclipse 실행 설정의 환경 변수에서 읽습니다.

```java
package cinema.server.db;

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
```

`cinema.server` 패키지에 **DbCheck.java**를 만듭니다. `try (...)` 안에서 연결과 조회 결과를 열면, 조회가 끝나거나 오류가 발생했을 때 Java가 알아서 닫습니다.

```java
package cinema.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import cinema.server.db.DbConnection;

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
```

### 3-3. Eclipse 실행 설정에 접속 정보 지정

1. **Run → Run Configurations… → Java Application**에서 새 실행 설정을 만들고 **Name**을 `DbCheck`로 지정합니다.
2. **Common** 탭의 **Save as**는 **Local file**로 둡니다. **Main** 탭의 **Project**에 `cinema`, **Main class**에 `cinema.server.DbCheck`를 입력합니다.
3. **Environment** 탭에서 **New…**를 눌러 아래 변수 세 개를 등록한 후 **Apply → Run**을 누릅니다.

| 환경 변수 | 입력값 |
| --- | --- |
| `CINEMA_DB_URL` | `jdbc:mysql://127.0.0.1:3306/cinema?connectTimeout=5000&socketTimeout=5000` |
| `CINEMA_DB_USER` | `cinema_app` |
| `CINEMA_DB_PASSWORD` | 3-1에서 로컬 계정에 설정한 비밀번호 |

로컬 MySQL 포트가 다르면 URL의 `3306`을 바꿉니다. Eclipse 콘솔에 **MySQL 연결 성공 · SELECT 1 = 1**이 나오면 통과입니다. 비밀번호가 들어 있는 실행 설정은 로컬에 저장하고, 공유 `.launch` 파일이나 Git에 올리지 않습니다. [Connector/J JDBC URL 형식](https://dev.mysql.com/doc/connector-j/en/connector-j-reference-jdbc-url-format.html)

## 4. 에러가 나면 지금 단계에서 확인할 것

| 문제가 보이는 곳 | 먼저 확인할 것 |
| --- | --- |
| `release version 21 not supported` | Eclipse 프로젝트의 **Java Build Path**와 실행 설정의 **JRE**가 JDK 21을 사용하는지 확인합니다. |
| `FlatLightLaf cannot be resolved` 또는 `No suitable driver` | Maven 의존성이 내려받아졌는지 보고 **Maven → Update Project…**를 다시 실행합니다. JDBC URL이 `jdbc:mysql:`로 시작하는지도 확인합니다. |
| `Communications link failure` | MySQL 서버가 실행 중인지, Workbench에서 같은 호스트와 포트로 접속되는지 확인합니다. |
| `Access denied for user` | 실행 설정의 계정·비밀번호와 MySQL 계정의 접속 호스트를 확인합니다. |
| `Unknown database 'cinema'` | URL에 지정한 MySQL 인스턴스에 `cinema`를 만들었는지 확인합니다. |
| `환경 변수 …을 설정해 주세요` | 환경 변수를 넣은 `DbCheck` 실행 설정을 선택해 다시 실행합니다. |

## 5. 다음에 클래스와 소켓을 추가할 순서

클래스 이름은 화면이나 기능이 하나씩 생길 때 정하면 됩니다. 처음에는 **화면, 서버 요청, 데이터 조회 중 누가 무슨 일을 하는지**만 정해도 두 명이 동시에 시작할 수 있습니다.

| 순서 | 프론트 | 백엔드·서버 |
| --- | --- | --- |
| 기본 실행 | `ClientMain`에서 FlatLaf 창을 띄웁니다. 홈·영화·예매 메뉴를 붙일 때 `MainFrame`으로 공통 창을 분리합니다. [#47](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/47) | `DbCheck`로 MySQL에 접속한 뒤 `ServerMain`의 `ServerSocket`과 연결 클래스의 `Socket`으로 로컬 통신을 확인합니다. 먼저 한 줄 `PING` → `PONG`으로 연결을 확인합니다. [#48](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/48) |
| 첫 연동 | `MovieListPanel`에서 예제 응답으로 영화 제목을 먼저 표시합니다. 실제 서버를 연결할 때 `SwingWorker`에서 요청을 보내 화면이 멈추지 않게 합니다. [#49](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/49) | 영화 테이블에서 조회한 데이터를 `MovieDao`가 읽고, 서버가 합의한 형식으로 목록을 반환합니다. [#58](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/58) |
| 이후 기능 | 로그인·좌석·예매 내역 화면을 해당 이슈에서 추가합니다. | 회원이나 예매 데이터가 필요해질 때 모델, 조회·저장 클래스, 예매 규칙을 추가합니다. |

첫 소켓 요청은 `127.0.0.1`에서 실행합니다. `PING`과 `PONG`은 각각 줄바꿈까지 보내고 출력 버퍼를 비웁니다. 양쪽이 한 줄씩 읽으므로 줄바꿈이 없으면 응답을 계속 기다릴 수 있습니다. 그다음에는 영화 목록 요청에 대해 성공·빈 목록·오류 응답 예시를 하나씩 공유합니다. 그렇게 하면 팀원이 예제 응답으로 화면을 만들고, 서버가 준비됐을 때 실제 응답으로 바꿀 수 있습니다. [담당별 개발 순서](roadmap.md)

### 첫 설정 완료 기준

- [ ] Eclipse에서 `cinema` 프로젝트가 인식되고 두 라이브러리가 다운로드된다.
- [ ] `ClientMain`을 실행하면 Swing 창이 열리고 버튼을 누르면 문구가 바뀐다.
- [ ] `DbCheck`를 실행하면 MySQL 연결 성공 메시지를 확인할 수 있다.
- [ ] 팀원은 같은 `pom.xml`을 가져와 실행할 수 있고, 각자의 로컬 DB 비밀번호는 Git에 올라가지 않는다.
