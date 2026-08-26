package com.example.practice.sale;

public class InsufficientPaymentException extends RuntimeException {

	public InsufficientPaymentException(int total, int received) {
		super("お預かり金額が不足しています: 合計=" + total + ", お預かり=" + received);
	}
}
