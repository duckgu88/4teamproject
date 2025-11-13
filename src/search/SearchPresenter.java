package search;

import java.util.ArrayList;
import java.util.Scanner;
import main.DeliveryOrder;

public class SearchPresenter {
    public static void search(Searchable searcher, ArrayList<DeliveryOrder> dList, Scanner scan) {
        System.out.print(">> 검색할 키워드를 입력하세요: ");
        String keyword = scan.next();

        ArrayList<DeliveryOrder> foundList = Matcher.findMatches(searcher, dList, keyword);

        System.out.println("\n=============== 검색 결과 ===============");
        
        if (foundList.isEmpty()) {
            System.out.println("해당하는 배송 정보가 없습니다.");
        } 
        else {
            for (DeliveryOrder order : foundList) {
                order.print();
            }
        }
        System.out.println("=======================================");
    }
}
