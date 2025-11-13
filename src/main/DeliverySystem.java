package main;

import java.io.File;
import java.util.*;
import mgr.*;

public class DeliverySystem {
	Scanner scan = new Scanner(System.in);
	Manager<Sender> senderMgr = new Manager<>();
	static Manager<Receiver> receiverMgr = new Manager<>();
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
        
        rm.printRegion();
	}
	
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
	
	private void addRequests(ArrayList<String> requests, RegionManager rm) {
		Random random = new Random();
		for(RegionGroup group : rm.rList) {
			for (DeliveryOrder order : group.orders) {
				if(!requests.isEmpty()) {
					order.receiver.request = requests.get(random.nextInt(requests.size()));
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
}