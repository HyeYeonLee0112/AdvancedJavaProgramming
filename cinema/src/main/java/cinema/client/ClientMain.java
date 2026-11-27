package cinema.client;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatLightLaf;

import cinema.client.auth.AuthPanel;
import cinema.client.auth.MockAuthClient;
import cinema.client.transport.AuthClient;

public class ClientMain {
    // Java 프로그램이 시작되면 Swing이 화면을 처리하는 곳에서 창을 엽니다.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientMain::showWindow);
    }

    // FlatLaf를 적용하고 앱을 열었을 때 보일 첫 화면을 정합니다.
    private static void showWindow() {
        if (!FlatLightLaf.setup()) {
            throw new IllegalStateException("FlatLaf 설정에 실패했습니다.");
        }

        JFrame frame = new JFrame("영화 예매");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 화면을 개발할 때는 테스트 응답을 연결합니다. 실제 서버 구현이 준비되면 이 줄에서 바꿉니다.
        AuthClient authClient = new MockAuthClient();

        // 앱의 다음 화면을 만들면 로그인 성공 알림을 띄우는 이 부분에서 연결합니다.
        AuthPanel authPanel = new AuthPanel(authClient, result ->
                JOptionPane.showMessageDialog(frame, result.displayName() + "님, 로그인됐어요."));
        frame.setContentPane(authPanel);
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
