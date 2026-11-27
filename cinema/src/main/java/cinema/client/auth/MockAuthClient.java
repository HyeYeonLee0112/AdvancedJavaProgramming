package cinema.client.auth;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import cinema.client.transport.AuthClient;
import cinema.dto.auth.AuthErrorCode;
import cinema.dto.auth.LoginRequest;
import cinema.dto.auth.LoginResult;
import cinema.dto.auth.SignUpRequest;
import cinema.dto.auth.SignUpResult;

/**
 * 화면을 개발할 때 쓸 테스트 응답입니다. 계정은 앱을 켠 동안에만 메모리에 둡니다.
 */
public final class MockAuthClient implements AuthClient {
    private final Map<String, DemoMember> members = new HashMap<>();
    private long nextMemberId = 2;

    // 화면을 켜자마자 로그인을 시험할 수 있도록 예시 계정을 넣습니다.
    public MockAuthClient() {
        members.put("demo@example.test", new DemoMember(1L, "시연 회원", "cinema123"));
    }

    // 입력값과 일치하는 테스트 계정이 있으면 화면에서 쓸 회원 정보를 돌려줍니다.
    @Override
    public synchronized LoginResult login(LoginRequest request) {
        DemoMember member = members.get(normalizeEmail(request.email()));
        if (member == null || !member.password().equals(request.password())) {
            return new LoginResult(false, null, null, null, AuthErrorCode.INVALID_CREDENTIALS);
        }
        return new LoginResult(true, member.id(), member.displayName(), "mock-session-" + member.id(), null);
    }

    // 같은 이메일이 있으면 중복을 알려 주고, 새 이메일이면 이 실행에서 로그인할 수 있게 저장합니다.
    @Override
    public synchronized SignUpResult signUp(SignUpRequest request) {
        String email = normalizeEmail(request.email());
        if (members.containsKey(email)) {
            return new SignUpResult(false, null, AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        long id = nextMemberId++;
        members.put(email, new DemoMember(id, request.displayName(), request.password()));
        return new SignUpResult(true, id, null);
    }

    // 가입할 때와 로그인할 때 이메일 주소를 같은 방식으로 비교합니다.
    private String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    private record DemoMember(long id, String displayName, String password) {
    }
}
