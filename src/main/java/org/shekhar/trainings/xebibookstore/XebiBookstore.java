package org.shekhar.trainings.xebibookstore;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.shekhar.trainings.xebibookstore.exceptions.BookNotInInventoryException;

public class XebiBookstore {

	private final Map<String, Integer> booksInventory;

	public XebiBookstore(final String inventory) {
		this.booksInventory = toBooksInventory(inventory);
	}

	public int checkout(String book) throws BookNotInInventoryException {
		if (booksInventory.containsKey(book)) {
			return booksInventory.get(book);
		}
		throw new BookNotInInventoryException(book);
	}

	private Map<String, Integer> toBooksInventory(String inventory) {
		try {
			List<String> lines = Files.readAllLines(Paths.get(inventory));
			Map<String, Integer> booksInventory = lines.stream().map(line -> line.split(";")).collect(Collectors.toMap(kv -> kv[0], kv -> Integer.parseInt(kv[1])));
			return booksInventory;
		} catch (Exception e) {
			throw new RuntimeException("Inventory not available at " + inventory);
		}
	}

	public static void main(String[] args) {
		System.out.println("************Welcome to Xebibookstore***********");
		if (args == null || args.length < 2) {
			throw new IllegalArgumentException("Please provide values for inventory file and book name.");
		}
		final String inventory = args[0];
		final String bookToBuy = args[1];
		XebiBookstore bookstore = new XebiBookstore(inventory);
		int price = bookstore.checkout(bookToBuy);
		System.out.println(price);

	}
}
