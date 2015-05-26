package org.shekhar.trainings.xebibookstore.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.shekhar.trainings.xebibookstore.exceptions.BookNotInInventoryException;
import org.shekhar.trainings.xebibookstore.exceptions.EmptyShoppingCartException;

public class ShoppingCart {

	private final Inventory inventory;

	private Map<String, Integer> itemsInCart = new HashMap<>();

	public ShoppingCart(Inventory inventory) {
		this.inventory = inventory;
	}

	public void add(String... books) throws BookNotInInventoryException {
		Arrays.stream(books).forEach(book -> add(book, 1));
	}

	public void add(String book, int quantity) throws BookNotInInventoryException {
		if (!inventory.exists(book)) {
			throw new BookNotInInventoryException(book);
		}
		itemsInCart.put(book, itemsInCart.compute(book, (k, v) -> (k == null) ? quantity : (v == null ? 0 : v) + quantity));
	}

	public int size() {
		return itemsInCart.values().stream().reduce(0, (total, quantity) -> total + quantity);
	}

	public Map<String, Integer> items() {
		return Collections.unmodifiableMap(itemsInCart);
	}

	public int checkout() throws EmptyShoppingCartException {
		if (itemsInCart.isEmpty()) {
			throw new EmptyShoppingCartException();
		}
		return items().entrySet().stream().map(entry -> entry.getValue() * inventory.bookPrice(entry.getKey())).reduce(0, (sum, element) -> sum += element);
	}

}
