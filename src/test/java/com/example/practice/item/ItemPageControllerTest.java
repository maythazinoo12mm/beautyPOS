package com.example.practice.item;

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
}
