package cinema.server.auth;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.OptionalLong;
import java.util.concurrent.ConcurrentHashMap;

//토큰을 키로 회원 번호와 만료 시각을 서버 메모리에 저장 
//로그인 후 30분 동안 유효, 서버 재시작 후에는 다시 로그인
//이후 예매 요청에서 서버가 findSessionMember()로 회원을 확인
//로그아웃 요청을 추가할 때 logout()을 연결합니다.
final class SessionStore {
    private static final Duration TTL = Duration.ofMinutes(30);
    private final SecureRandom random = new SecureRandom();
    
    //세션: 회원번호&만료시각
    private record Session(long memberId, Instant expiresAt) {}
    // 키는 세션 토큰, 값은 세션, 여러 서버 작업 스레드에서 공유합니다.
    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();



    String issue(long memberId) {
    	
        //1. 현재 시각과 같거나 그보다 오래된 만료 시각의 세션을 정리
    	// 현재 시각 추출
        Instant now = Instant.now();
        
	    // removeIf는 아래 조건에서 true를 반환한 항목을 삭제
        // entry는 토큰(키)과 세션(값)을 담은 항목입니다.
        sessions.entrySet().removeIf(entry -> {
            
            // getValue()로 세션 추출
            Session session = entry.getValue();

            // 세션에서 만료 시각 추출
            Instant expiresAt = session.expiresAt();

            // 만료 시각이 현재 시각보다 뒤라면 아직 유효합니다.
            boolean stillValid = expiresAt.isAfter(now);

            // 더 이상 유효하지 않으면 true를 반환해 이 항목을 삭제합니다.
            return !stillValid;
        });
        
        //2. 만료하지 않았으니, 로그인 토큰 반환
        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        // 무작위 바이트를 소켓으로 전달할 수 있는 문자열로 표현합니다.
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        // 로그인 성공 시 토큰과 회원의 대응 관계를 서버 메모리에 저장합니다.
        sessions.put(token, new Session(memberId, now.plus(TTL)));
        return token;
    }

    OptionalLong findMember(String token) {
        if (token == null || token.isBlank()) {
            return OptionalLong.empty();
        }
        
        // 받은 토큰으로 세션을 찾습니다. 없거나 만료되면 빈 결과를 돌려줍니다.
        Session session = sessions.get(token);
        if (session == null) {
            return OptionalLong.empty();
        }
        
        //조회하는 토큰의 세션 만료 시 삭제 
        if (!session.expiresAt().isAfter(Instant.now())) {

            sessions.remove(token, session);
            return OptionalLong.empty();
        }
        
        //만료하지 않은 정상적인 토큰을 전달받으면 회원id 반환
        return OptionalLong.of(session.memberId());
    }

    // 로그아웃시 해당 토큰을 삭제
    void revoke(String token) {
        if (token != null) {
            sessions.remove(token);
        }
    }
}