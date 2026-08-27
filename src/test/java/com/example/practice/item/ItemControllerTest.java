package com.example.practice.item;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ItemService itemService;

	@Test
	void getAll_returnsSeedData() throws Exception {
		mockMvc.perform(get("/api/items"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()", greaterThanOrEqualTo(3)));
	}

	@Test
	void create_returnsCreatedItem() throws Exception {
		String body = """
				{"barcode":"C999","name":"モニター","price":30000,"stock":3}
				""";

		mockMvc.perform(post("/api/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("モニター"));
	}

	@Test
	void create_duplicateBarcode_returnsConflict() throws Exception {
		itemService.create(new Item("C900", "重複元", 1000, 1));
		String body = """
				{"barcode":"C900","name":"重複先","price":1000,"stock":1}
				""";

		mockMvc.perform(post("/api/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isConflict());
	}

	@Test
	void create_blankName_returnsBadRequest() throws Exception {
		String body = """
				{"barcode":"C901","name":"","price":1000,"stock":1}
				""";

		mockMvc.perform(post("/api/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getOne_existingId_returnsItem() throws Exception {
		Item item = itemService.create(new Item("C902", "個別取得テスト", 500, 5));

		mockMvc.perform(get("/api/items/{id}", item.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.barcode").value("C902"));
	}

	@Test
	void getOne_unknownId_returnsNotFound() throws Exception {
		mockMvc.perform(get("/api/items/{id}", 999_999))
				.andExpect(status().isNotFound());
	}

	@Test
	void update_existingId_returnsUpdatedItem() throws Exception {
		Item item = itemService.create(new Item("C903", "更新前", 1000, 1));
		String body = """
				{"barcode":"C903","name":"更新後","price":2000,"stock":9}
				""";

		mockMvc.perform(put("/api/items/{id}", item.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("更新後"))
				.andExpect(jsonPath("$.price").value(2000));
	}

	@Test
	void update_unknownId_returnsNotFound() throws Exception {
		String body = """
				{"barcode":"C904","name":"存在しない","price":1000,"stock":1}
				""";

		mockMvc.perform(put("/api/items/{id}", 999_999)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isNotFound());
	}

	@Test
	void delete_existingId_returnsNoContent() throws Exception {
		Item item = itemService.create(new Item("C905", "削除対象", 500, 1));

		mockMvc.perform(delete("/api/items/{id}", item.getId()))
				.andExpect(status().isNoContent());
		mockMvc.perform(get("/api/items/{id}", item.getId()))
				.andExpect(status().isNotFound());
	}

	@Test
	void delete_unknownId_returnsNotFound() throws Exception {
		mockMvc.perform(delete("/api/items/{id}", 999_999))
				.andExpect(status().isNotFound());
	}
}
