package org.shekhar.trainings.xebibookstore.domain;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.shekhar.trainings.xebibookstore.exceptions.BookNotInInventoryException;

public class FileBasedInventoryManager implements InventoryManager {

	private final Map<String, Integer> booksInventory;

	public FileBasedInventoryManager(String inventory) {
		this.booksInventory = toBooksInventory(inventory);
	}

	@Override
	public boolean exists(String book) {
		return booksInventory.containsKey(book);
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

	@Override
	public int bookPrice(String book) throws BookNotInInventoryException {
		if(booksInventory.containsKey(book)){
			return booksInventory.get(book);			
		}
		throw new BookNotInInventoryException(book);
	}
	
}
