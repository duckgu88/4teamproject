package main;

import java.util.Random;
import java.time.LocalDate;

public class DeliveryOrder {
    // [수정] 필드를 private으로 변경
    private Sender sender;
    private Receiver receiver;
    private int invoiceNumber;
    private LocalDate expectedArrival;
    
    public DeliveryOrder(Sender s, Receiver r) {
        this.sender = s;
        this.receiver = r;
        this.invoiceNumber = createNumber();
        if (!r.getStatus().equals("배송완료")) {
            expectedArrival = LocalDate.now().plusDays(r.getDeliveryDays());
        } else {
            expectedArrival = null;
        }

    }

    private int createNumber() {
        Random rand = new Random();
        return 100000 + rand.nextInt(900000); // 6자리 송장번호
    }

    public void print( ) {
        System.out.printf("송장번호: %d\n", invoiceNumber);
        sender.print();
        receiver.print();
        System.out.println();
    }
    public LocalDate getExpectedArrival() {
        return expectedArrival;
    }
    
    public String getDeliveryStatus() {
        return receiver.getStatus();
    }
    
    // [새 메소드] GUI에서 사용할 전체 주문 정보 문자열 반환
    public String getInfoString() {
        String senderInfo = (sender != null) ? sender.getInfoString() : "발신자 정보 없음";
        String receiverInfo = (receiver != null) ? receiver.getInfoString() : "수신자 정보 없음";
        String statusText = getDeliveryStatus();
        if (!statusText.equals("배송완료") && expectedArrival != null) {
            statusText += " (예상 도착일: " + expectedArrival + ")";
        }        
        return String.format(
        	    "--- 송장번호: %s ---\n" +
        	    "%s\n" +
        	    "%s\n" +
        	    "배송 상태: %s\n",
        	    invoiceNumber, senderInfo, receiverInfo, statusText
        	);
    }
    
    // --- ▼ [필수] search 및 guitool에서 사용할 Getter 메소드 추가 ▼ ---
    public Sender getSender() {
        return sender;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }
    // --- ▲ [필수] Getter 메소드 추가 ▲ ---
}