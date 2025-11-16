package main;

import java.util.Random;

public class DeliveryOrder {
	private Sender sender;
	private Receiver receiver;
	private int invoiceNumber;

	public DeliveryOrder(Sender s, Receiver r) {
		this.sender = s;
		this.receiver = r;
		this.invoiceNumber = createNumber();
	}

	private int createNumber() {
		Random rand = new Random();
		return 100000 + rand.nextInt(900000);
	}

	public void print( ) {
		System.out.printf("송장번호: %d\n", invoiceNumber);
		sender.print();
		receiver.print();
		System.out.println();
	}

	public Sender getSender() {
		return sender;
	}

	public Receiver getReceiver() {
		return receiver;
	}

	public int getInvoiceNumber() {
		return invoiceNumber;
	}
}