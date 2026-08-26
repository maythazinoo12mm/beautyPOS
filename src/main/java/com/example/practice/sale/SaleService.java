package com.example.practice.sale;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.practice.item.ItemService;

@Service
@Transactional(readOnly = true)
public class SaleService {

	private final SaleRepository saleRepository;
	private final ItemService itemService;

	public SaleService(SaleRepository saleRepository, ItemService itemService) {
		this.saleRepository = saleRepository;
		this.itemService = itemService;
	}

	public List<Sale> findAll() {
		return saleRepository.findAll();
	}

	@Transactional
	public Sale checkout(Cart cart, int receivedAmount) {
		if (cart.isEmpty()) {
			throw new EmptyCartException();
		}
		int total = cart.getTotal();
		if (receivedAmount < total) {
			throw new InsufficientPaymentException(total, receivedAmount);
		}

		for (CartItem line : cart.getItems()) {
			itemService.decreaseStock(line.getItemId(), line.getQuantity());
		}

		Sale sale = new Sale(LocalDateTime.now(), total, receivedAmount, receivedAmount - total);
		for (CartItem line : cart.getItems()) {
			sale.addSaleItem(new SaleItem(line.getItemId(), line.getName(), line.getUnitPrice(), line.getQuantity()));
		}
		Sale saved = saleRepository.save(sale);

		cart.clear();
		return saved;
	}
}
