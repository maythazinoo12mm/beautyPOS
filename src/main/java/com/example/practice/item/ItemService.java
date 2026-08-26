package com.example.practice.item;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ItemService {

	private final ItemRepository itemRepository;

	public ItemService(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	public List<Item> findAll() {
		return itemRepository.findAll();
	}

	public Item findById(Long id) {
		return itemRepository.findById(id)
				.orElseThrow(() -> new ItemNotFoundException(id));
	}

	public Item findByCodeOrName(String keyword) {
		return itemRepository.findByBarcode(keyword)
				.or(() -> itemRepository.findByNameContaining(keyword).stream().findFirst())
				.orElseThrow(() -> new ItemNotFoundException("商品が見つかりません: " + keyword));
	}

	public List<Item> searchByCodeOrName(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return itemRepository.findAll();
		}
		return itemRepository.findByBarcode(keyword)
				.map(List::of)
				.orElseGet(() -> itemRepository.findByNameContaining(keyword));
	}

	@Transactional
	public Item create(Item item) {
		if (itemRepository.findByBarcode(item.getBarcode()).isPresent()) {
			throw new DuplicateBarcodeException(item.getBarcode());
		}
		return itemRepository.save(item);
	}

	@Transactional
	public Item update(Long id, Item updated) {
		Item item = findById(id);
		itemRepository.findByBarcode(updated.getBarcode())
				.filter(other -> !other.getId().equals(id))
				.ifPresent(other -> { throw new DuplicateBarcodeException(updated.getBarcode()); });
		item.setBarcode(updated.getBarcode());
		item.setName(updated.getName());
		item.setPrice(updated.getPrice());
		item.setStock(updated.getStock());
		return itemRepository.save(item);
	}

	@Transactional
	public void delete(Long id) {
		Item item = findById(id);
		itemRepository.delete(item);
	}

	@Transactional
	public void decreaseStock(Long id, int quantity) {
		Item item = findById(id);
		if (item.getStock() < quantity) {
			throw new InsufficientStockException(item.getName(), item.getStock(), quantity);
		}
		item.setStock(item.getStock() - quantity);
	}
}
