package com.example.practice.sale;

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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 空欄でのクイック追加の回帰テスト（2026-08-25にpos.htmlのボタンへformnovalidateを追加して修正）。
 */
@SpringBootTest
@AutoConfigureMockMvc
class PosControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void quickAdd_blankKeyword_showsGuidanceMessageWithoutError() throws Exception {
		mockMvc.perform(post("/pos/quick-add").param("keyword", ""))
				.andExpect(status().isOk())
				.andExpect(view().name("pos"))
				.andExpect(content().string(containsString("商品コードまたは商品名を入力してください")));
	}

	@Test
	void quickAdd_unknownKeyword_showsNotFoundMessage() throws Exception {
		mockMvc.perform(post("/pos/quick-add").param("keyword", "ZZZZ"))
				.andExpect(status().isOk())
				.andExpect(view().name("pos"))
				.andExpect(content().string(containsString("商品が見つかりません: ZZZZ")));
	}

	@Test
	void quickAdd_validCode_addsToCartAndRedirects() throws Exception {
		MockHttpSession session = new MockHttpSession();

		mockMvc.perform(post("/pos/quick-add").param("keyword", "C001").session(session))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/pos"));
	}

	@Test
	void quickAdd_sameCodeTwice_accumulatesQuantityInSameSession() throws Exception {
		MockHttpSession session = new MockHttpSession();

		mockMvc.perform(post("/pos/quick-add").param("keyword", "C001").session(session));
		mockMvc.perform(post("/pos/quick-add").param("keyword", "C001").session(session));

		mockMvc.perform(get("/pos").session(session))
				.andExpect(content().string(containsString("2400円")));
	}

	@Test
	void checkout_emptyCart_redirectsToPaymentWithError() throws Exception {
		MockHttpSession session = new MockHttpSession();

		mockMvc.perform(post("/pos/checkout").param("receivedAmount", "1000").session(session))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/pos/payment"));
	}

	@Test
	void checkout_insufficientPayment_redirectsToPaymentWithError() throws Exception {
		MockHttpSession session = new MockHttpSession();
		mockMvc.perform(post("/pos/quick-add").param("keyword", "C001").session(session));

		mockMvc.perform(post("/pos/checkout").param("receivedAmount", "100").session(session))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/pos/payment"));
	}

	@Test
	void checkout_sufficientPayment_redirectsToReceipt() throws Exception {
		MockHttpSession session = new MockHttpSession();
		mockMvc.perform(post("/pos/quick-add").param("keyword", "C001").session(session));

		mockMvc.perform(post("/pos/checkout").param("receivedAmount", "2000").session(session))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/pos/receipt"));
	}
}
