package com.example.practice.sale;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.example.practice.item.Item;

@Component
@SessionScope
public class Cart {

	private final List<CartItem> items = new ArrayList<>();

	public void addItem(Item item, int quantity) {
		Optional<CartItem> existing = items.stream()
				.filter(line -> line.getItemId().equals(item.getId()))
				.findFirst();
		if (existing.isPresent()) {
			existing.get().addQuantity(quantity);
		} else {
			items.add(new CartItem(item.getId(), item.getBarcode(), item.getName(), item.getPrice(), quantity));
		}
	}

	public void removeItem(Long itemId) {
		items.removeIf(line -> line.getItemId().equals(itemId));
	}

	public void clear() {
		items.clear();
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	public List<CartItem> getItems() {
		return items;
	}

	public int getTotal() {
		return items.stream().mapToInt(CartItem::getSubtotal).sum();
	}
}
