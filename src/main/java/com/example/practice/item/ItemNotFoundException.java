package com.example.practice.item;

public class ItemNotFoundException extends RuntimeException {

	public ItemNotFoundException(Long id) {
		super("Item not found: id=" + id);
	}

	public ItemNotFoundException(String message) {
		super(message);
	}
}
