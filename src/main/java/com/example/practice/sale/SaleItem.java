package com.example.practice.sale;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class SaleItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "sale_id")
	private Sale sale;

	private Long itemId;

	private String itemName;

	private int unitPrice;

	private int quantity;

	private int subtotal;

	protected SaleItem() {
		// JPA用のデフォルトコンストラクタ
	}

	public SaleItem(Long itemId, String itemName, int unitPrice, int quantity) {
		this.itemId = itemId;
		this.itemName = itemName;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
		this.subtotal = unitPrice * quantity;
	}

	void setSale(Sale sale) {
		this.sale = sale;
	}

	public Long getId() {
		return id;
	}

	public Sale getSale() {
		return sale;
	}

	public Long getItemId() {
		return itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public int getUnitPrice() {
		return unitPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getSubtotal() {
		return subtotal;
	}
}
