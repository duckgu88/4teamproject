package guitool;

import javax.swing.SwingUtilities;
import main.DeliverySystem; // main 패키지 임포트

public class GUIApp {

    public static void main(String[] args) {
        // 1. GUI 시작 전, DeliverySystem의 모든 데이터를 미리 로드
        DeliverySystem.getInstance().loadAllData();

        SwingUtilities.invokeLater(() -> {
            new MainFrame(); // MainFrame 인스턴스를 생성하여 GUI를 시작
        });
    }
}