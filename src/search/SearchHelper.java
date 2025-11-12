package search;

import java.util.ArrayList;
import java.util.Scanner;
import main.DeliveryOrder;

public class SearchHelper {
    public static void search(Searchable searcher, ArrayList<DeliveryOrder> dList, Scanner scan) {
        System.out.print(">> 검색할 키워드를 입력하세요: ");
        String keyword = scan.next();
        boolean found = false;

        System.out.println("\n=============== 검색 결과 ===============");
        for (DeliveryOrder order : dList) {
            if (searcher.match(order, keyword)) {
                order.print();
                found = true;
            }
        }

        if (!found) {
            System.out.println("해당하는 배송 정보가 없습니다.");
        }
        System.out.println("=======================================");
    }
}
