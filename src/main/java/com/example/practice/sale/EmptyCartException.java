package com.example.practice.sale;

public class EmptyCartException extends RuntimeException {

	public EmptyCartException() {
		super("カートが空です");
	}
}
