# #33 · Maven·JavaFX 실행 기반

빈 app에서 창과 버튼 하나를 실행한다. 수업 실습 프로젝트의 설정은 건드리지 않는다.

상태: 학습·구현 안내서. 실행 설정과 최소 예제를 제시하며 앱을 설치하거나 생성한 결과 보고가 아니다.
연결: [#33](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/33) · [전체 계획](issue-32-roadmap.md) · [문서 목록](../포스트잇보드_따라구현하기.md)
선행: #32의 환경 선택 확인
PRD 연결: 기술 결정 · L-05

## 1. 시작 상태와 목표

작성 시점 app에는 Eclipse 설정과 빈 src/bin만 있고 pom.xml·JavaFX 코드는 없다. Eclipse는 이미 설치된 환경을 기준으로 한다. Maven은 라이브러리와 빌드 규칙을 관리하는 도구다. JavaFX는 창·버튼을 만드는 화면 라이브러리이며 JDK만으로는 포함되지 않는다.

```text
저장소: C:\DevHub\06_SWU\3-1\AdvancedJavaProgramming
코드 위치: 위 저장소의 app
Eclipse workspace: C:\DevHub\06_SWU\3-1\eclipse-workspace
JDK: C:\Users\이혜연\.jdks\temurin-21
```

workspace는 Eclipse의 작업 목록·설정 장소다. 실제 코드를 workspace로 복사하지 않는다. 기존 README·docs·수업 실습은 유지한다.

## 2. 파일별 책임과 이번 선택

| 파일·폴더 | 역할 |
|---|---|
| app/pom.xml | Java 버전·JavaFX 의존성·실행 시작 클래스 |
| app/src/main/java/board/client/BoardApplication.java | 창과 버튼의 최소 실행 진입점 |
| app/src/main/resources | 이후 화면/설정 자원; 당장 FXML을 만들지 않음 |
| app/src/test/java | 이후 계산·검사 테스트 |
| app/mvnw.cmd·mvnw·.mvn/wrapper | 같은 빌드 도구를 재현할 실행기 |

- 지금: JDK 21·UTF-8·JavaFX controls·Java 코드로 화면 구성.
- 짧게 이해: pom은 실행 코드가 아니라 Maven 설정이다.
- 나중: FXML·CSS·JUnit·DB 드라이버·통신 라이브러리는 해당 이슈에서 추가.
- 제외: module-info.java·FlatLaf·Swing 코드 혼합.

JavaFX 21.0.12는 아래 최소 실습의 예시 버전이다. PRD의 최종 버전 미정을 이 문서만으로 확정하지 않는다. 사용한 버전을 실제 실행 결과와 함께 #33에 기록한다. JavaFX와 1인 프로젝트 허용 여부는 교수자 확인 항목이다.

## 3. 따라 구현하기

### 01-1. Java 버전과 Eclipse 프로젝트 확인

PowerShell에서 다음을 실행한다.

```powershell
& 'C:\Users\이혜연\.jdks\temurin-21\bin\java.exe' --version
& 'C:\Users\이혜연\.jdks\temurin-21\bin\javac.exe' --version
```

둘 다 21이 나와야 한다. Eclipse `Window → Preferences → Java → Installed JREs`에서 해당 JDK를 등록하고 프로젝트의 JRE도 21로 선택한다.

`app`이 목록에 없으면 `File → Import → General → Existing Projects into Workspace`에서 `AdvancedJavaProgramming\app`을 선택한다. `Copy projects into workspace`는 선택하지 않는다. 실제 코드 위치를 그대로 사용하기 위해서다.

프로젝트 우클릭 `Properties → Resource`의 인코딩은 UTF-8로 맞춘다.

### 01-2. Maven 폴더와 설정 만들기

Maven은 라이브러리를 내려받고, 정해진 폴더의 코드를 빌드하는 도구다. JavaFX는 JDK에 포함되지 않으므로 Maven으로 가져온다. Eclipse 설치에 포함된 Maven 실행 기능을 사용하면 당장 별도 `mvn` 설치는 필요 없다.

Eclipse에서 `app` 아래에 다음 파일·폴더를 직접 만든다.

```text
app/
  pom.xml
  src/
    main/
      java/
        board/
          client/
            BoardApplication.java
      resources/
    test/
      java/
```

`pom.xml`은 프로젝트에 필요한 라이브러리·빌드 규칙 목록이다. 아래는 첫 실행을 위한 설정 예제다. JavaFX 21.0.12와 실행 플러그인 0.0.8의 배포 여부를 공식 Maven 저장소에서 확인했다. 최종 버전 선택은 #33에서 기록한다.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>swu</groupId>
  <artifactId>sticky-board</artifactId>
  <version>0.1.0-SNAPSHOT</version>
  <properties>
    <maven.compiler.release>21</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <javafx.version>21.0.12</javafx.version>
  </properties>
  <dependencies>
    <dependency>
      <groupId>org.openjfx</groupId>
      <artifactId>javafx-controls</artifactId>
      <version>${javafx.version}</version>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.13.0</version>
      </plugin>
      <plugin>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-maven-plugin</artifactId>
        <version>0.0.8</version>
        <configuration>
          <mainClass>board.client.BoardApplication</mainClass>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

`release=21`은 Java 21 규칙으로 컴파일한다는 뜻이다. `javafx-controls`는 버튼·라벨 등 화면 부품을 가져오며 필요한 JavaFX 기본 라이브러리도 함께 연결한다. `mainClass`는 실행 시작 클래스의 패키지 포함 이름이다.

프로젝트 우클릭 `Configure → Convert to Maven Project`를 실행한다. 이후 `Maven → Update Project…`를 눌러 반영한다. 이미 Maven 프로젝트라면 Convert 대신 Update만 한다. Source 목록에서 `src/main/java`가 소스 폴더로 잡혔는지 확인한다. 코드 파일은 기존 `src` 바로 아래에 만들지 않는다.

이 안내서는 `module-info.java` 없이 시작한다. FlatLaf는 Swing용 테마이므로 이 JavaFX 프로젝트에는 등록하지 않는다.

### 01-3. 버튼 하나로 실행 흐름 확인

`src/main/java`를 우클릭해 `New → Package → board.client`를 만들고, 그 안에 `BoardApplication` 클래스를 만든다. 파일명은 `BoardApplication.java`다.

먼저 “창을 띄우고 버튼을 클릭하면 글자가 바뀐다”는 의도를 아래처럼 읽는다.

```text
프로그램 시작
  JavaFX가 창 준비
  라벨과 버튼 생성
  버튼 클릭 시 라벨 문구 변경
  화면에 두 부품 배치
  창 표시
```

최소 실행 예제:

```java
package board.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BoardApplication extends Application {
    @Override
    public void start(Stage stage) {
        Label status = new Label("아직 클릭하지 않았어요");
        Button button = new Button("확인");
        button.setOnAction(event -> status.setText("클릭 성공"));
        VBox root = new VBox(12, status, button);
        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("포스트잇 보드 — 실행 확인");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

`Application`은 JavaFX 앱의 기본 클래스다. `launch`가 JavaFX를 시작하면 `start(Stage)`가 호출된다. `Stage`는 창, `Scene`은 창 안의 화면, `VBox`는 부품을 세로로 놓는 배치 도구다. `setOnAction`은 Swing의 `addActionListener`처럼 클릭 후 실행할 동작을 연결한다.

### 01-4. Eclipse에서 실행

프로젝트 우클릭 `Run As → Maven build…`에서 다음을 확인한다.

1. Base directory: `app` 폴더.
2. Goals: `clean compile`.
3. JRE 탭: Temurin JDK 21.
4. Run을 눌러 `BUILD SUCCESS`를 확인한다.
5. Goals를 `javafx:run`으로 바꿔 실행한다. 첫 실행은 인터넷 연결이 필요하다.

앱이 열린 동안 실행 과정이 살아 있는 것은 정상이다. 창을 닫아 종료한다. 초반에는 Maven 실행을 기준으로 삼는다.

다른 PC에서도 같은 명령을 쓰려면 #33의 마지막 작업으로 Maven Wrapper를 만든다. Eclipse Maven Build의 Goals에 아래를 입력할 수 있다.

```text
org.apache.maven.plugins:maven-wrapper-plugin:3.3.4:wrapper -Dtype=only-script
```

생성된 `mvnw`, `mvnw.cmd`, `.mvn/wrapper/` 설정을 확인하고 버전·생성 파일을 기록한다. 이후 PowerShell에서는 `app` 안에서 다음을 사용한다.

```powershell
.\mvnw.cmd clean compile
.\mvnw.cmd javafx:run
```

Wrapper도 최초 실행에 인터넷과 JDK가 필요하다.

마지막으로 원본과 구분되는 새 테스트 폴더에 pom.xml, src, mvnw, mvnw.cmd, .mvn을 복사한다. bin·target·Eclipse workspace의 .metadata는 실행 준비물로 복사하지 않는다. 새 폴더 안에서 JDK 21을 사용해 Wrapper의 clean compile과 javafx:run을 다시 확인한다. 원본 app과 수업 실습 폴더는 옮기거나 지우지 않는다. 새 폴더에서 라이브러리를 다시 내려받을 수 있으므로 첫 실행의 인터넷 요구도 기록한다.

### 01-5. 완료 기준과 오류 해석

- [ ] 21로 컴파일되고 버튼 클릭 시 라벨이 바뀐다.
- [ ] 창 종료 후 실행이 종료된다.
- [ ] 빌드·실행 방법과 선택 버전을 기록했다.
- [ ] Wrapper로 같은 빌드를 재현했다.

| 보이는 오류 | 먼저 확인할 것 |
|---|---|
| `package javafx... does not exist` | pom.xml 위치·Maven Update·Maven Dependencies 확인 |
| `release version 21 not supported` | Maven 실행 설정의 JRE가 JDK 21인지 확인 |
| `Could not find ... main class` | 패키지·파일 위치와 mainClass가 일치하는지 확인 |
| `JavaFX runtime components are missing` | Maven의 javafx:run으로 실행했는지 확인 |
| 라이브러리 다운로드 실패 | 네트워크·프록시와 요청한 버전을 확인 |
| public class 파일명 오류 | BoardApplication과 파일명의 대소문자 일치 확인 |

오늘 회고: “main 다음에 누가 start를 호출하는가?”, “버튼 코드는 클릭할 때 실행되는가, 화면 생성 때 실행되는가?”를 자기 말로 설명한다.

## 4. 실제 결과와 회고

```text
java/javac 버전:
Eclipse Maven 실행 JRE:
선택한 JavaFX·플러그인·Maven Wrapper 버전:
clean compile 결과:
javafx:run 버튼/종료 결과:
새 테스트 폴더의 Wrapper 빌드 결과:
첫 오류·원인·수정:
미확인 사항:
```

리뷰할 때 pom.xml, 시작 클래스, 실행 설정의 JRE와 첫 오류를 가져온다. Maven 오류는 요약 한 줄만 가져오기보다 원인 구간을 함께 확인한다. 개인 정보가 있는 경로는 가린다.

다음: [#34 로컬 텍스트 포스트잇 CRUD](issue-34-local-notes.md). 창이 실행되기 전에는 카드 기능을 추가하지 않는다.

## 공식 자료

- [OpenJFX — Eclipse와 Maven 시작 안내](https://openjfx.io/openjfx-docs/)
- [Maven 컴파일 버전 설정](https://maven.apache.org/plugins/maven-compiler-plugin/examples/set-compiler-release.html)
- [Maven Wrapper](https://maven.apache.org/wrapper/)
