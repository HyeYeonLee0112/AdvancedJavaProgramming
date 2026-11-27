package cinema.client.transport;

import cinema.dto.auth.LoginRequest;
import cinema.dto.auth.LoginResult;
import cinema.dto.auth.SignUpRequest;
import cinema.dto.auth.SignUpResult;

/**
 * Swing 화면에서 회원가입과 로그인을 호출할 때 사용하는 인터페이스입니다.
 * 화면에서는 서버 응답을 기다리는 동안 입력과 그림이 멈추지 않도록
 * {@link javax.swing.SwingWorker}에서 호출합니다.
 */
public interface AuthClient {
    /**
     * 이메일과 비밀번호로 로그인합니다.
     *
     * @param request 이메일과 비밀번호를 담은 로그인 요청
     * @return 성공하면 회원 정보와 세션 토큰을, 실패하면 오류 코드를 담은 결과
     */
    LoginResult login(LoginRequest request);

    /**
     * 이메일, 비밀번호, 표시 이름으로 회원 계정을 만듭니다.
     *
     * @param request 새 계정 정보를 담은 회원가입 요청
     * @return 성공하면 생성된 회원 번호를, 실패하면 오류 코드를 담은 결과
     */
    SignUpResult signUp(SignUpRequest request);
}
