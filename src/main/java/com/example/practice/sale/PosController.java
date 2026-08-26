package com.example.practice.sale;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.practice.item.InsufficientStockException;
import com.example.practice.item.Item;
import com.example.practice.item.ItemNotFoundException;
import com.example.practice.item.ItemService;

@Controller
@RequestMapping("/pos")
public class PosController {

	private final ItemService itemService;
	private final SaleService saleService;
	private final Cart cart;

	public PosController(ItemService itemService, SaleService saleService, Cart cart) {
		this.itemService = itemService;
		this.saleService = saleService;
		this.cart = cart;
	}

	@GetMapping
	public String index(Model model) {
		model.addAttribute("cart", cart);
		return "pos";
	}

	@PostMapping("/search")
	public String search(@RequestParam String keyword, Model model) {
		model.addAttribute("cart", cart);
		model.addAttribute("keyword", keyword);
		model.addAttribute("searchPerformed", true);
		List<Item> results = itemService.searchByCodeOrName(keyword);
		if (results.isEmpty()) {
			model.addAttribute("searchError", keyword.isBlank() ? "登録されている商品がありません" : "商品が見つかりません: " + keyword);
		} else {
			model.addAttribute("searchResults", results);
		}
		return "pos";
	}

	@PostMapping("/quick-add")
	public String quickAdd(@RequestParam String keyword, Model model) {
		if (keyword.isBlank()) {
			model.addAttribute("cart", cart);
			model.addAttribute("keyword", keyword);
			model.addAttribute("errorMessage", "商品コードまたは商品名を入力してください");
			return "pos";
		}
		try {
			Item item = itemService.findByCodeOrName(keyword);
			cart.addItem(item, 1);
			return "redirect:/pos";
		} catch (ItemNotFoundException e) {
			model.addAttribute("cart", cart);
			model.addAttribute("keyword", keyword);
			model.addAttribute("errorMessage", e.getMessage());
			return "pos";
		}
	}

	@PostMapping("/cart/add")
	public String addToCart(@RequestParam Long itemId, @RequestParam(defaultValue = "1") int quantity) {
		Item item = itemService.findById(itemId);
		cart.addItem(item, quantity);
		return "redirect:/pos";
	}

	@PostMapping("/cart/remove/{itemId}")
	public String removeFromCart(@PathVariable Long itemId) {
		cart.removeItem(itemId);
		return "redirect:/pos";
	}

	@PostMapping("/new")
	public String startNewSale() {
		cart.clear();
		return "redirect:/pos";
	}

	@GetMapping("/payment")
	public String paymentForm(Model model) {
		model.addAttribute("cart", cart);
		return "payment";
	}

	@PostMapping("/checkout")
	public String checkout(@RequestParam int receivedAmount, RedirectAttributes redirectAttributes) {
		try {
			Sale sale = saleService.checkout(cart, receivedAmount);
			redirectAttributes.addFlashAttribute("sale", sale);
			return "redirect:/pos/receipt";
		} catch (EmptyCartException | InsufficientPaymentException | InsufficientStockException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/pos/payment";
		}
	}

	@GetMapping("/receipt")
	public String receipt(Model model) {
		if (!model.containsAttribute("sale")) {
			return "redirect:/pos";
		}
		return "receipt";
	}
}
