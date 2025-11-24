package main;

import java.util.Scanner;
import mgr.Manageable;

public class Sender implements Manageable {
    // [수정] 필드를 private으로 변경 (캡슐화)
    private String name;
    private String phone;
    private String address;
    private String company;
    private String weight;
    private String item;

    @Override
    public void read(Scanner scan) {
        name = scan.next();
        phone = scan.next();
        address = scan.next() + " " + scan.next();
        company = scan.next();
        weight = scan.next();
        item = scan.next();
    }

    @Override
    public void print() {
        System.out.printf("보내는 사람: %s (%s, %s)\n", name, phone, address);
        System.out.printf("  (물품: %s(%skg), 발송처: %s)\n", item, weight, company);
    }

    // [새 메소드] GUI에서 사용할 문자열 반환
    public String getInfoString() {
        return String.format(
            "보내는 사람: %s (%s, %s)\n  (물품: %s(%skg), 발송처: %s)",
            name, phone, address, item, weight, company
        );
    }

    // --- ▼ [필수] search 및 guitool에서 사용할 Getter 메소드 추가 ▼ ---
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getCompany() {
        return company;
    }

    public String getWeight() {
        return weight;
    }

    public String getItem() {
        return item;
    }
    // --- ▲ [필수] Getter 메소드 추가 ▲ ---
}