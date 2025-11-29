package guitool;

import guitool.Navigation;
import javax.swing.*;
import java.awt.*;

    // CardLayout을 사용하여 여러 JPanel 기반의 페이지들을 전환하며 관리하는 최상위 컨테이너
public class MainFrame extends JFrame implements Navigation {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // MainFrame의 생성자
    public MainFrame() {
        setTitle("Delivery Management System"); // 제목 설정
        setSize(1000, 700); // 크기 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫기 버튼 클릭 시 애플리케이션 종료
        setLocationRelativeTo(null); // 화면 중앙에 배치
        setMinimumSize(new Dimension(1300, 600)); // 최소 창 크기 설정

        cardLayout = new CardLayout(); // CardLayout 객체 생성
        cardPanel = new JPanel(cardLayout); // CardLayout을 사용할 패널 생성

        // --- 애플리케이션의 각 페이지(JPanel)들을 생성 ---
        LoginScreen loginScreen = new LoginScreen(this);
        ShippingPage shippingPage = new ShippingPage(this);
        InquiryPage inquiryPage = new InquiryPage(this);

        // --- 생성된 페이지 패널들을 CardLayout에 추가 ---
        cardPanel.add(loginScreen, "LOGIN");
        cardPanel.add(shippingPage, "SHIPPING");
        cardPanel.add(inquiryPage, "INQUIRY");

        add(cardPanel);

        cardLayout.show(cardPanel, "LOGIN"); // 애플리케이션 시작 시 "LOGIN" 페이지를 초기 화면으로 설정

        setVisible(true); // 프레임을 화면에 표시
    }

    @Override
    public void showPage(String pageName) {
        cardLayout.show(cardPanel, pageName);
    }

}
