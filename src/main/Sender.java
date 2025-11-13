package main;

import java.util.Scanner;
import mgr.Manageable;

public class Sender implements Manageable {
	String name;
	String phone;
	String address;
	String company;
	String weight;
	String item;

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
}