package main;

import java.util.*;
import java.io.*;
import mgr.*;
import search.SearchSystem;

public class DeliverySystem {
	Scanner scan = new Scanner(System.in);
	Manager<Sender> senderMgr = new Manager<>();
	static Manager<Receiver> receiverMgr = new Manager<>();
	ArrayList<DeliveryOrder> Dlist = new ArrayList<>();
	void mymain() {
		senderMgr.readAll("senders.txt", new SenderFactory());	
		receiverMgr.readAll("receivers.txt", new ReceiverFactory());
		SearchSystem searchSystem = new SearchSystem(senderMgr, receiverMgr, Dlist);
		
		createDeliveryOrder();
	
		while (true) {
            System.out.println("\n[메인 메뉴]");
            System.out.println("1. 전체 배송 주문 출력");
            System.out.println("2. 조회");
            System.out.println("종료하시려면 'end'를 입력하세요.");
            System.out.print(">> ");

            String choice = scan.next();
            scan.nextLine(); 

            if (choice.equals("1")) {
                printAllDeliveryOrder();
            } else if (choice.equals("2")) {
                searchSystem.searchMenu();
            } else if (choice.equalsIgnoreCase("end")) {
                System.out.println("프로그램을 종료합니다.");
                return;
            } else {
                System.out.println("잘못된 입력입니다. 다시 선택해주세요.");
            }
        }
	}
	
	Scanner openFile(String filename) {
		Scanner filein = null;
		try {
			filein = new Scanner(new File(filename));
		} catch (IOException e) {
			System.out.println("파일 입력 오류");
			System.exit(0);
		}
		return filein;
	}
	public static void main(String[] args) {
		DeliverySystem dm = new DeliverySystem();
		dm.mymain();
	}
	
	void createDeliveryOrder() {
		int size = senderMgr.getList().size();
		for(int i = 0; i < size; i++) {
			Sender s = senderMgr.getList().get(i);
			Receiver r = receiverMgr.getList().get(i);
			Dlist.add(new DeliveryOrder(s,r));
		}
	}
	void printAllDeliveryOrder() {
        for (DeliveryOrder d : Dlist)
            d.print();
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
}
