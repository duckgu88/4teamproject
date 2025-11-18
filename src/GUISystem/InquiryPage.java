package GUISystem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import main.DeliveryOrder;
import main.DeliverySystem;
import search.*;   // SenderSearcher, InvoiceSearcher, Matcher, Searchable 등

public class InquiryPage extends JFrame {
    private JTable resultTable;      // 검색된 데이터를 보여줄 테이블
    private String currentCategory;    // 지금 어떤 카테고리로 조회 중인지 기억
    private DefaultTableModel tableModel;

    public InquiryPage() {
        setTitle("배송 조회");
        setSize(850, 600); // 배송 GUI랑 비슷하게
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. 카테고리 버튼 패널
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] categories = {
                "보내는 사람", "받는 사람", "송장번호",
                "지역", "전화번호", "운송회사명", "물품명"
        };

        for (String cat : categories) {
            JButton btn = new JButton(cat);
            btn.addActionListener(e -> {
                currentCategory = cat;               // 지금 선택된 카테고리 기억
                openSubInquiryWindow(cat);          // 서브 조회창 띄우기
            });
            categoryPanel.add(btn);
        }

        // 2. 결과 표시 영역 (JTable로 출력)
        String[] columnNames = {"송장번호", "보내는 사람", "연락처", "물품", "받는 사람", "주소", "요청사항"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);

        // 3. 메인 화면으로 돌아가는 버튼
        JButton backButton = new JButton("메인 화면으로 돌아가기");
        backButton.addActionListener(e -> {
            // MainMenu를 새로 띄우고 현재 InquiryPage 창은 닫기
            new MainMenu();
            dispose(); // InquiryPage 창 닫기
        });

        // 레이아웃에 추가
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);  // '메인 화면으로 돌아가기' 버튼을 하단에 추가

        add(categoryPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);  // 버튼을 하단에 배치

        setVisible(true);
    }

    private void openSubInquiryWindow(String category) {
        SubInquiryPage subPanel = new SubInquiryPage(category);

        // ★ 여기서 SearchButtonListener를 붙임
        subPanel.setSearchButtonListener(
                new SearchButtonListener(category, subPanel)
        );

        JFrame subFrame = new JFrame("조회 - " + category);
        subFrame.setSize(400, 150);
        subFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        subFrame.setLocationRelativeTo(this);
        subFrame.add(subPanel);
        subFrame.setVisible(true);
    }

    // 검색 결과를 이 화면에 뿌리는 메소드 (JTable 사용)
    // 검색 결과를 이 화면에 뿌리는 메소드 (JTable 사용)
public void showSearchResult(ArrayList<DeliveryOrder> foundList) {
    // 테이블 초기화
    tableModel.setRowCount(0);  // 기존 데이터 삭제

    if (foundList == null || foundList.isEmpty()) {
        JOptionPane.showMessageDialog(this, "해당 조건에 맞는 배송 정보가 없습니다.");
        return;
    }

    // 검색된 데이터를 테이블에 추가
    for (DeliveryOrder order : foundList) {
        // 송장번호, 보내는 사람, 받는 사람, 물품명, 지역, 전화번호, 운송회사명 등 각 항목을 적절히 매핑
        Object[] row = new Object[]{
            order.getInvoiceNumber(),   // 송장번호
            order.getSender().getName(), // 보내는 사람
            order.getReceiver().getPhone(), // 연락처
            order.getSender().getItem(),       // 물품 (배송 정보)
            order.getReceiver().getName(), // 받는 사람
            order.getReceiver().getAddress(), // 주소
            order.getReceiver().getRequest()   // 요청사항 (필요시 추가)
        };
        tableModel.addRow(row);
    }
}


    // ==========================
    //  내부 클래스: SearchButtonListener
    // ==========================
    private class SearchButtonListener implements java.awt.event.ActionListener {
        private String category;
        private SubInquiryPage subPanel;

        public SearchButtonListener(String category, SubInquiryPage subPanel) {
            this.category = category;
            this.subPanel = subPanel;
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            String keyword = subPanel.getInputText();
            if (keyword == null || keyword.trim().isEmpty()) {
                JOptionPane.showMessageDialog(subPanel, "검색어를 입력하세요.");
                return;
            }

            // 1. 카테고리에 맞는 Searchable 구현체 선택
            Searchable searcher = null;
            switch (category) {
                case "보내는 사람":
                    searcher = new SenderSearcher();
                    break;
                case "받는 사람":
                    searcher = new ReceiverSearcher();
                    break;
                case "송장번호":
                    searcher = new InvoiceSearcher();
                    break;
                case "지역":
                    searcher = new RegionSearcher();
                    break;
                case "전화번호":
                    searcher = new PhoneSearcher();
                    break;
                case "운송회사명":
                    searcher = new CompanySearcher();
                    break;
                case "물품명":
                    searcher = new ItemSearcher();
                    break;
                default:
                    JOptionPane.showMessageDialog(subPanel, "알 수 없는 카테고리입니다: " + category);
                    return;
            }

            // 2. DeliverySystem에서 전체 주문 리스트 가져오기
            ArrayList<DeliveryOrder> allList = DeliverySystem.getInstance().Dlist; 
            // (ShippingPage에서 쓰던 그 Dlist 그대로)

            // 3. Matcher를 이용해서 keyword와 일치하는 주문들 찾기
            ArrayList<DeliveryOrder> foundList =
                    Matcher.findMatches(searcher, allList, keyword);

            // 4. InquiryPage 화면에 결과 뿌리기
            showSearchResult(foundList);

            // 5. 서브창은 닫아도 되고, 안 닫고 놔둬도 됨
            SwingUtilities.getWindowAncestor(subPanel).dispose();
        }
    }
}
