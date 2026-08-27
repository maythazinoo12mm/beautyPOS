package com.example.practice.sale;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SaleHistoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void list_returnsSalesView() throws Exception {
		mockMvc.perform(get("/sales"))
				.andExpect(status().isOk())
				.andExpect(view().name("sales"));
	}

	@Test
	void list_afterCheckout_showsRegisteredSale() throws Exception {
		MockHttpSession session = new MockHttpSession();
		mockMvc.perform(post("/pos/quick-add").param("keyword", "C002").session(session));
		mockMvc.perform(post("/pos/checkout").param("receivedAmount", "3500").session(session));

		mockMvc.perform(get("/sales"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("3500")));
	}
}
