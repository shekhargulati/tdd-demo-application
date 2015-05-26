package org.shekhar.trainings.xebibookstore.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.shekhar.trainings.xebibookstore.exceptions.BookNotInInventoryException;

public class ShoppingCart {

	private final Inventory inventoryManager;

	private List<String> itemsInCart = new ArrayList<>();

	public ShoppingCart(Inventory inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	public void add(String... books) {
		Map<Boolean, List<String>> booksPartionedByInventoryExistsPredicate = Arrays.stream(books).collect(Collectors.partitioningBy(book -> inventoryManager.exists(book)));
		if (booksPartionedByInventoryExistsPredicate.containsKey(false) && !booksPartionedByInventoryExistsPredicate.get(false).isEmpty()) {
			List<String> booksNotInInventory = booksPartionedByInventoryExistsPredicate.get(false);
			throw new BookNotInInventoryException(booksNotInInventory);
		}

		List<String> booksInInventory = booksPartionedByInventoryExistsPredicate.get(true);
		booksInInventory.forEach(book -> itemsInCart.add(book));

	}

	public int size() {
		return itemsInCart.size();
	}
	
	public List<String> items() {
		return Collections.unmodifiableList(itemsInCart);
	}

	public int amount() {
		return items().stream().map(book -> inventoryManager.bookPrice(book)).reduce(0, (sum, element) -> sum += element);
	}

}
