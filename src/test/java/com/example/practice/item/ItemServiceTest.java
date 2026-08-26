package com.example.practice.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ItemServiceTest {

	@Autowired
	private ItemService itemService;

	@Autowired
	private ItemRepository itemRepository;

	@Test
	void findByCodeOrName_byBarcode_returnsItem() {
		Item item = itemService.findByCodeOrName("C001");

		assertThat(item.getName()).isEqualTo("化粧水");
	}

	@Test
	void findByCodeOrName_byPartialName_returnsItem() {
		Item item = itemService.findByCodeOrName("口紅");

		assertThat(item.getBarcode()).isEqualTo("C003");
	}

	@Test
	void findByCodeOrName_unknownKeyword_throwsItemNotFoundException() {
		assertThatThrownBy(() -> itemService.findByCodeOrName("ZZZZ"))
				.isInstanceOf(ItemNotFoundException.class)
				.hasMessageContaining("ZZZZ");
	}

	@Test
	void searchByCodeOrName_blankKeyword_returnsAllItems() {
		assertThat(itemService.searchByCodeOrName("")).hasSizeGreaterThanOrEqualTo(6);
		assertThat(itemService.searchByCodeOrName(null)).hasSizeGreaterThanOrEqualTo(6);
	}

	@Test
	void searchByCodeOrName_partialName_returnsMatches() {
		assertThat(itemService.searchByCodeOrName("美容液"))
				.extracting(Item::getBarcode)
				.contains("C002");
	}

	@Test
	void create_duplicateBarcode_throwsDuplicateBarcodeException() {
		assertThatThrownBy(() -> itemService.create(new Item("C001", "重複商品", 100, 1)))
				.isInstanceOf(DuplicateBarcodeException.class)
				.hasMessageContaining("C001");
	}

	@Test
	void create_newBarcode_savesItem() {
		Item saved = itemService.create(new Item("T100", "テスト美容液", 2000, 5));

		assertThat(saved.getId()).isNotNull();
		assertThat(itemRepository.findByBarcode("T100")).isPresent();
	}

	@Test
	void update_toAnotherExistingBarcode_throwsDuplicateBarcodeException() {
		Item target = itemService.create(new Item("T101", "テスト対象", 500, 5));

		assertThatThrownBy(() -> itemService.update(target.getId(), new Item("C001", "テスト対象", 500, 5)))
				.isInstanceOf(DuplicateBarcodeException.class);
	}

	@Test
	void update_sameBarcodeAsBefore_doesNotThrow() {
		Item target = itemService.create(new Item("T102", "テスト対象2", 500, 5));

		Item updated = itemService.update(target.getId(), new Item("T102", "テスト対象2改", 600, 8));

		assertThat(updated.getName()).isEqualTo("テスト対象2改");
		assertThat(updated.getPrice()).isEqualTo(600);
	}

	@Test
	void decreaseStock_sufficientStock_decreasesByQuantity() {
		Item target = itemService.create(new Item("T103", "在庫テスト", 500, 10));

		itemService.decreaseStock(target.getId(), 3);

		assertThat(itemService.findById(target.getId()).getStock()).isEqualTo(7);
	}

	@Test
	void decreaseStock_insufficientStock_throwsInsufficientStockException() {
		Item target = itemService.create(new Item("T104", "在庫不足テスト", 500, 2));

		assertThatThrownBy(() -> itemService.decreaseStock(target.getId(), 3))
				.isInstanceOf(InsufficientStockException.class)
				.hasMessageContaining("在庫不足テスト");
		assertThat(itemService.findById(target.getId()).getStock()).isEqualTo(2);
	}

	@Test
	void findById_unknownId_throwsItemNotFoundException() {
		assertThatThrownBy(() -> itemService.findById(999_999L))
				.isInstanceOf(ItemNotFoundException.class);
	}
}
