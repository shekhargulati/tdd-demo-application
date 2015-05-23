package org.shekhar.trainings.xebibookstore;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class XebibookstoreTest {

	/**
	 * This is the first end-to-end test which only tests how to buy a single
	 * book at a time using a file based book inventory. First test is kept
	 * simple yet functional to make sure it delivers some value for the end
	 * customer.
	 */
	@Test
	public void givenABookInventory_WhenUserBuysABookThatExistInInventory_ThenASuccessResponseIsShownToTheUser() {
		XebiBookstore bookstore = new XebiBookstore("src/test/resources/books.txt");
		String book = "Effective Java";
		String response = bookstore.buy(book);
		assertThat(response, is(equalTo(String.format("You have successfully bought %s", book))));
	}

}
