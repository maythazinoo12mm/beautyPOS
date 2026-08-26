package com.example.practice.sale;

public class CartItem {

	private final Long itemId;
	private final String barcode;
	private final String name;
	private final int unitPrice;
	private int quantity;

	public CartItem(Long itemId, String barcode, String name, int unitPrice, int quantity) {
		this.itemId = itemId;
		this.barcode = barcode;
		this.name = name;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
	}

	public void addQuantity(int quantity) {
		this.quantity += quantity;
	}

	public int getSubtotal() {
		return unitPrice * quantity;
	}

	public Long getItemId() {
		return itemId;
	}

	public String getBarcode() {
		return barcode;
	}

	public String getName() {
		return name;
	}

	public int getUnitPrice() {
		return unitPrice;
	}

	public int getQuantity() {
		return quantity;
	}
}
