package cinema.dto.auth;

/**
 * 회원가입·로그인 실패 시 화면에 전달하는 오류 코드입니다.
 */
public enum AuthErrorCode {
    /** 이미 가입된 이메일로 회원가입을 요청했습니다. */
    EMAIL_ALREADY_EXISTS,

    /** 이메일과 비밀번호로 계정을 확인할 수 없습니다. */
    INVALID_CREDENTIALS,

    /** 서버에 연결하지 못했거나 응답을 받기 전에 연결이 끊겼습니다. */
    NETWORK_ERROR,

    /** 정해진 시간 안에 서버 응답을 받지 못했습니다. */
    REQUEST_TIMEOUT,

    /** 서버가 정한 입력 조건을 만족하지 않습니다. */
    INVALID_INPUT,

    /** 서버나 데이터베이스 오류로 처리할 수 없습니다. */
    SERVER_ERROR
}
