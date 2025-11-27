package guitool;

import javax.swing.SwingUtilities;
import main.DeliverySystem; // <-- main 패키지 임포트

public class GUIApp {
    public static void main(String[] args) {
        // 1. GUI 시작 전, 데이터를 미리 로드합니다.
        DeliverySystem.getInstance().loadAllData();

        // 2. EDT(Event Dispatch Thread)에서 GUI를 안전하게 실행
        SwingUtilities.invokeLater(() -> {
            new MainFrame(); // 메인 프레임부터 시작
        });
    }
}