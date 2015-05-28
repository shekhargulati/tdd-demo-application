package org.shekhar.trainings.xebibookstore.domain;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.shekhar.trainings.xebibookstore.domain.exceptions.BookNotInInventoryException;

public class FileBasedInventory implements Inventory {

	private final Map<String, Integer> booksInventory;

	public FileBasedInventory(String inventory) {
		this.booksInventory = toBooksInventory(inventory);
	}

	@Override
	public boolean exists(String book) {
		return booksInventory.containsKey(book);
	}

	@Override
	public int price(String book) throws BookNotInInventoryException {
		if (booksInventory.containsKey(book)) {
			return booksInventory.get(book);
		}
		throw new BookNotInInventoryException(book);
	}

	private Map<String, Integer> toBooksInventory(String inventory) {
		try {
			List<String> lines = Files.readAllLines(Paths.get(inventory));
			Map<String, Integer> booksInventory = lines.stream().map(line -> line.split(";")).collect(Collectors.toMap(bookNameAndPrice -> bookNameAndPrice[0], kv -> Integer.parseInt(kv[1])));
			return booksInventory;
		} catch (Exception e) {
			throw new RuntimeException("Inventory not available at " + inventory);
		}
	}

	@Override
	public String[] add(Book... books) {
		throw new UnsupportedOperationException("You can't add books to File based inventory. Please add books to the inventory file.");
	}

	@Override
	public String add(Book book) {
		throw new UnsupportedOperationException("You can't add books to File based inventory. Please add books to the inventory file.");
	}
}
