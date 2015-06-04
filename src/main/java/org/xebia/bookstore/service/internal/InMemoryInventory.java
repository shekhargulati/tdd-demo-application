package org.xebia.bookstore.service.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.xebia.bookstore.exceptions.BookNotInInventoryException;
import org.xebia.bookstore.model.Book;
import org.xebia.bookstore.service.Inventory;

public class InMemoryInventory implements Inventory {

	private Map<String, Book> inventory = new HashMap<>();

	@Override
	public boolean exists(final String title) {
		return inventory.containsKey(title);
	}

	@Override
	public String[] add(Book... books) {
		return Arrays.stream(books).map(book -> add(book)).toArray(String[]::new);

	}

	@Override
	public String add(Book book) {
		String id = BookIdGenerator.newBookIdentifer();
		book.assignBookId(id);
		inventory.put(book.getTitle(), book);
		return id;
	}

	@Override
	public boolean hasEnoughCopies(String title, int quantity) {
		return inventory.get(title).getQuantity() >= quantity;
	}

	private static class BookIdGenerator {

		public static String newBookIdentifer() {
			return UUID.randomUUID().toString();
		}
	}

	@Override
	public Book find(String title) {
		if (!exists(title)) {
			throw new BookNotInInventoryException(title);
		}
		return inventory.get(title);
	}
}
