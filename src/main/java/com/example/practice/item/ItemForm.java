package com.example.practice.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ItemForm {

	@NotBlank(message = "商品コードを入力してください")
	private String barcode;

	@NotBlank(message = "商品名を入力してください")
	private String name;

	@Min(value = 0, message = "価格は0以上で入力してください")
	private int price;

	@Min(value = 0, message = "在庫数は0以上で入力してください")
	private int stock;

	public static ItemForm from(Item item) {
		ItemForm form = new ItemForm();
		form.barcode = item.getBarcode();
		form.name = item.getName();
		form.price = item.getPrice();
		form.stock = item.getStock();
		return form;
	}

	public Item toItem() {
		return new Item(barcode, name, price, stock);
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}
}
