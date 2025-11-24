package main;

import java.io.File;
import java.util.*;
import mgr.*;
import java.time.LocalDate; // Import LocalDate

public class DeliverySystem {
    // 1. 유일한 인스턴스를 저장할 static 변수
    private static DeliverySystem instance;

    private static LocalDate currentDate = LocalDate.now(); 

    Scanner scan = new Scanner(System.in);
    Manager<Sender> senderMgr = new Manager<>();
    static Manager<Receiver> receiverMgr = new Manager<>(); // ReceiverFactory 대신 ReceiverManager 인스턴스 사용
    ArrayList<RegionGroup> regionGroups = new ArrayList<>();
    
    // 2. Dlist를 외부에서 접근할 수 있도록 public으로 변경
    public ArrayList<DeliveryOrder> Dlist = new ArrayList<>(); 
    
    // 3. RegionManager도 외부에서 접근할 수 있도록 멤버 변수로 이동
    public RegionManager rm = new RegionManager(); 

    // 4. private 생성자로 변경 (외부에서 new DeliverySystem() 방지)
    private DeliverySystem() {}

    // 5. 유일한 인스턴스를 가져오는 static 메소드
    public static DeliverySystem getInstance() {
        if (instance == null) {
            instance = new DeliverySystem();
        }
        return instance;
    }

    public static LocalDate getCurrentDate() {
        return currentDate;
    }

    public static void advanceDate() {
        currentDate = currentDate.plusDays(1);
        System.out.println("현재 날짜가 " + currentDate + "로 변경되었습니다.");
    }

    public void updateDeliveryStatuses() {
        LocalDate today = getCurrentDate();
        for (DeliveryOrder order : Dlist) {
            Receiver receiver = order.getReceiver();
            
            // 1. 배송 완료된 주문은 건너뜀
            if ("배송완료".equals(receiver.getStatus())) {
                continue;
            }

            // 2. 남은 배송일수를 하루 감소시킴
            receiver.decrementDeliveryDays(); 

            // 3. 상태 전환 로직
            String currentStatus = receiver.getStatus();
            int daysLeft = receiver.getDeliveryDays(); // 업데이트된 남은 일수

            if ("배송전".equals(currentStatus)) {
                // "배송전" 상태는 "배송중"으로 변경
                receiver.setStatus("배송중"); 
            }
            
            // "배송중" 또는 방금 "배송중"으로 바뀐 상태에서 남은 일수가 0 이하면 "배송완료" 처리
            // (getStatus()는 변경될 수 있으므로 다시 확인)
            if ( ("배송중".equals(receiver.getStatus()) || "배송전".equals(receiver.getStatus())) && daysLeft <= 0) {
                 receiver.setStatus("배송완료");
            }
        }
        System.out.println("배송 상태 업데이트 완료 (기준 날짜: " + today + ")");
    }

    // 6. mymain() -> loadAllData()로 이름 변경 (데이터 로딩 역할)
    public void loadAllData() { 
        senderMgr.readAll("senders.txt", new SenderFactory());
        receiverMgr.readAll("receivers.txt", new ReceiverFactory());

        createDeliveryOrder(); // Dlist 생성

        // RegionManager 로직 수행
        rm.groupRegion(Dlist);
        rm.assignDrivers("drivers.txt");

        ArrayList<String> requests = readRequests();
        addRequests(requests, rm);
        System.out.println("=== 모든 주문 정보 ===");
        for (DeliveryOrder order : Dlist) {
            System.out.println(order.getInfoString());
        }
        System.out.println("데이터 로딩 완료."); // 로딩 확인용
    }

    /**
     * 7. [수정된 메소드] 송장 번호로 주문을 찾는 기능
     */
    public DeliveryOrder findOrder(String invoiceStr) {
        try {
            int invoiceNumber = Integer.parseInt(invoiceStr.trim());
            for (DeliveryOrder order : Dlist) {
                // [수정] 'order.invoiceNumber' 대신 'order.getInvoiceNumber()' Getter 사용
                if (order.getInvoiceNumber() == invoiceNumber) {
                    return order; // 찾았다!
                }
            }
        } catch (NumberFormatException e) {
            return null; // 숫자가 아닌 문자가 입력됨
        }
        return null; // 못 찾음
    }

    // --- (이하는 기존 코드) ---
    
    ArrayList<String> readRequests() {
        ArrayList<String> requests = new ArrayList<>();
        try (Scanner scan = new Scanner(new File("requests.txt"))) {
            while(scan.hasNextLine()) {
                requests.add(scan.nextLine());
            }
        } catch (Exception e) {
            System.out.println("requests.txt 읽기 실패");
        }
        return requests;
    }
    
    /**
     * [수정된 메소드] 요청사항을 추가하는 기능
     */
    private void addRequests(ArrayList<String> requests, RegionManager rm) {
        Random random = new Random();
        for(RegionGroup group : rm.rList) {
            // [권장] 'group.orders' 대신 'group.getOrders()' Getter 사용
            for (DeliveryOrder order : group.getOrders()) { 
                if(!requests.isEmpty()) {
                    // [수정] 'order.receiver.request = ...' 대신 Setter 사용
                    order.getReceiver().setRequest(requests.get(random.nextInt(requests.size())));
                }
            }
        }
    }

    void createDeliveryOrder() {
        int size = senderMgr.getList().size();
        for(int i = 0; i < size; i++) {
            Sender s = senderMgr.getList().get(i);
            Receiver r = receiverMgr.getList().get(i);
            Dlist.add(new DeliveryOrder(s,r));
        }
    }
    
    public class SenderFactory implements Factory {
        public Manageable create() {
            return new Sender();
        }
    }

    public class ReceiverFactory implements Factory {
        public Manageable create() {
            return new Receiver();
        }
    }

    public static String getCurrentDateString() {
        return currentDate.toString();   // yyyy-MM-dd 형태로 반환됨
    }
}