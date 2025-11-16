package search;

import main.DeliveryOrder;

public class RegionSearcher implements Searchable {
    @Override
    public boolean match(DeliveryOrder order, String keyword) {
        // 받는 사람의 주소에 키워드가 포함되어 있는지 확인
        return order.getReceiver().getAddress().contains(keyword);
    }
}