package com.example.practice.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 商品コード重複登録・必須項目未入力の回帰テスト（2026-08-25にDuplicateBarcodeException新設・@Valid追加で修正）。
 */
@SpringBootTest
@AutoConfigureMockMvc
class ItemPageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ItemService itemService;

	@Test
	void list_showsSeedItems() throws Exception {
		mockMvc.perform(get("/items"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("化粧水")));
	}

	@Test
	void create_duplicateBarcode_showsErrorAndStaysOnForm() throws Exception {
		mockMvc.perform(post("/items")
						.param("barcode", "C001")
						.param("name", "重複テスト")
						.param("price", "100")
						.param("stock", "1"))
				.andExpect(status().isOk())
				.andExpect(view().name("item-form"))
				.andExpect(content().string(containsString("この商品コードは既に使用されています: C001")));
	}

	@Test
	void create_blankName_showsValidationErrorWithoutServerError() throws Exception {
		mockMvc.perform(post("/items")
						.param("barcode", "T200")
						.param("name", "")
						.param("price", "100")
						.param("stock", "1"))
				.andExpect(status().isOk())
				.andExpect(view().name("item-form"))
				.andExpect(content().string(containsString("商品名を入力してください")));
	}

	@Test
	void create_validItem_redirectsToList() throws Exception {
		mockMvc.perform(post("/items")
						.param("barcode", "T201")
						.param("name", "新規テスト商品")
						.param("price", "1500")
						.param("stock", "10"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/items"));
	}

	@Test
	void editForm_showsExistingValues() throws Exception {
		Item item = itemService.create(new Item("T202", "編集対象", 800, 4));

		mockMvc.perform(get("/items/{id}/edit", item.getId()))
				.andExpect(status().isOk())
				.andExpect(view().name("item-form"))
				.andExpect(content().string(containsString("編集対象")));
	}

	@Test
	void update_validChanges_redirectsToList() throws Exception {
		Item item = itemService.create(new Item("T203", "更新前商品", 1000, 5));

		mockMvc.perform(post("/items/{id}", item.getId())
						.param("barcode", "T203")
						.param("name", "更新後商品")
						.param("price", "1200")
						.param("stock", "8"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/items"));

		assertThat(itemService.findById(item.getId()).getName()).isEqualTo("更新後商品");
	}

	@Test
	void update_toAnotherExistingBarcode_showsErrorAndStaysOnForm() throws Exception {
		itemService.create(new Item("T204", "他の商品", 1000, 1));
		Item target = itemService.create(new Item("T205", "更新対象", 1000, 1));

		mockMvc.perform(post("/items/{id}", target.getId())
						.param("barcode", "T204")
						.param("name", "更新対象")
						.param("price", "1000")
						.param("stock", "1"))
				.andExpect(status().isOk())
				.andExpect(view().name("item-form"))
				.andExpect(content().string(containsString("この商品コードは既に使用されています: T204")));
	}

	@Test
	void delete_existingItem_removesItAndRedirectsToList() throws Exception {
		Item item = itemService.create(new Item("T206", "削除対象商品", 500, 1));

		mockMvc.perform(post("/items/{id}/delete", item.getId()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/items"));

		assertThatThrownBy(() -> itemService.findById(item.getId()))
				.isInstanceOf(ItemNotFoundException.class);
	}
}
