package main;

import java.util.Scanner;
import mgr.Manageable;

public class Receiver implements Manageable {
	String name;
	String phone;
	String address;

	@Override
	public void read(Scanner scan) {
		name = scan.next();
		phone = scan.next();
		address = scan.next() + " " + scan.next();
	}

	@Override
	public void print() {
		System.out.printf("받는 사람: %s (%s, %s)\n", name, phone, address);
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}
}