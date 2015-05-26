package org.shekhar.trainings.xebibookstore;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.shekhar.trainings.xebibookstore.exceptions.BookNotInInventoryException;
import org.shekhar.trainings.xebibookstore.exceptions.EmptyShoppingCartException;

public class XebiBookstoreTest {

	private XebiBookstore bookstore;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setup() {
		bookstore = new XebiBookstore("src/test/resources/books.txt");
	}

	/*
	 * ****************************** User Story 1 *************************************
	 * As a customer
	 * I want the ability to checkout a book at its actual price.
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
	
	
	/*
	 * ****************************** User Story 2*************************************
	 * As a customer of XebiBookstore
	 * I want the ability to add one or more books to the shopping cart
	 * So that I can checkout them at their actual base price.
	 */
	
	@Test
	public void givenBookInventory_WhenUserAddMultipleBooksThatExistInInventoryToShoppingCart_ThenUserShouldBeAskedToPaySumOfAllBookPrices() throws Exception {
		final int checkoutAmount = bookstore.checkout("Effective Java","OpenShift Cookbook");
		assertThat(checkoutAmount, is(equalTo(85)));
	}
	
	@Test
	public void givenBookInventory_WhenUserTriesToCheckoutAnEmptyCart_ThenExceptionIsThrown() throws Exception {
		expectedException.expect(EmptyShoppingCartException.class);
		expectedException.expectMessage("You can't checkout an empty cart. Please first add items to the cart.");
		bookstore.checkout();
	}
	
	/*
	 * ****************************** User Story 3*************************************
	 * As a customer
	 * I want the ability to add multiple quantities of a book to the shopping cart
	 * So that I do bulk buying for a book
	 */
	
	@Test
	public void givenBookInventory_WhenUserTriesToDoBulkCheckoutOfABook_ThenCheckoutAmountIsQuanityTimesBookPrice() throws Exception {
		String[] books = new String[2];
		Arrays.fill(books, "OpenShift Cookbook");
		int checkoutAmount = bookstore.checkout(books);
		assertThat(checkoutAmount, is(equalTo(110)));
		
	}
	
	
	

}
