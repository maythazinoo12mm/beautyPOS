package com.example.practice.item;

public class InsufficientStockException extends RuntimeException {

	public InsufficientStockException(String itemName, int currentStock, int requested) {
		super("在庫が不足しています: " + itemName + " (在庫=" + currentStock + ", 要求=" + requested + ")");
	}
}
