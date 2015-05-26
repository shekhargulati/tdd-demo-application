package org.shekhar.trainings.xebibookstore.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.shekhar.trainings.xebibookstore.exceptions.BookNotInInventoryException;

public class InMemoryInventory implements Inventory {

	private Map<String, Book> inventory = new HashMap<>();

	@Override
	public boolean exists(final String title) {
		return inventory.containsKey(title);
	}

	@Override
	public int price(String title) throws BookNotInInventoryException {
		if (!exists(title)) {
			throw new BookNotInInventoryException(title);
		}
		return inventory.get(title).getPrice();
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

	private static class BookIdGenerator {

		public static String newBookIdentifer() {
			return UUID.randomUUID().toString();
		}
	}

}
