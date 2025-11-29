package guitool;

import main.DeliveryOrder;
import main.DeliverySystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 게스트 로그인 - 배송 조회 화면
 * (송장번호 / 받는 사람 / 배달기사 / 운송회사 / 요청사항 / 배송 상태)
 */
public class GuestLogin extends JFrame {

    private final JFrame parentFrame;

    // 색상
    private static final Color COLOR_BACKGROUND = new Color(239, 222, 207);
    private static final Color COLOR_BUTTON = new Color(225, 205, 188);
    private static final Color COLOR_BUTTON_DARK = new Color(200, 180, 160);
    private static final Color COLOR_TABLE_HEADER = new Color(225, 205, 188);
    private static final Color COLOR_TABLE_BG = new Color(250, 245, 235);

    // 폰트 (ShippingPage와 비슷하게)
    private static final java.awt.Font FONT_BUTTON = new java.awt.Font("맑은 고딕", java.awt.Font.PLAIN, 13);
    private static final java.awt.Font FONT_LABEL  = new java.awt.Font("맑은 고딕", java.awt.Font.PLAIN, 12);

    // 테이블 + 날짜 라벨
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel dateLabel;

    // 마지막 검색 상태 기억용
    private enum SearchMode { NONE, INVOICE, NAME }
    private SearchMode lastMode = SearchMode.NONE;
    private String lastKeyword = null;

    // 파일 기반 매핑
    // ex) "서울특별시" -> "김도윤"
    private final Map<String, String> regionToDriver = new HashMap<>();
    // ex) "강태빈" -> "한진"
    private final Map<String, String> senderToCompany = new HashMap<>();

    public GuestLogin(JFrame parent) {
        this.parentFrame = parent;

        setTitle("게스트 로그인 - 배송 조회");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        getContentPane().setBackground(COLOR_BACKGROUND);
        getContentPane().setLayout(new BorderLayout(5, 5));

        // 데이터 매핑 먼저 로드
        loadDriverMap();
        loadSenderCompanyMap();

        initNorth();
        initCenter();
        initSouth();

        // 시작할 때는 아무 데이터도 안 보이게
        loadTable(new ArrayList<DeliveryOrder>());

        setVisible(true);
    }

