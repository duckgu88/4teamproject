package guitool;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import main.DeliveryOrder;
import main.DeliverySystem;

public class ShippingPage extends JFrame {

    // --- [디자인] 사용할 색상 정의 ---
    private static final Color COLOR_BACKGROUND = new Color(239, 222, 207);
    private static final Color COLOR_BUTTON = new Color(225, 205, 188);
    private static final Color COLOR_BUTTON_HOVER = new Color(218, 184, 153);
    private static final Color COLOR_BUTTON_ACTIVE = new Color(200, 170, 140);
    private static final Color COLOR_TABLE_HEADER = new Color(218, 184, 153);
    private static final Color COLOR_TEXT = new Color(77, 77, 77);
    private static final Color COLOR_GRID = new Color(220, 200, 185);
    private static final Color COLOR_ROW_ALT = new Color(247, 241, 235);

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private DefaultTableModel tableModel;
    private JTable table;
    private DefaultTableModel summaryTableModel;
    private JTable summaryTable;
    private JLabel mapLabel;
    private JSplitPane splitPane;

    private List<DeliveryOrder> originalList;
    private JFrame parentFrame;
    private String currentActiveCard = "TABLE";

    private JButton btnShowAll;
    private JButton btnSortSender;
    private JButton btnSortReceiver;
    private JButton btnGroupByRegion;
    private JButton btnFilterRequests;
    private JButton backButton; // 뒤로가기 버튼
    private JButton btnFilterCompleted;
    private JButton btnFilterInProgress;
    private JButton btnFilterPending;
    private JButton advanceDayButton;

    // 날짜 표시용 라벨 (배송 상태 순 버튼 자리를 대체)
    private JLabel dateLabel;

    private JButton activeButton = null;

    // --- [신규] 지역별 색상 매핑 ---
    private static final Map<String, Color> REGION_COLOR_MAP = new TreeMap<>();
    static {
        REGION_COLOR_MAP.put("경기도", new Color(253, 226, 147)); // 노란색
        REGION_COLOR_MAP.put("서울특별시", new Color(255, 158, 102)); // 주황색
        REGION_COLOR_MAP.put("강원도", new Color(248, 182, 83)); // 황토색
        REGION_COLOR_MAP.put("충청남도", new Color(133, 117, 215)); // 파란-보라색
        REGION_COLOR_MAP.put("충청북도", new Color(100, 196, 185)); // 청록색
        REGION_COLOR_MAP.put("전라북도", new Color(246, 140, 93)); // 주황색
        REGION_COLOR_MAP.put("경상북도", new Color(111, 189, 112)); // 초록색
        REGION_COLOR_MAP.put("대구광역시", new Color(118, 172, 215)); // 하늘색
        REGION_COLOR_MAP.put("전라남도", new Color(59, 168, 153)); // 진한 청록색
        REGION_COLOR_MAP.put("경상남도", new Color(170, 112, 180)); // 자주색
        REGION_COLOR_MAP.put("제주특별자치도", new Color(237, 85, 94)); // 빨간색
        REGION_COLOR_MAP.put("대구광역시", new Color(118, 172, 215));
        REGION_COLOR_MAP.put("광주광역시", new Color(118, 172, 215));
        REGION_COLOR_MAP.put("울산광역시", new Color(118, 172, 215));
        REGION_COLOR_MAP.put("부산광역시", new Color(192, 137, 201));
        REGION_COLOR_MAP.put("인천광역시", new Color(253, 226, 147));
    }

    public ShippingPage(JFrame parent) { // 생성자에서 이전 창을 받음
        this.parentFrame = parent; // 이전 창 저장
        setTitle("배송 관리");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(COLOR_BACKGROUND);
        originalList = DeliverySystem.getInstance().Dlist;

        setupUI();
        addListeners();

        setActiveButton(btnShowAll);
        refreshTable(new ArrayList<>(originalList));

        cardLayout.show(cardPanel, "TABLE");

        setVisible(true);
    }

