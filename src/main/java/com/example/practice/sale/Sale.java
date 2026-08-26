package com.example.practice.sale;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Sale {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime saleDateTime;

	private int totalAmount;

	private int receivedAmount;

	private int changeAmount;

	@OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SaleItem> saleItems = new ArrayList<>();

	protected Sale() {
		// JPA用のデフォルトコンストラクタ
	}

	public Sale(LocalDateTime saleDateTime, int totalAmount, int receivedAmount, int changeAmount) {
		this.saleDateTime = saleDateTime;
		this.totalAmount = totalAmount;
		this.receivedAmount = receivedAmount;
		this.changeAmount = changeAmount;
	}

	public void addSaleItem(SaleItem saleItem) {
		saleItem.setSale(this);
		saleItems.add(saleItem);
	}

	public Long getId() {
		return id;
	}

	public LocalDateTime getSaleDateTime() {
		return saleDateTime;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public int getReceivedAmount() {
		return receivedAmount;
	}

	public int getChangeAmount() {
		return changeAmount;
	}

	public List<SaleItem> getSaleItems() {
		return saleItems;
	}
}
