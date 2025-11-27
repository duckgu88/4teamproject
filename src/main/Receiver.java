package main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import mgr.Manageable;

public class Receiver implements Manageable {
    private String name;
    private String phone;
    private String address;
    private int deliveryDays; // 배송 예상 일수 (0이면 배송완료)
    private String status;    // 배송전, 배송중, 배송완료
    private String request;
    private LocalDate estimatedArrivalDate; // 예상 도착 날짜

    @Override
    public void read(Scanner scan) {
        name = scan.next();
        phone = scan.next();

        StringBuilder addressBuilder = new StringBuilder();
        String currentToken = "";
        while (scan.hasNext()) {
            currentToken = scan.next();
            // 다음 토큰이 숫자이거나 알려진 상태 문자열 중 하나이면 주소가 끝났다고 판단
            if (currentToken.matches("\\d+") || 
                currentToken.equals("배송전") || 
                currentToken.equals("배송중") || 
                currentToken.equals("배송완료")) {
                break;
            }
            addressBuilder.append(currentToken).append(" ");
        }
        address = addressBuilder.toString().trim();

        // currentToken이 배송 일수이거나 최종 상태일 수 있음
        if (currentToken.matches("\\d+")) {
            deliveryDays = Integer.parseInt(currentToken);
            status = scan.next(); // 다음 토큰이 상태
        } else {
            deliveryDays = 0; // 배송완료이거나 일수 정보가 없는 경우
            status = currentToken; // 현재 토큰이 상태 (예: "배송완료")
        }
        
        calculateEstimatedArrivalDate(); // 예상 도착 날짜 계산
    }

    private void calculateEstimatedArrivalDate() {
        if (deliveryDays > 0) {
            estimatedArrivalDate = DeliverySystem.getCurrentDate().plusDays(deliveryDays);
        } else {
            estimatedArrivalDate = null; // 배송완료이거나 일수 정보가 없으면 null
        }
    }

    public void setStatus(String newStatus) {
        this.status = newStatus;
        if ("배송완료".equals(newStatus)) {
            this.estimatedArrivalDate = null;
            this.deliveryDays = 0;
        } else if ("배송중".equals(newStatus) || "배송전".equals(newStatus)) {
            // 상태가 배송 완료에서 변경되거나, 배송전/배송중으로 명시적으로 설정될 경우
            // deliveryDays는 변경하지 않고, estimatedArrivalDate만 다시 계산
            calculateEstimatedArrivalDate(); 
        }
    }
    
    // [신규 메소드] 남은 배송일수를 하루 감소시키고 도착 예정일을 재계산
    public void decrementDeliveryDays() {
        if (this.deliveryDays > 0) {
            this.deliveryDays--;
        }
        calculateEstimatedArrivalDate(); // 남은 일수가 변경되었으므로 도착 예정일 재계산
    }

    @Override
    public void print() {
    	System.out.printf("받는 사람: %s (%s, %s), 상태: %s\n",
                name, phone, address, status);
        if(request != null)
            System.out.println("요청사항: " + request);
    }
    
    public String getAddress() {
        return address;
    }

    public String getInfoString() {
    	String info = String.format("받는 사람: %s (%s, %s)\n%s",
                name, phone, address, getFormattedDeliveryStatus()); // 포맷된 상태 사용
        if (request != null && !request.isEmpty()) {
            info += "\n요청사항: " + request; 
        }
        return info;
    }

    // --- ▼ [필수] JTable에서 사용할 Getter 메소드 추가 ▼ ---
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getRequest() {
        return (request != null) ? request : "";
    }
    
    public void setRequest(String request) {
        this.request = request;
    }

    public String getStatus() { 
        return status; 
    }
    public int getDeliveryDays() { 
        return deliveryDays; 
    }

    public LocalDate getEstimatedArrivalDate() {
        return estimatedArrivalDate;
    }

    // [신규 메소드] 포맷팅된 배송 상태 문자열 반환
    public String getFormattedDeliveryStatus() {
        if ("배송완료".equals(status)) {
            return "배송 완료";
        } else if (estimatedArrivalDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일");
            return String.format("%s (%s 도착 예정)", status, estimatedArrivalDate.format(formatter));
        } else {
            return status; // 예상 도착일이 없으면 raw 상태 반환
        }
    }

    public void setAddress(String address) {
        this.address = address;
    }
}