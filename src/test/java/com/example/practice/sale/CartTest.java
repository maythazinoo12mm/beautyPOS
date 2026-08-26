package com.example.practice.sale;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.practice.item.Item;

class CartTest {

	private Cart cart;
	private Item lotion;
	private Item lipstick;

	@BeforeEach
	void setUp() {
		cart = new Cart();
		lotion = new Item("C001", "化粧水", 1200, 30);
		ReflectionTestUtils.setField(lotion, "id", 1L);
		lipstick = new Item("C003", "口紅", 1800, 25);
		ReflectionTestUtils.setField(lipstick, "id", 2L);
	}

	@Test
	void addItem_newItem_addsOneLine() {
		cart.addItem(lotion, 1);

		assertThat(cart.getItems()).hasSize(1);
		assertThat(cart.getTotal()).isEqualTo(1200);
	}

	@Test
	void addItem_sameItemTwice_accumulatesQuantityInSameLine() {
		cart.addItem(lotion, 1);
		cart.addItem(lotion, 1);

		assertThat(cart.getItems()).hasSize(1);
		assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
		assertThat(cart.getTotal()).isEqualTo(2400);
	}

	@Test
	void addItem_differentItems_addsSeparateLines() {
		cart.addItem(lotion, 2);
		cart.addItem(lipstick, 1);

		assertThat(cart.getItems()).hasSize(2);
		assertThat(cart.getTotal()).isEqualTo(1200 * 2 + 1800);
	}

	@Test
	void removeItem_removesMatchingLine() {
		cart.addItem(lotion, 1);
		cart.addItem(lipstick, 1);

		cart.removeItem(lotion.getId());

		assertThat(cart.getItems()).hasSize(1);
		assertThat(cart.getItems().get(0).getItemId()).isEqualTo(lipstick.getId());
	}

	@Test
	void clear_emptiesCart() {
		cart.addItem(lotion, 1);

		cart.clear();

		assertThat(cart.isEmpty()).isTrue();
		assertThat(cart.getTotal()).isZero();
	}

	@Test
	void isEmpty_newCart_isTrue() {
		assertThat(cart.isEmpty()).isTrue();
	}
}
