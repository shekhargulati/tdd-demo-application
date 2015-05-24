package org.shekhar.trainings.xebibookstore;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.shekhar.trainings.xebibookstore.exceptions.BookNotInInventoryException;

public class XebibookstoreTest {

	private XebiBookstore bookstore;
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setup() {
		bookstore = new XebiBookstore("src/test/resources/books.txt");
	}

	/**
	 * This is the first end-to-end test which only tests how to checkout a
	 * single book at a time using a file based book inventory. First test is
	 * kept simple yet functional to make sure it delivers some value for the
	 * end customer.
	 */
	@Test
	public void givenBookInventory_WhenUserCheckoutABookThatExistInInventory_ThenUserIsAskedToPayTheActualPrice() {
		final String book = "Effective Java";
		final int price = bookstore.checkout(book);
		assertThat(price, is(equalTo(30)));
	}

	@Test
	public void givenBookInventory_WhenUserCheckoutABookThatDoesNotExistInInventory_ThenExceptionIsThrown() throws Exception {
		final String book = "Refactoring to Patterns";
		expectedException.expect(BookNotInInventoryException.class);
		expectedException.expectMessage(equalTo("Sorry, 'Refactoring to Patterns' not in stock!!"));
		bookstore.checkout(book);
	}

}