    private void setupUI() {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(COLOR_BACKGROUND);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        JPanel leftButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtonsPanel.setBackground(COLOR_BACKGROUND);

        backButton = createStyledButton("뒤로가기");
        btnShowAll = createStyledButton("전체 보기");
        btnSortSender = createStyledButton("보내는 사람 순");
        btnSortReceiver = createStyledButton("받는 사람 순");  // ← 텍스트 변경
        btnGroupByRegion = createStyledButton("지역 별 요약");
        btnFilterRequests = createStyledButton("요청사항 보기");
        btnFilterCompleted = createStyledButton("배송 완료");
        btnFilterInProgress = createStyledButton("배송 중");
        btnFilterPending = createStyledButton("배송 전");

        leftButtonsPanel.add(backButton);
        leftButtonsPanel.add(btnShowAll);
        leftButtonsPanel.add(btnSortSender);
        leftButtonsPanel.add(btnSortReceiver);
        leftButtonsPanel.add(btnGroupByRegion);
        leftButtonsPanel.add(btnFilterRequests);
        leftButtonsPanel.add(btnFilterCompleted);
        leftButtonsPanel.add(btnFilterInProgress);
        leftButtonsPanel.add(btnFilterPending);

        // 배송 상태 순 버튼 대신 날짜 라벨 추가
        dateLabel = new JLabel("현재 날짜: " + DeliverySystem.getCurrentDate().toString());
        dateLabel.setForeground(COLOR_TEXT);
        dateLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        leftButtonsPanel.add(dateLabel);

        buttonPanel.add(leftButtonsPanel, BorderLayout.WEST); // 왼쪽 버튼 패널을 서쪽에 추가

        advanceDayButton = createStyledButton("날짜 갱신");  // ← 텍스트 변경
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtonPanel.setBackground(COLOR_BACKGROUND);
        rightButtonPanel.add(advanceDayButton);
        buttonPanel.add(rightButtonPanel, BorderLayout.EAST);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(COLOR_BACKGROUND);

        JScrollPane tableScrollPane = createTablePanel();
        cardPanel.add(tableScrollPane, "TABLE");

        splitPane = createSummarySplitPane();
        cardPanel.add(splitPane, "SUMMARY");

        add(buttonPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = { "송장번호", "보내는 사람", "연락처", "물품", "받는 사람", "주소", "요청사항", "배송 상태" };
        tableModel = new DefaultTableModel(columnNames, 0);

        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? COLOR_BACKGROUND : COLOR_ROW_ALT);
                    c.setForeground(COLOR_TEXT);
                } else {
                    c.setBackground(COLOR_BUTTON_HOVER);
                    c.setForeground(COLOR_TEXT);
                }
                return c;
            }
        };

        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setForeground(COLOR_TEXT);
        table.setGridColor(COLOR_GRID);
        table.setRowHeight(24);
        table.setSelectionBackground(COLOR_BUTTON_HOVER);
        table.setSelectionForeground(COLOR_TEXT);
        table.setBackground(COLOR_BACKGROUND);

        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_TABLE_HEADER);
        header.setForeground(COLOR_TEXT);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(100, 30));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(COLOR_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        scrollPane.setBackground(COLOR_BACKGROUND);

        return scrollPane;
    }

    // --- [수정] 지역별 요약 SplitPane 생성 ---
    private JSplitPane createSummarySplitPane() {
        mapLabel = new JLabel();
        try {
            // 1. 이미지 경로 설정
            ImageIcon mapIcon = new ImageIcon(getClass().getResource("/image/korea.png"));

            if (mapIcon.getImage() == null) {
                throw new Exception("이미지 리소스를 찾을 수 없습니다.");
            }

            // 높이 기준으로 크기 조절
            Image image = mapIcon.getImage().getScaledInstance(-1, 500, Image.SCALE_SMOOTH);
            mapLabel.setIcon(new ImageIcon(image));

        } catch (Exception e) {
            mapLabel.setText("지도 로드 실패: /guitool/image/korea.png");
            e.printStackTrace();
        }
        mapLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mapLabel.setBackground(COLOR_BACKGROUND);
        mapLabel.setOpaque(true);

        // 테이블 생성
        String[] summaryColumnNames = { "지역", "이름", "색상" };
        summaryTableModel = new DefaultTableModel(summaryColumnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 2) return Color.class;
                return super.getColumnClass(column);
            }
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        summaryTable = new JTable(summaryTableModel);
        setupSummaryTable(summaryTable);

        TableColumn colorColumn = summaryTable.getColumnModel().getColumn(2);
        colorColumn.setCellRenderer(new ColorSquareRenderer());
        colorColumn.setMaxWidth(60);

        JScrollPane summaryTableScrollPane = new JScrollPane(summaryTable);
        summaryTableScrollPane.getViewport().setBackground(COLOR_BACKGROUND);
        summaryTableScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapLabel, summaryTableScrollPane);

        split.setDividerLocation(400);
        split.setResizeWeight(0.5);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setBackground(COLOR_BACKGROUND);

        return split;
    }

    private void setupSummaryTable(JTable table) {
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setForeground(COLOR_TEXT);
        table.setGridColor(COLOR_GRID);
        table.setRowHeight(24);
        table.setSelectionBackground(COLOR_BUTTON_HOVER);
        table.setSelectionForeground(COLOR_TEXT);
        table.setBackground(COLOR_BACKGROUND);

        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_TABLE_HEADER);
        header.setForeground(COLOR_TEXT);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(100, 30));

        table.setDefaultRenderer(Color.class, new ColorSquareRenderer());
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(COLOR_BUTTON);
        button.setForeground(COLOR_TEXT);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(COLOR_BUTTON_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (button == activeButton) {
                    button.setBackground(COLOR_BUTTON_ACTIVE);
                } else {
                    button.setBackground(COLOR_BUTTON);
                }
            }
        });
        return button;
    }

    private void refreshTable(List<DeliveryOrder> listToShow) {
        tableModel.setRowCount(0);
        for (DeliveryOrder order : listToShow) {
            Object[] rowData = {
                    order.getInvoiceNumber(),
                    order.getSender().getName(),
                    order.getSender().getPhone(),
                    order.getSender().getItem(),
                    order.getReceiver().getName(),
                    order.getReceiver().getAddress(),
                    order.getReceiver().getRequest(),
                    order.getReceiver().getFormattedDeliveryStatus() // 배송 상태
            };
            tableModel.addRow(rowData);
        }
    }

    private void updateRegionSummaryTable() {
        summaryTableModel.setRowCount(0);

        Map<String, List<String>> regionMap = originalList.stream()
                .collect(Collectors.groupingBy(
                        order -> getRegionFromAddress(order.getReceiver().getAddress()),
                        () -> new TreeMap<>(),
                        Collectors.mapping(
                                order -> order.getReceiver().getName(),
                                Collectors.toList())));

        for (Map.Entry<String, List<String>> entry : regionMap.entrySet()) {
            String region = entry.getKey();
            List<String> names = entry.getValue();

            String uniqueNames = names.stream()
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(", "));

            Color regionColor = REGION_COLOR_MAP.getOrDefault(region, Color.LIGHT_GRAY);
            summaryTableModel.addRow(new Object[] { region, uniqueNames, regionColor });
        }
    }

    private void setActiveButton(JButton clickedButton) {
        if (activeButton != null) {
            activeButton.setBackground(COLOR_BUTTON);
        }
        clickedButton.setBackground(COLOR_BUTTON_ACTIVE);
        this.activeButton = clickedButton;
    }

    private String getRegionFromAddress(String address) {
        if (address == null || address.trim().isEmpty()) return "기타";
        String[] parts = address.split(" ");
        if (parts.length > 0) return parts[0];
        return "기타";
    }

    private void addListeners() {
        btnShowAll.addActionListener(e -> {
            setActiveButton(btnShowAll);
            refreshTable(new ArrayList<>(originalList));
            cardLayout.show(cardPanel, "TABLE");
            currentActiveCard = "TABLE";
        });
        btnSortSender.addActionListener(e -> {
            setActiveButton(btnSortSender);
            List<DeliveryOrder> listToShow = new ArrayList<>(originalList);
            listToShow.sort(Comparator.comparing(o -> o.getSender().getName()));
            refreshTable(listToShow);
            cardLayout.show(cardPanel, "TABLE");
            currentActiveCard = "TABLE";
        });
        btnSortReceiver.addActionListener(e -> {
            setActiveButton(btnSortReceiver);
            List<DeliveryOrder> listToShow = new ArrayList<>(originalList);
            listToShow.sort(Comparator.comparing(o -> o.getReceiver().getName()));
            refreshTable(listToShow);
            cardLayout.show(cardPanel, "TABLE");
            currentActiveCard = "TABLE";
        });
        btnFilterRequests.addActionListener(e -> {
            setActiveButton(btnFilterRequests);
            List<DeliveryOrder> listToShow = originalList.stream()
                    .filter(order -> {
                        String request = order.getReceiver().getRequest();
                        return request != null && !request.trim().isEmpty();
                    })
                    .collect(Collectors.toList());
            refreshTable(listToShow);
            cardLayout.show(cardPanel, "TABLE");
            currentActiveCard = "TABLE";
        });
        btnGroupByRegion.addActionListener(e -> {
            setActiveButton(btnGroupByRegion);
            updateRegionSummaryTable();
            cardLayout.show(cardPanel, "SUMMARY");
            currentActiveCard = "SUMMARY";
        });

        // 뒤로가기 버튼 리스너
        backButton.addActionListener(e -> {
            parentFrame.setVisible(true);
            dispose();
        });

        // 배송 상태 필터링 리스너
        btnFilterCompleted.addActionListener(e -> {
            setActiveButton(btnFilterCompleted);
            List<DeliveryOrder> listToShow = originalList.stream()
                    .filter(order -> "배송완료".equals(order.getReceiver().getStatus()))
                    .collect(Collectors.toList());
            refreshTable(listToShow);
            cardLayout.show(cardPanel, "TABLE");
            currentActiveCard = "TABLE";
        });

        btnFilterInProgress.addActionListener(e -> {
            setActiveButton(btnFilterInProgress);
            List<DeliveryOrder> listToShow = originalList.stream()
                    .filter(order -> "배송중".equals(order.getReceiver().getStatus()))
                    .collect(Collectors.toList());
            refreshTable(listToShow);
            cardLayout.show(cardPanel, "TABLE");
            currentActiveCard = "TABLE";
        });

        btnFilterPending.addActionListener(e -> {
            setActiveButton(btnFilterPending);
            List<DeliveryOrder> listToShow = originalList.stream()
                    .filter(order -> "배송전".equals(order.getReceiver().getStatus()))
                    .collect(Collectors.toList());
            refreshTable(listToShow);
            cardLayout.show(cardPanel, "TABLE");
            currentActiveCard = "TABLE";
        });

        // 날짜 갱신 버튼 이벤트 리스너
        advanceDayButton.addActionListener(e -> {
            DeliverySystem.advanceDate();
            DeliverySystem.getInstance().updateDeliveryStatuses();

            if (activeButton == btnShowAll) {
                refreshTable(new ArrayList<>(originalList));
            } else if (activeButton == btnSortSender) {
                List<DeliveryOrder> listToShow = new ArrayList<>(originalList);
                listToShow.sort(Comparator.comparing(o -> o.getSender().getName()));
                refreshTable(listToShow);
            } else if (activeButton == btnSortReceiver) {
                List<DeliveryOrder> listToShow = new ArrayList<>(originalList);
                listToShow.sort(Comparator.comparing(o -> o.getReceiver().getName()));
                refreshTable(listToShow);
            } else if (activeButton == btnFilterRequests) {
                List<DeliveryOrder> listToShow = originalList.stream()
                        .filter(order -> {
                            String request = order.getReceiver().getRequest();
                            return request != null && !request.trim().isEmpty();
                        })
                        .collect(Collectors.toList());
                refreshTable(listToShow);
            } else if (activeButton == btnFilterCompleted) {
                List<DeliveryOrder> listToShow = originalList.stream()
                        .filter(order -> "배송완료".equals(order.getReceiver().getStatus()))
                        .collect(Collectors.toList());
                refreshTable(listToShow);
            } else if (activeButton == btnFilterInProgress) {
                List<DeliveryOrder> listToShow = originalList.stream()
                        .filter(order -> "배송중".equals(order.getReceiver().getStatus()))
                        .collect(Collectors.toList());
                refreshTable(listToShow);
            } else if (activeButton == btnFilterPending) {
                List<DeliveryOrder> listToShow = originalList.stream()
                        .filter(order -> "배송전".equals(order.getReceiver().getStatus()))
                        .collect(Collectors.toList());
                refreshTable(listToShow);
            } else { // Default to show all if no active filter/sort (btnGroupByRegion 포함)
                refreshTable(new ArrayList<>(originalList));
            }

            // 지역 요약 테이블 갱신
            updateRegionSummaryTable();
            // 상단 날짜 라벨도 갱신
            dateLabel.setText("현재 날짜: " + DeliverySystem.getCurrentDate().toString());

            JOptionPane.showMessageDialog(this,
                    "현재 날짜: " + DeliverySystem.getCurrentDate().toString() +
                    "\n배송 상태가 업데이트되었습니다.");
        });
    }

    private int getDeliveryStatusOrder(String status) {
        return switch (status) {
            case "배송중" -> 1;
            case "배송전" -> 2;
            case "배송완료" -> 3;
            default -> 99; // 기타 알 수 없는 상태
        };
    }

    class ColorSquareRenderer extends JPanel implements TableCellRenderer {
        public ColorSquareRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (value instanceof Color) setBackground((Color) value);
            else setBackground(Color.WHITE);

            if (isSelected) setBorder(BorderFactory.createLineBorder(table.getSelectionBackground().darker()));
            else setBorder(null);
            return this;
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }
}
