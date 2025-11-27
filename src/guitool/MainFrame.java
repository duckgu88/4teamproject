package guitool;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public MainFrame() {
        setTitle("Delivery Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // 페이지들을 패널로 생성하여 CardLayout에 추가
        LoginScreen loginScreen = new LoginScreen(this);
        ShippingPage shippingPage = new ShippingPage(this);
        GuestLogin guestLogin = new GuestLogin(this);
        InquiryPage inquiryPage = new InquiryPage(this);

        cardPanel.add(loginScreen, "LOGIN");
        cardPanel.add(shippingPage, "SHIPPING");
        cardPanel.add(guestLogin, "GUEST");
        cardPanel.add(inquiryPage, "INQUIRY");

        add(cardPanel);

        // 초기 화면 설정
        cardLayout.show(cardPanel, "LOGIN");

        setVisible(true);
    }

    public void showCard(String cardName) {
        cardLayout.show(cardPanel, cardName);
    }

    public static void main(String[] args) {
        // 데이터 로딩은 GUIApp에서 하므로 여기서는 바로 실행
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