    // =====================================
    // 상단 영역 (뒤로가기 + 검색 카테고리)
    // =====================================
    private void initNorth() {
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(COLOR_BACKGROUND);
        northPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 뒤로가기 버튼
        JButton backButton = new JButton("뒤로가기");
        backButton.setBackground(COLOR_BUTTON_DARK);
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setFont(FONT_BUTTON);
        backButton.setPreferredSize(new Dimension(110, 32));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (parentFrame != null) parentFrame.setVisible(true);
                dispose();
            }
        });
        northPanel.add(backButton, BorderLayout.WEST);

        // 검색 카테고리 버튼들
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        categoryPanel.setBackground(COLOR_BACKGROUND);

        JButton btnSearchByInvoice = new JButton("송장번호로 검색");
        styleCategoryButton(btnSearchByInvoice);
        btnSearchByInvoice.setPreferredSize(new Dimension(160, 32)); // 글자 다 보이게
        btnSearchByInvoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSearchByInvoice();
            }
        });

        JButton btnSearchByName = new JButton("이름으로 검색");
        styleCategoryButton(btnSearchByName);
        btnSearchByName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSearchByName();
            }
        });

        categoryPanel.add(btnSearchByInvoice);
        categoryPanel.add(btnSearchByName);

        northPanel.add(categoryPanel, BorderLayout.CENTER);

        getContentPane().add(northPanel, BorderLayout.NORTH);
    }

    private void styleCategoryButton(JButton btn) {
        btn.setBackground(COLOR_BUTTON);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(FONT_BUTTON);
        btn.setPreferredSize(new Dimension(130, 32));
    }

    // =====================================
    // 가운데 리스트 영역 (JTable)
    // =====================================
    private void initCenter() {
        // 컬럼: 송장번호, 받는 사람, 배달기사, 운송회사, 요청사항, 배송 상태
        String[] columns = {
                "송장번호",
                "받는 사람",
                "배달기사",
                "운송회사",
                "요청사항",
                "배송 상태"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 게스트는 수정 불가
            }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setBackground(COLOR_TABLE_BG);
        table.getTableHeader().setBackground(COLOR_TABLE_HEADER);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(COLOR_TABLE_BG);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    // =====================================
    // 하단 영역 (현재 날짜 + 날짜 갱신)
    // =====================================
    private void initSouth() {
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        southPanel.setBackground(COLOR_BACKGROUND);
        southPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        dateLabel = new JLabel();
        dateLabel.setForeground(Color.BLACK);
        dateLabel.setFont(FONT_LABEL);
        updateDateLabel();

        JButton btnUpdateDate = new JButton("날짜 갱신");
        btnUpdateDate.setBackground(COLOR_BUTTON);
        btnUpdateDate.setForeground(Color.BLACK);
        btnUpdateDate.setFocusPainted(false);
        btnUpdateDate.setFont(FONT_BUTTON);
        btnUpdateDate.setPreferredSize(new Dimension(100, 28));
        btnUpdateDate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onUpdateDate();
            }
        });

        southPanel.add(dateLabel);
        southPanel.add(btnUpdateDate);

        getContentPane().add(southPanel, BorderLayout.SOUTH);
    }

    private void updateDateLabel() {
        // DeliverySystem에 getCurrentDateString()이 없으면 아래 한 줄을
        // String dateStr = DeliverySystem.getCurrentDate().toString();
        // 로 바꿔 써도 됨.
        String dateStr = DeliverySystem.getCurrentDate().toString();
        dateLabel.setText("현재 날짜: " + dateStr);
    }

    // =====================================
    // 파일 → 매핑 로딩
    // =====================================
    // drivers.txt : "서울특별시 김도윤" 형식
    private void loadDriverMap() {
        regionToDriver.clear();
        try (Scanner sc = new Scanner(new File("drivers.txt"), "UTF-8")) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    String region = parts[0];   // 시/도
                    String driver = parts[1];   // 기사 이름
                    regionToDriver.put(region, driver);
                }
            }
        } catch (Exception e) {
            System.out.println("drivers.txt 로부터 기사 정보를 읽는 중 오류: " + e.getMessage());
        }
    }

    // senders.txt : "강태빈 010-... 경기도 수원시 한진 15kg 생필품" 형식
    private void loadSenderCompanyMap() {
        senderToCompany.clear();
        try (Scanner sc = new Scanner(new File("senders.txt"), "UTF-8")) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                // 0:이름 1:전화 2:경기도 3:수원시 4:한진 ...
                if (parts.length >= 5) {
                    String name = parts[0];
                    String company = parts[4];  // 운송회사
                    senderToCompany.put(name, company);
                }
            }
        } catch (Exception e) {
            System.out.println("senders.txt 로부터 운송회사 정보를 읽는 중 오류: " + e.getMessage());
        }
    }

    // =====================================
    // 검색 이벤트 (송장 / 이름)
    // =====================================
    private void onSearchByInvoice() {
        String keyword = JOptionPane.showInputDialog(
                this,
                "조회할 송장번호를 입력하세요.",
                "송장번호로 검색",
                JOptionPane.PLAIN_MESSAGE
        );
        if (keyword == null) return;

        keyword = keyword.trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "송장번호를 입력해주세요.");
            return;
        }

        int keyNum;
        try {
            keyNum = Integer.parseInt(keyword);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "송장번호는 숫자만 입력해주세요.");
            return;
        }

        ArrayList<DeliveryOrder> all = DeliverySystem.getInstance().Dlist;
        List<DeliveryOrder> filtered = new ArrayList<>();

        for (DeliveryOrder order : all) {
            if (order.getInvoiceNumber() == keyNum) {
                filtered.add(order);
            }
        }

        if (filtered.isEmpty()) {
            JOptionPane.showMessageDialog(this, "해당 송장번호의 배송 데이터가 없습니다.");
        }

        lastMode = SearchMode.INVOICE;
        lastKeyword = keyword;

        loadTable(filtered);
    }

    private void onSearchByName() {
        String keyword = JOptionPane.showInputDialog(
                this,
                "조회할 받는 사람 이름을 입력하세요.",
                "이름으로 검색",
                JOptionPane.PLAIN_MESSAGE
        );
        if (keyword == null) return;

        keyword = keyword.trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "이름을 입력해주세요.");
            return;
        }

        ArrayList<DeliveryOrder> all = DeliverySystem.getInstance().Dlist;
        List<DeliveryOrder> filtered = new ArrayList<>();

        for (DeliveryOrder order : all) {
            if (order.getReceiver() != null &&
                    order.getReceiver().getName() != null &&
                    order.getReceiver().getName().contains(keyword)) {
                filtered.add(order);
            }
        }

        if (filtered.isEmpty()) {
            JOptionPane.showMessageDialog(this, "해당 이름의 배송 데이터가 없습니다.");
        }

        lastMode = SearchMode.NAME;
        lastKeyword = keyword;

        loadTable(filtered);
    }

    // =====================================
    // 날짜 갱신
    // =====================================
    private void onUpdateDate() {
        DeliverySystem.advanceDate();
        DeliverySystem.getInstance().updateDeliveryStatuses();
        updateDateLabel();

        List<DeliveryOrder> listToShow = new ArrayList<>();

        if (lastMode == SearchMode.INVOICE && lastKeyword != null) {
            try {
                int keyNum = Integer.parseInt(lastKeyword);
                for (DeliveryOrder order : DeliverySystem.getInstance().Dlist) {
                    if (order.getInvoiceNumber() == keyNum) {
                        listToShow.add(order);
                    }
                }
            } catch (NumberFormatException ignored) {}
        } else if (lastMode == SearchMode.NAME && lastKeyword != null) {
            for (DeliveryOrder order : DeliverySystem.getInstance().Dlist) {
                if (order.getReceiver() != null &&
                        order.getReceiver().getName() != null &&
                        order.getReceiver().getName().contains(lastKeyword)) {
                    listToShow.add(order);
                }
            }
        }

        loadTable(listToShow);

        String msg = "현재 날짜: " + DeliverySystem.getCurrentDate().toString()
                + "\n배송 상태가 업데이트되었습니다.";
        JOptionPane.showMessageDialog(
                this,
                msg,
                "메시지",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================
    // 테이블 채우기
    // =====================================
    private void loadTable(List<DeliveryOrder> list) {
        tableModel.setRowCount(0);
        if (list == null) return;

        for (DeliveryOrder order : list) {
            String invoice = String.valueOf(order.getInvoiceNumber());

            String receiverName = "";
            String address = "";
            String request = "";
            String status = "";

            if (order.getReceiver() != null) {
                receiverName = order.getReceiver().getName();
                address = order.getReceiver().getAddress();
                request = order.getReceiver().getRequest();
                status = order.getReceiver().getFormattedDeliveryStatus();
            }

            // 배달기사: 주소의 첫 단어(시/도) → drivers.txt 매핑
            String driverName = "";
            if (address != null && !address.isEmpty()) {
                String[] addrParts = address.split("\\s+");
                if (addrParts.length > 0) {
                    String regionKey = addrParts[0]; // 예: 인천광역시, 부산광역시...
                    String driver = regionToDriver.get(regionKey);
                    if (driver != null) driverName = driver;
                }
            }

            // 운송회사: 보내는 사람 이름 → senders.txt 매핑
            String companyName = "";
            if (order.getSender() != null) {
                String senderName = order.getSender().getName();
                String comp = senderToCompany.get(senderName);
                if (comp != null) companyName = comp;
            }

            tableModel.addRow(new Object[]{
                    invoice,
                    receiverName,
                    driverName,
                    companyName,
                    request,
                    status
            });
        }
    }
}