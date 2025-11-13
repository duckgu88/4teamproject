package search;

import java.util.ArrayList;
import java.util.Scanner;
import main.DeliveryOrder;
import main.Receiver;
import main.Sender;
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
        while (true) {
            System.out.println("\n=============== 조회 메뉴 ===============");
            System.out.println("1. 보내는 사람으로 조회");
            System.out.println("2. 받는 사람으로 조회");
            System.out.println("3. 송장번호로 조회");
            System.out.println("4. 지역별로 조회");
            System.out.println("0. 메인 메뉴로");
            System.out.print(">> ");
            String choice = scan.next();

            if (choice.equals("0")) {
                System.out.println("메인 메뉴로 돌아갑니다.");
                break;
            }

            Searchable searcher = null;
            switch (choice) {
                case "1":
                    searcher = new SenderSearcher();
                    break;
                case "2":
                    searcher = new ReceiverSearcher();
                    break;
                case "3":
                    searcher = new InvoiceSearcher();
                    break;
                case "4":
                    searcher = new RegionSearcher();
                    break;
                default:
                    System.out.println("잘못된 선택입니다.");
                    continue; 
            }
            
            SearchPresenter.search(searcher, dList, scan);
        }
    }
}
