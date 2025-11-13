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
		
		createDeliveryOrder();
		printAllDeliveryOrder();

		// 정보 조회를 위한 탐색 시스템 시작
		SearchSystem searchSystem = new SearchSystem(senderMgr, receiverMgr, Dlist);
		searchSystem.searchMenu();
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
