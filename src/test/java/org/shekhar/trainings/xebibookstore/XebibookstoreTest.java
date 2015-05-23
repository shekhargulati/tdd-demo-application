package org.shekhar.trainings.xebibookstore;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.shekhar.trainings.xebibookstore.exceptions.BookNotInInventoryException;

public class XebibookstoreTest {

	/**
	 * This is the first end-to-end test which only tests how to checkout a single
	 * book at a time using a file based book inventory. First test is kept
	 * simple yet functional to make sure it delivers some value for the end
	 * customer.
	 */
	@Test
	public void givenBookInventory_WhenUserCheckoutABookThatExistInInventory_ThenUserIsAskedToPayTheActualPrice() {
		XebiBookstore bookstore = new XebiBookstore("src/test/resources/books.txt");
		String book = "Effective Java";
		int price = bookstore.checkout(book);
		assertThat(price, is(equalTo(30)));
	}

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void givenBookInventory_WhenUserCheckoutABookThatDoesNotExistInInventory_ThenExceptionIsThrown() throws Exception {
		XebiBookstore bookstore = new XebiBookstore("src/test/resources/books.txt");
		String book = "Refactoring to Patterns";

		expectedException.expect(BookNotInInventoryException.class);
		expectedException.expectMessage(equalTo("Sorry, 'Refactoring to Patterns' not in stock!!"));
		bookstore.checkout(book);
	}

}
