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
