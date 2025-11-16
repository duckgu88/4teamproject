package main;

import java.io.File;
import java.util.*;
import mgr.*;
import search.SearchSystem;

public class DeliverySystem {
	Scanner scan = new Scanner(System.in);
	Manager<Sender> senderMgr = new Manager<>();
	Manager<Receiver> receiverMgr = new Manager<>();
	ArrayList<RegionGroup> regionGroups = new ArrayList<>();
	ArrayList<DeliveryOrder> Dlist = new ArrayList<>();

	void mymain() {
		senderMgr.readAll("senders.txt", new SenderFactory());
		receiverMgr.readAll("receivers.txt", new ReceiverFactory());
		createDeliveryOrder();

		RegionManager rm = new RegionManager();
		rm.groupRegion(Dlist);
		rm.assignDrivers("drivers.txt");

		ArrayList<String> requests = readRequests();
		addRequests(requests, rm);

		while (true) {
			System.out.println("\n=============== 메인 메뉴 ===============");
			System.out.println("1. 전체 배송 목록 출력");
			System.out.println("2. 지역별 배송 목록 출력");
			System.out.println("3. 배송 조회");
			System.out.println("0. 종료");
			System.out.print(">> ");
			String choice = scan.next();

			switch (choice) {
			case "1":
				for (DeliveryOrder order : Dlist) {
					order.print();
				}
				break;
			case "2":
				rm.printRegion();
				break;
			case "3":
				SearchSystem searchSystem = new SearchSystem(senderMgr, receiverMgr, Dlist);
				searchSystem.searchMenu();
				break;
			case "0":
				System.out.println("프로그램을 종료합니다.");
				return;
			default:
				System.out.println("잘못된 선택입니다.");
			}
		}
	}

	ArrayList<String> readRequests() {
		ArrayList<String> requests = new ArrayList<>();
		try (Scanner scan = new Scanner(new File("requests.txt"))) {
			while (scan.hasNextLine()) {
				requests.add(scan.nextLine());
			}
		} catch (Exception e) {
			System.out.println("requests.txt 읽기 실패");
		}
		return requests;
	}

	private void addRequests(ArrayList<String> requests, RegionManager rm) {
		Random random = new Random();
		for (RegionGroup group : rm.rList) {
			for (DeliveryOrder order : group.orders) {
				if (!requests.isEmpty()) {
					order.getReceiver().setRequest(requests.get(random.nextInt(requests.size())));
				}
			}
		}
	}

	public static void main(String[] args) {
		DeliverySystem dm = new DeliverySystem();
		dm.mymain();
	}

	void createDeliveryOrder() {
		int size = senderMgr.getList().size();
		for (int i = 0; i < size; i++) {
			Sender s = senderMgr.getList().get(i);
			Receiver r = receiverMgr.getList().get(i);
			Dlist.add(new DeliveryOrder(s, r));
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
}