package main;

import java.util.Scanner;
import mgr.Manageable;

public class Receiver implements Manageable {
	String name;
	String phone;
	String address;
	String request;

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

	public String getAdderss() {
		return address;
	}
}