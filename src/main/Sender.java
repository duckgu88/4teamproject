package main;

import java.util.Scanner;
import mgr.Manageable;

public class Sender implements Manageable {
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
		System.out.printf("보내는 사람: %s (%s, %s, %s, %s, %s)\n", name, phone, address, company, weight, item);
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

	public String getCompany() {
		return company;
	}

	public String getWeight() {
		return weight;
	}

	public String getItem() {
		return item;
	}
}