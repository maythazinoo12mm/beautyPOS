package com.example.practice.sale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.practice.item.Item;
import com.example.practice.item.ItemRepository;
import com.example.practice.item.ItemService;

@SpringBootTest
class SaleServiceTest {

	@Autowired
	private SaleService saleService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private ItemRepository itemRepository;

	@Test
	void checkout_decreasesStockAndRegistersSale() {
		Item item = itemRepository.save(new Item("T001", "テスト美容液", 1000, 10));
		Cart cart = new Cart();
		cart.addItem(item, 2);

		Sale sale = saleService.checkout(cart, 3000);

		assertThat(sale.getId()).isNotNull();
		assertThat(sale.getTotalAmount()).isEqualTo(2000);
		assertThat(sale.getChangeAmount()).isEqualTo(1000);
		assertThat(sale.getSaleItems()).hasSize(1);
		assertThat(itemService.findById(item.getId()).getStock()).isEqualTo(8);
		assertThat(cart.isEmpty()).isTrue();
	}

	@Test
	void checkout_withInsufficientPayment_throws() {
		Item item = itemRepository.save(new Item("T002", "テスト口紅", 1000, 10));
		Cart cart = new Cart();
		cart.addItem(item, 1);

		assertThatThrownBy(() -> saleService.checkout(cart, 500))
				.isInstanceOf(InsufficientPaymentException.class);
	}

	@Test
	void checkout_withEmptyCart_throws() {
		Cart cart = new Cart();

		assertThatThrownBy(() -> saleService.checkout(cart, 1000))
				.isInstanceOf(EmptyCartException.class);
	}
}
