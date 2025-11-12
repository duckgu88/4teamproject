package search;

import java.util.ArrayList;
import java.util.Scanner;
import main.DeliveryOrder;
import main.Sender;
import main.Receiver;
import mgr.Manager;

public class SearchSystem {
    Scanner scan = new Scanner(System.in);
    Manager<Sender> senderMgr;
    Manager<Receiver> receiverMgr;
    ArrayList<DeliveryOrder> dList;

    public SearchSystem(Manager<Sender> senderMgr, Manager<Receiver> receiverMgr, ArrayList<DeliveryOrder> dList) {
        this.senderMgr = senderMgr;
        this.receiverMgr = receiverMgr;
        this.dList = dList;
    }

    public void searchMenu() {
        System.out.println("\n=============== 조회 메뉴 ===============");
        System.out.println("1. 보내는 사람으로 조회");
        System.out.println("2. 받는 사람으로 조회");
        System.out.println("3. 송장번호로 조회");
        System.out.print(">> ");
        int choice = scan.nextInt();

        Searchable searcher = null;
        switch (choice) {
            case 1:
                searcher = new SenderSearcher();
                break;
            case 2:
                searcher = new ReceiverSearcher();
                break;
            case 3:
                searcher = new InvoiceSearcher();
                break;
            default:
                System.out.println("잘못된 선택입니다.");
                return;
        }
        SearchHelper.search(searcher, dList, scan);
    }
}
