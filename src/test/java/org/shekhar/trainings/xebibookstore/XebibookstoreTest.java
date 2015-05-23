package org.shekhar.trainings.xebibookstore;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class XebibookstoreTest {

	@Test
	public void givenABookInventory_WhenUserBuysABookThatExistInInventory_ThenBookShouldBeRemovedFromTheInventory() {
		XebiBookstore bookstore = new XebiBookstore("books.txt");
		String book = "Effective Java";
		bookstore.buy(book);
		boolean existsInInventory = bookstore.hasBookInInventory(book);
		assertFalse(String.format("%s is sold so it does not exist in the inventory", book), existsInInventory);
	}

}
