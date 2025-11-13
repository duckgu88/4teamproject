package search;

import java.util.ArrayList;
import main.DeliveryOrder;

public class Matcher {
    public static ArrayList<DeliveryOrder> findMatches(Searchable searcher, ArrayList<DeliveryOrder> dList, String keyword) {
        ArrayList<DeliveryOrder> foundList = new ArrayList<>();
        for (DeliveryOrder order : dList) {
            if (searcher.match(order, keyword)) {
                foundList.add(order);
            }
        }
        return foundList;
    }
}
