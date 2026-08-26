package com.example.practice.item;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

	Optional<Item> findByBarcode(String barcode);

	List<Item> findByNameContaining(String keyword);
}
