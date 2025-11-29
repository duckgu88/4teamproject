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
 * 게스트 로그인 페이지를 위한 JDialog 클래스입니다.
 * 사용자에게 송장 번호를 입력받아 배송 정보를 조회하고 표시하는 기능을 제공합니다.
 * 모달 대화상자로 동작하여 호출한 부모 창의 상호작용을 일시적으로 막습니다.
 */
public class GuestLogin extends JDialog {

    private JTextField invoiceField; // 송장 번호 입력 필드
    private JButton searchButton; // 조회 버튼
    private JButton hintButton; // 테스트용 송장 번호 힌트 버튼
    private JTextArea resultArea; // 배송 조회 결과를 표시하는 텍스트 영역
    private JButton advanceDayButton; // 날짜를 하루 진행시키는 버튼
    
    /**
     * GuestLogin 대화상자의 생성자입니다.
     * UI 컴포넌트들을 초기화하고 레이아웃을 설정합니다.
     * @param parent 이 다이얼로그를 띄운 부모 JFrame (모달 동작을 위해 필요)
     */
    public GuestLogin(JFrame parent) { // 생성자에서 부모 JFrame을 받음
        super(parent, "게스트 로그인 - 배송 조회", true); // Modal JDialog로 설정 (부모 창을 블록함)
        
        // --- JDialog 기본 설정 ---
        setSize(500, 400); // 다이얼로그의 초기 크기 설정 (너비, 높이)
        setMinimumSize(new Dimension(500, 400)); // 다이얼로그의 최소 크기 설정
        setLocationRelativeTo(parent); // 다이얼로그를 부모 창의 중앙에 배치
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // 창 닫기 버튼 클릭 시 다이얼로그만 닫힘

        // --- 컨텐츠 패널 설정 ---
        Container contentPane = getContentPane(); // 다이얼로그의 컨텐츠 패널 가져오기
        contentPane.setBackground(UITheme.COLOR_BACKGROUND); // 배경색 설정
        contentPane.setLayout(new GridBagLayout()); // GridBagLayout으로 레이아웃 매니저 변경 (유연한 배치)

        GridBagConstraints gbc = new GridBagConstraints(); // GridBagLayout 제약 조건 객체

        // ================= 상단 입력 패널 (송장번호 입력 및 버튼들) =================
        JPanel masterTopPanel = new JPanel(new BorderLayout());
        masterTopPanel.setBackground(UITheme.COLOR_BACKGROUND);
        masterTopPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // 패딩 설정

        // 뒤로가기 버튼
        JButton backButton = UITheme.createGuestStyledButton("뒤로가기", new Dimension(80, 25));
        masterTopPanel.add(backButton, BorderLayout.WEST);

        JPanel inputPanel = new JPanel(); // 송장번호 입력 필드와 검색 버튼들을 담는 패널
        inputPanel.setBackground(UITheme.COLOR_BACKGROUND);
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0)); // 가운데 정렬, 가로 간격 15px

        JLabel instructionLabel = new JLabel("송장번호:");
        instructionLabel.setForeground(UITheme.COLOR_TEXT);
        inputPanel.add(instructionLabel);

        invoiceField = new JTextField(8); // 송장번호 입력 필드 (8칸 너비)
        inputPanel.add(invoiceField);

        searchButton = UITheme.createGuestStyledButton("조회", new Dimension(60, 25)); // 조회 버튼
        inputPanel.add(searchButton);

        hintButton = UITheme.createGuestStyledButton("?", new Dimension(25, 25)); // 힌트 버튼
        hintButton.setToolTipText("테스트용 송장번호 확인");
        inputPanel.add(hintButton);
        
        masterTopPanel.add(inputPanel, BorderLayout.CENTER); // 입력 패널을 상단 패널의 중앙에 배치

        // 날짜 하루 진행 버튼
        advanceDayButton = UITheme.createGuestStyledButton("하루 지남", new Dimension(80, 25));
        masterTopPanel.add(advanceDayButton, BorderLayout.EAST); // 날짜 진행 버튼을 상단 패널의 동쪽에 배치
        
        // GridBagLayout에 상단 패널 추가 (맨 위 행, 가로로 늘어남)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // 가로 방향으로 추가 공간을 모두 차지
        gbc.fill = GridBagConstraints.HORIZONTAL; // 가로로 늘어나게 함
        contentPane.add(masterTopPanel, gbc);

        // ================= 중앙 결과 패널 (블록 형태의 출력 영역) =================
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setPreferredSize(new Dimension(400, 300)); // 출력 패널의 선호 크기 설정
        outputPanel.setMinimumSize(new Dimension(400, 300)); // 출력 패널의 최소 크기 설정 (축소 방지)
        outputPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // 블록처럼 보이도록 테두리 설정

        resultArea = new JTextArea(); // 배송 조회 결과가 표시될 텍스트 영역
        resultArea.setEditable(false); // 수정 불가능하도록 설정
        resultArea.setBackground(UITheme.COLOR_TEXTAREA_BACKGROUND); // 배경색 설정
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // 모노스페이스 폰트 (정렬 보기 좋게)
        resultArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 내부 패딩 설정

        JScrollPane scrollPane = new JScrollPane(resultArea); // 텍스트 영역에 스크롤 기능 추가
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // 스크롤 패널의 기본 테두리 제거
        outputPanel.add(scrollPane, BorderLayout.CENTER); // 스크롤 패널을 출력 패널의 중앙에 배치

        // GridBagLayout에 중앙 결과 패널 추가 (두 번째 행, 중앙 정렬)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0; // 남은 세로 공간을 이 컴포넌트가 차지 (상단 패널을 위로 밀어 올림)
        gbc.fill = GridBagConstraints.NONE; // 컴포넌트 크기 고정 (선호 크기 유지)
        gbc.anchor = GridBagConstraints.CENTER; // 할당된 공간 내에서 중앙에 배치
        contentPane.add(outputPanel, gbc);
        
        // ================= 이벤트 리스너 설정 =================
        // 조회 버튼 액션: 송장 번호를 이용하여 배송 정보 조회 후 결과 표시
        searchButton.addActionListener(e -> {
            String invoiceNumber = invoiceField.getText();
            if (invoiceNumber == null || invoiceNumber.trim().isEmpty()) {
                resultArea.setText("송장번호를 입력해주세요.");
                return;
            }

            DeliveryOrder order = DeliverySystem.getInstance().findOrder(invoiceNumber);

            if (order != null) {
                resultArea.setText(formatOrderInfo(order)); // 조회된 정보 포맷팅 후 표시
            } else {
                resultArea.setText("해당 송장번호의 배송 정보가 없습니다.\n송장번호를 다시 확인해주세요.");
            }
        });

        // 힌트 버튼 액션: 테스트용 송장 번호 목록을 팝업으로 표시
        hintButton.addActionListener(e -> showCheatSheet());

        // 뒤로가기 버튼 액션: 현재 다이얼로그를 닫음 (부모 창인 로그인 화면으로 돌아감)
        backButton.addActionListener(e -> dispose()); 

        // 날짜 하루 진행 버튼 액션: 시스템 날짜를 하루 진행시키고 배송 상태 업데이트
        advanceDayButton.addActionListener(e -> {
            DeliverySystem.advanceDate(); // 날짜 하루 진행
            DeliverySystem.getInstance().updateDeliveryStatuses(); // 배송 상태 업데이트
            
            // 송장 번호가 입력되어 있으면 자동으로 재조회
            if (!invoiceField.getText().trim().isEmpty()) {
                searchButton.doClick(); 
            } else {
                resultArea.setText("날짜가 하루 지났습니다. 송장번호를 입력하여 다시 조회해주세요.");
            }
        });
    }
    
    /**
     * 테스트 목적으로 유효한 송장 번호 목록을 팝업 메시지로 표시합니다.
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
            if (count >= 5) break; // 최대 5개까지만 표시
        }
        sb.append("\n(위 번호 중 하나를 입력하세요)");

        JOptionPane.showMessageDialog(this, sb.toString(), "테스트 힌트", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * DeliveryOrder 객체의 정보를 보기 좋게 문자열로 포맷팅합니다.
     * @param order 포맷팅할 DeliveryOrder 객체
     * @return 포맷팅된 배송 정보 문자열
     */
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