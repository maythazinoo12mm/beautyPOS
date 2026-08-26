package com.example.practice.item;

public class DuplicateBarcodeException extends RuntimeException {

	public DuplicateBarcodeException(String barcode) {
		super("この商品コードは既に使用されています: " + barcode);
	}
}
