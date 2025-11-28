package guitool;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import main.DeliveryOrder;
import main.DeliverySystem;
import search.InvoiceSearcher;
import search.ItemSearcher;
import search.Matcher;
import search.PhoneSearcher;
import search.ReceiverSearcher;
import search.RegionSearcher;
import search.Searchable;
import search.SenderSearcher;

public class InquiryPresenter {

    private final InquiryPage view;

    public InquiryPresenter(InquiryPage view) {
        this.view = view;
    }

    // 검색 로직을 수행하고 결과를 뷰에 업데이트
    public void performSearch(String category, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "검색어를 입력해주세요.");
            return;
        }

        Searchable searcher = createSearcher(category);
        if (searcher == null) {
            return; // 유효하지 않은 카테고리 예외처리 
        }

        ArrayList<DeliveryOrder> allList = DeliverySystem.getInstance().Dlist;
        ArrayList<DeliveryOrder> searchResults = Matcher.findMatches(searcher, allList, keyword);

        // 뷰에 결과 업데이트 요청
        view.updateTable(searchResults, true);
    }

    // 특정 주문을 삭제
    public void deleteOrder(DeliveryOrder orderToDelete) {
        // Model update
        boolean removed = DeliverySystem.getInstance().Dlist.remove(orderToDelete);

        if (removed) {
            // 성공
            view.onOrderDeletionSuccess(orderToDelete);
        } else {
            //실패
            view.onOrderDeletionFailure();
        }
    }

    // 특정 주문의 주소를 수정
    public void editOrderAddress(DeliveryOrder orderToEdit, String newAddress) {
        if (newAddress != null && !newAddress.trim().isEmpty()) {
            orderToEdit.getReceiver().setAddress(newAddress);
            view.onEditAddressSuccess(orderToEdit, newAddress);
        }
    }

    // 카테고리 문자열에 따라 적절한 Searchable 구현체를 생성
    private Searchable createSearcher(String category) {
        switch (category) {
            case "보내는 사람":
                return new SenderSearcher();
            case "받는 사람":
                return new ReceiverSearcher();
            case "송장번호":
                return new InvoiceSearcher();
            case "지역":
                return new RegionSearcher();
            case "전화번호":
                return new PhoneSearcher();
            case "물품명":
                return new ItemSearcher();
            default:
                return null;
        }
    }
}
