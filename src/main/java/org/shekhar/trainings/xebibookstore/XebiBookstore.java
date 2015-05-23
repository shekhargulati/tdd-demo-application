package org.shekhar.trainings.xebibookstore;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XebiBookstore {

	private final Map<String, String> booksInventory;

	public XebiBookstore(final String inventory) {
		this.booksInventory = toBooksInventory(inventory);
	}

	public String buy(String book) {
		return booksInventory.containsKey(book) ? String.format("You have successfully bought %s", book) : null;
	}

	private Map<String, String> toBooksInventory(String inventory) {
		try {
			List<String> lines = Files.readAllLines(Paths.get(inventory));
			Map<String, String> booksInventory = lines.stream().map(line -> line.split(";")).collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
			return booksInventory;
		} catch (Exception e) {
			throw new RuntimeException("Inventory not available at " + inventory);
		}
	}

	public static void main(String[] args) {
		System.out.println("Welcome to Xebibookstore");
	}

}
