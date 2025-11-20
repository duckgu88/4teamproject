package guitool;

import javax.swing.*;
import main.DeliverySystem;
import main.DeliveryOrder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * 게스트 로그인 페이지 (배송 조회 기능)
 */
public class GuestLogin extends JFrame {

    private JTextField invoiceField;
    private JButton searchButton;
    private JButton hintButton; // [추가] 힌트 버튼
    private JTextArea resultArea;
    
    private final Color backgroundColor = new Color(239, 222, 207);
    private final Color buttonColor = new Color(180, 160, 140);
    private final Color textColor = Color.BLACK;

    public GuestLogin() {
        setTitle("게스트 로그인 - 배송 조회");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setBackground(backgroundColor);
        contentPane.setLayout(new BorderLayout(10, 10));

        // --- 1. 상단 패널 ---
        JPanel topPanel = new JPanel();
        topPanel.setBackground(backgroundColor);
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));

        JLabel instructionLabel = new JLabel("송장번호:");
        instructionLabel.setForeground(textColor);
        topPanel.add(instructionLabel);

        invoiceField = new JTextField(10);
        topPanel.add(invoiceField);

        searchButton = new JButton("조회");
        searchButton.setBackground(buttonColor);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        topPanel.add(searchButton);

        // ★★★ [추가] 힌트 버튼 생성 (테스트용) ★★★
        hintButton = new JButton("?");
        hintButton.setBackground(new Color(100, 100, 100)); // 회색
        hintButton.setForeground(Color.WHITE);
        hintButton.setToolTipText("테스트용 송장번호 확인");
        hintButton.setFocusPainted(false);
        hintButton.setPreferredSize(new Dimension(45, 25)); 
        topPanel.add(hintButton);
        
        // --- 2. 중앙 패널 ---
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(250, 245, 235));
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        
        // --- 이벤트 리스너 ---
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

        // ★★★ [추가] 힌트 버튼 이벤트 (유효한 송장번호 보여주기) ★★★
        hintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCheatSheet();
            }
        });

        setVisible(true);
    }
    
    /**
     * [추가] 테스트를 위해 유효한 송장번호 목록을 팝업으로 보여줌
     */
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
        
        String req = order.getReceiver().getRequest();
        if(req != null && !req.isEmpty()) {
             sb.append("📢 요청사항: ").append(req).append("\n");
        }
        
        return sb.toString();
    }
}