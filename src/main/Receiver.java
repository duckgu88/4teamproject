package main;

import java.util.Scanner;
import mgr.Manageable;

public class Receiver implements Manageable {
	private String name;
	private String phone;
	private String address;
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

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public String getAddress() {
		return address;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}
}