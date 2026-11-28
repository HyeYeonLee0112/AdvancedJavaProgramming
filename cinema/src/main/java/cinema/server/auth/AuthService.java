package cinema.server.auth;

import cinema.dto.auth.AuthErrorCode;
import cinema.dto.auth.LoginRequest;
import cinema.dto.auth.LoginResult;
import cinema.dto.auth.SignUpRequest;
import cinema.dto.auth.SignUpResult;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Locale;
import java.util.OptionalLong;
import java.util.regex.Pattern;

// 회원 저장, 비밀번호 확인, 세션 발급을 연결해 인증 흐름을 처리한다.
public final class AuthService {
   
    private final MemberDao members = new MemberDao();
    private final PasswordHasher passwords = new PasswordHasher();
    private final SessionStore sessions = new SessionStore();
    
    // 이 프로젝트에서 허용하는 이메일 형식을 미리 컴파일한다.
    private static final Pattern EMAIL =
            Pattern.compile("[a-z0-9._%+-]+@[a-z0-9-]+(?:\\.[a-z0-9-]+)+");
    
    // 미등록 이메일로 로그인 시도 하는 경우
    // 응답 시간만 보고 이메일이 가입돼 있는지 짐작하기 어렵게 만들기 위해서 해시 안해도 되는데 함
    // 그걸 위한 salt
    private final byte[] absentMemberSalt = passwords.newSalt();


    // 가입 입력을 검사하고 비밀번호 해시를 저장한 뒤 새 회원 번호를 반환한다.
    public SignUpResult signUp(SignUpRequest request) {
        String email = normalizeEmail(request.email());
        // 표시 이름이 null이면 빈 문자열로 처리하고, 값이 있으면 앞뒤 공백을 제거한다.
        String name = "";
        if(request.displayName() != null) 
        	name = request.displayName().strip();
        
        String password = request.password();
        
        // DB를 호출하기 전에 서버에서 가입 입력 조건을 검사한다.
        if (!validEmail(email) || !validPassword(password) || name.isEmpty() || name.length() > 50) {
        	//INVALID_INPUT으로 실패 반환
            return new SignUpResult(false, null, AuthErrorCode.INVALID_INPUT);
        }

        try {
            byte[] salt = passwords.newSalt();
            byte[] hash = passwords.derive(password, salt, PasswordHasher.ITERATIONS);
            // 해시, 솔트, 반복 횟수를 함께 저장해 로그인할 때 같은 조건으로 계산할 수 있게 한다.
            long id = members.insert(email, name, salt, hash, PasswordHasher.ITERATIONS);
            return new SignUpResult(true, id, null);
        
            // DB 예외를 화면에서 처리할 오류 코드로 변환한다.
        } catch (SQLException error) {
            if (error.getErrorCode() == 1062) {
                return new SignUpResult(false, null, AuthErrorCode.EMAIL_ALREADY_EXISTS);
            }
            return new SignUpResult(false, null, AuthErrorCode.SERVER_ERROR);
        } catch (GeneralSecurityException error) {
            return new SignUpResult(false, null, AuthErrorCode.SERVER_ERROR);
        }
    }

    // 이메일로 회원을 조회하고 비밀번호가 일치하면 세션 토큰을 발급한다.
    public LoginResult login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        String password = request.password();
        if (!validEmail(email) || !validPassword(password)) {
            return loginFailure(AuthErrorCode.INVALID_INPUT);
        }

        try {
            // 조회 결과가 비어 있으면 null을 사용한다. DB 오류는 아래 catch에서 처리한다.
            var member = members.findByEmail(email).orElse(null);
            if (member == null) {
            	
            	//응답 시간만 보고 이메일이 가입돼 있는지 짐작하기 어렵게 만들기 위해서 해시 안해도 되는데 함
                passwords.derive(password, absentMemberSalt, PasswordHasher.ITERATIONS);
                return loginFailure(AuthErrorCode.INVALID_CREDENTIALS);
            }

            // 입력한 비밀번호를 가입 당시 저장한 솔트와 반복 횟수로 다시 계산한다.
            byte[] actual = passwords.derive(password, member.salt(), member.iterations());
            //MessageDigest: Java 표준 라이브러리에서 SHA-256 같은 해시를 계산하는 클래스
            //isEqual메서드: 이미 계산된 두 해시를 비교
            if (!MessageDigest.isEqual(member.hash(), actual)) {
                return loginFailure(AuthErrorCode.INVALID_CREDENTIALS);
            }

            // 계산한 해시가 저장된 해시와 일치한 회원에게 토큰을 발급
            String token = sessions.issue(member.id());
            return new LoginResult(true, member.id(), member.displayName(), token, null);
        // DB 작업과 해시 계산에서 발생한 예외를 서버 오류 응답으로 변환한다.
        } catch (SQLException | GeneralSecurityException error) {
            return loginFailure(AuthErrorCode.SERVER_ERROR);
        }
    }

    // 유효한 토큰에 연결된 회원 번호를 조회한다. 찾지 못하면 빈 결과를 반환한다.
    public OptionalLong findSessionMember(String token) {
        return sessions.findMember(token);
    }

    // 로그아웃한 회원의 토큰을 세션 저장소에서 제거한다.
    public void logout(String token) {
        sessions.revoke(token);
    }

    // 아래는 헬퍼함수들
    // 이메일 앞뒤 공백을 제거하고 조회와 저장에 사용할 소문자 형태로 맞춘다.
    private static String normalizeEmail(String email) {
        return email == null ? "" : email.strip().toLowerCase(Locale.ROOT);
    }

    // 이메일이 최대 254자이고 프로젝트에서 허용하는 형식인지 확인한다.
    private static boolean validEmail(String email) {
        return email.length() <= 254 && EMAIL.matcher(email).matches();
    }

    // 비밀번호에 공백 외 문자가 있는지 확인하고 12~128자 길이 제한을 적용한다.
    private static boolean validPassword(String password) {
        return password != null && !password.isBlank()
                && password.length() >= 12 && password.length() <= 128;
    }

    // 오류 코드를 담은 공통 로그인 실패 응답을 만든다.
    private static LoginResult loginFailure(AuthErrorCode code) {
        return new LoginResult(false, null, null, null, code);
    }

}