package main;

import java.util.Scanner;
import mgr.Manageable;

public class Receiver implements Manageable {
    // [수정] 필드를 private으로 변경
    private String name;
    private String phone;
    private String address;
    // [수정] request 필드도 private으로 변경
    private String request;

    @Override
    public void read(Scanner scan) {
        name = scan.next();
        phone = scan.next();
        address = scan.next() + " " + scan.next();
    }

    @Override
    public void print() {
        System.out.printf("받는 사람: %s (%s, %s), ", name, phone, address);
        if(request != null)
            System.out.println("요청사항: " + request);
    }
    
    public String getAddress() { // [수정] 오타 수정 (getAdderss -> getAddress)
        return address;
    }

    // [새 메소드] GUI에서 사용할 문자열 반환
    public String getInfoString() {
        String info = String.format("받는 사람: %s (%s, %s)", name, phone, address);
        if (request != null) {
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
    
    // --- ▼ [신규 추가] 'request' 변수에 값을 설정할 Setter 메소드 ▼ ---
    public void setRequest(String request) {
        this.request = request;
    }
}