package guitool;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import main.DeliveryOrder;

public class WaybillDialog extends JDialog {

    public WaybillDialog(JFrame parent, DeliveryOrder order) {
        super(parent, "운송장 상세 정보", true); // Modal(뒤에 창 클릭 불가)
        setSize(380, 550); // 영수증 비율에 맞게 조정
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // 전체 배경 패널 (흰색 종이 느낌)
        JPanel paperPanel = new JPanel();
        paperPanel.setBackground(Color.WHITE);
        paperPanel.setLayout(new BoxLayout(paperPanel, BoxLayout.Y_AXIS));
        // 바깥 여백과 검은 테두리로 종이 느낌 내기
        paperPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20), 
                new LineBorder(Color.BLACK, 2) 
        ));

        // --- 1. 헤더 (로고 및 제목) ---
        JLabel titleLabel = new JLabel("물류 운송장");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subTitle = new JLabel("Global Express Delivery");
        subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- 2. 바코드 (텍스트로 흉내) ---
        JLabel barcode = new JLabel("||| |||| || |||||| |||| ||");
        barcode.setFont(new Font("SansSerif", Font.PLAIN, 32));
        barcode.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel invoiceNum = new JLabel("No. " + order.getInvoiceNumber());
        invoiceNum.setFont(new Font("Monospaced", Font.BOLD, 18));
        invoiceNum.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- 3. 상세 정보 패널 ---
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 8, 8)); // 줄 간격 8
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // 정보 추가
        addInfoRow(infoPanel, "보내는 분", order.getSender().getName());
        addInfoRow(infoPanel, "연락처", order.getSender().getPhone());
        // 발송처는 Sender 클래스에 getCompany()가 있다고 가정 (없으면 주소 등으로 대체)
        addInfoRow(infoPanel, "발송처", order.getSender().getCompany()); 
        
        infoPanel.add(new JSeparator()); // 구분선
        
        addInfoRow(infoPanel, "받는 분", order.getReceiver().getName());
        addInfoRow(infoPanel, "연락처", order.getReceiver().getPhone());
        addInfoRow(infoPanel, "배송지", "<html>" + order.getReceiver().getAddress() + "</html>"); 
        
        infoPanel.add(new JSeparator()); // 구분선

        addInfoRow(infoPanel, "물품명", order.getSender().getItem());
        
        // 요청사항이 있을 때만 표시
        String req = order.getReceiver().getRequest();
        if (req != null && !req.isEmpty()) {
            addInfoRow(infoPanel, "요청사항", "<html><font color='red'>" + req + "</font></html>");
        } else {
             addInfoRow(infoPanel, "요청사항", "없음");
        }

        // --- 컴포넌트 조립 ---
        paperPanel.add(Box.createVerticalStrut(10));
        paperPanel.add(titleLabel);
        paperPanel.add(subTitle);
        paperPanel.add(Box.createVerticalStrut(20));
        paperPanel.add(barcode);
        paperPanel.add(invoiceNum);
        paperPanel.add(Box.createVerticalStrut(10));
        paperPanel.add(infoPanel);
        paperPanel.add(Box.createVerticalGlue()); // 남은 공간 채우기

        // --- 4. 하단 닫기 버튼 ---
        JButton closeButton = new JButton("닫기");
        closeButton.setBackground(new Color(225, 205, 188));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(239, 222, 207)); // 배경색과 맞춤
        bottomPanel.add(closeButton);

        add(paperPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addInfoRow(JPanel panel, String title, String value) {
        JLabel l = new JLabel("<html><b>" + title + ":</b> " + (value==null?"-":value) + "</html>");
        l.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(l);
    }
}