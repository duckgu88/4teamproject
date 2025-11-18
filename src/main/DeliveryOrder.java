package main;

import java.util.Random;

public class DeliveryOrder {
    // [수정] 필드를 private으로 변경
    private Sender sender;
    private Receiver receiver;
    private int invoiceNumber;

    public DeliveryOrder(Sender s, Receiver r) {
        this.sender = s;
        this.receiver = r;
        this.invoiceNumber = createNumber();
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