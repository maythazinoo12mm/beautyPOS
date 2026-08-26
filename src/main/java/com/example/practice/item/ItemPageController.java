package com.example.practice.item;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/items")
public class ItemPageController {

	private final ItemService itemService;

	public ItemPageController(ItemService itemService) {
		this.itemService = itemService;
	}

	@GetMapping
	public String list(Model model) {
		model.addAttribute("items", itemService.findAll());
		return "items";
	}

	@GetMapping("/new")
	public String newForm(Model model) {
		model.addAttribute("item", new ItemForm());
		return "item-form";
	}

	@PostMapping
	public String create(@Valid @ModelAttribute("item") ItemForm form, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			return "item-form";
		}
		try {
			itemService.create(form.toItem());
			return "redirect:/items";
		} catch (DuplicateBarcodeException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "item-form";
		}
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) {
		Item item = itemService.findById(id);
		model.addAttribute("item", ItemForm.from(item));
		model.addAttribute("id", id);
		return "item-form";
	}

	@PostMapping("/{id}")
	public String update(@PathVariable Long id, @Valid @ModelAttribute("item") ItemForm form, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("id", id);
			return "item-form";
		}
		try {
			itemService.update(id, form.toItem());
			return "redirect:/items";
		} catch (DuplicateBarcodeException e) {
			model.addAttribute("id", id);
			model.addAttribute("errorMessage", e.getMessage());
			return "item-form";
		}
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id) {
		itemService.delete(id);
		return "redirect:/items";
	}
}
