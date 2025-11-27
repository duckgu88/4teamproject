package guitool;

import javax.swing.*;
import main.DeliverySystem;
import main.DeliveryOrder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import guitool.UITheme;

/**
 * 게스트 로그인 페이지 (배송 조회 기능)
 */
public class GuestLogin extends JPanel {

    private MainFrame mainFrame; // MainFrame 참조
    private JTextField invoiceField;
    private JButton searchButton;
    private JButton hintButton; // [추가] 힌트 버튼
    private JTextArea resultArea;
    private JButton advanceDayButton;
    
    public GuestLogin(MainFrame mainFrame) { // 생성자에서 MainFrame을 받음
        this.mainFrame = mainFrame; // MainFrame 저장

        setBackground(UITheme.COLOR_BACKGROUND);
        setLayout(new BorderLayout(10, 10));

        JPanel masterTopPanel = new JPanel(new BorderLayout());
        masterTopPanel.setBackground(UITheme.COLOR_BACKGROUND);
        masterTopPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 뒤로가기 버튼
        JButton backButton = UITheme.createGuestStyledButton("뒤로가기", new Dimension(80, 25));
        masterTopPanel.add(backButton, BorderLayout.WEST);

        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(UITheme.COLOR_BACKGROUND);
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));

        JLabel instructionLabel = new JLabel("송장번호:");
        instructionLabel.setForeground(UITheme.COLOR_TEXT);
        inputPanel.add(instructionLabel);

        invoiceField = new JTextField(7);
        inputPanel.add(invoiceField);

        searchButton = UITheme.createGuestStyledButton("조회", new Dimension(50, 25));
        inputPanel.add(searchButton);

        hintButton = UITheme.createGuestStyledButton("?", new Dimension(25, 25));
        hintButton.setToolTipText("테스트용 송장번호 확인");
        inputPanel.add(hintButton);
        
        masterTopPanel.add(inputPanel, BorderLayout.CENTER);

        // 날짜 하루 지나게 하기 버튼
        advanceDayButton = UITheme.createGuestStyledButton("하루 지남", new Dimension(80, 25));
        masterTopPanel.add(advanceDayButton, BorderLayout.EAST);
        
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setBackground(UITheme.COLOR_TEXTAREA_BACKGROUND);
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(masterTopPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String invoiceNumber = invoiceField.getText();
                if (invoiceNumber == null || invoiceNumber.trim().isEmpty()) {
                    resultArea.setText("송장번호를 입력해주세요.");
                    return;
                }

                DeliveryOrder order = DeliverySystem.getInstance().findOrder(invoiceNumber);

                if (order != null) {
                    resultArea.setText(formatOrderInfo(order));
                } else {
                    resultArea.setText("해당 송장번호의 배송 정보가 없습니다.\n송장번호를 다시 확인해주세요.");
                }
            }
        });

        hintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCheatSheet();
            }
        });

        // 뒤로가기 버튼 이벤트
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showCard("LOGIN"); // 로그인 화면으로 전환
            }
        });

        // 날짜 하루 지나게 하기 버튼 이벤트
        advanceDayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeliverySystem.advanceDate();
                DeliverySystem.getInstance().updateDeliveryStatuses();
                
                if (!invoiceField.getText().trim().isEmpty()) {
                    searchButton.doClick();
                } else {
                    resultArea.setText("날짜가 하루 지났습니다. 송장번호를 입력하여 다시 조회해주세요.");
                }
            }
        });
    }
    
    private void showCheatSheet() {
        ArrayList<DeliveryOrder> allOrders = DeliverySystem.getInstance().Dlist;
        
        if (allOrders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "생성된 주문 데이터가 없습니다.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== [테스트용] 유효한 송장번호 목록 ===\n\n");
        
        int count = 0;
        for (DeliveryOrder order : allOrders) {
            sb.append("송장: ").append(order.getInvoiceNumber())
              .append("  (수령인: ").append(order.getReceiver().getName()).append(")\n");
            count++;
            if (count >= 5) break;
        }
        sb.append("\n(위 번호 중 하나를 입력하세요)");

        JOptionPane.showMessageDialog(this, sb.toString(), "테스트 힌트", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String formatOrderInfo(DeliveryOrder order) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- 배송 조회 결과 ---\n\n");
        sb.append("✅ 송장번호: ").append(order.getInvoiceNumber()).append("\n");
        sb.append("📦 상품명:   ").append(order.getSender().getItem()).append("\n");
        sb.append("👤 보내는 분: ").append(order.getSender().getName()).append("\n");
        sb.append("👤 받는 분:   ").append(order.getReceiver().getName()).append("\n");
        sb.append("🏠 배송 주소: ").append(order.getReceiver().getAddress()).append("\n");
        sb.append("🚚 배송 상태: ").append(order.getReceiver().getFormattedDeliveryStatus()).append("\n");
        
        String req = order.getReceiver().getRequest();
        if(req != null && !req.isEmpty()) {
             sb.append("📢 요청사항: ").append(req).append("\n");
        }
        
        return sb.toString();
    }
}