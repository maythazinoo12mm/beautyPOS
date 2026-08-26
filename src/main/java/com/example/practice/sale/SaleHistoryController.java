package com.example.practice.sale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SaleHistoryController {

	private final SaleService saleService;

	public SaleHistoryController(SaleService saleService) {
		this.saleService = saleService;
	}

	@GetMapping("/sales")
	public String list(Model model) {
		model.addAttribute("sales", saleService.findAll());
		return "sales";
	}
}
