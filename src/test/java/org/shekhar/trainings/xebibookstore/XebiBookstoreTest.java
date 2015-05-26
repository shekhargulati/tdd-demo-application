package org.shekhar.trainings.xebibookstore;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.shekhar.trainings.xebibookstore.domain.FileBasedInventory;
import org.shekhar.trainings.xebibookstore.domain.ShoppingCart;
import org.shekhar.trainings.xebibookstore.exceptions.BookNotInInventoryException;
import org.shekhar.trainings.xebibookstore.exceptions.EmptyShoppingCartException;

public class XebiBookstoreTest {

	private ShoppingCart cart;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setup() {
		cart = new ShoppingCart(new FileBasedInventory("src/test/resources/books.txt"));
	}

	/*
	 * ****************************** User Story 1 *************************************
	 * As a customer
	 * I want the ability to checkout a book at its actual price.
	 */
	@Test
	public void givenBookInventory_WhenUserCheckoutABookThatExistInInventory_ThenUserIsAskedToPayTheActualPrice() {
		cart.add("Effective Java",1);
		final int price = cart.checkout();
		assertThat(price, is(equalTo(30)));
	}

	@Test
	public void givenBookInventory_WhenUserCheckoutABookThatDoesNotExistInInventory_ThenExceptionIsThrown() throws Exception {
		expectedException.expect(BookNotInInventoryException.class);
		expectedException.expectMessage(equalTo("Sorry, 'Refactoring to Patterns' not in stock!!"));
		cart.add("Refactoring to Patterns");
	}
	
	
	/*
	 * ****************************** User Story 2*************************************
	 * As a customer of XebiBookstore
	 * I want the ability to add one or more books to the shopping cart
	 * So that I can checkout them at their actual base price.
	 */
	
	@Test
	public void givenBookInventory_WhenUserAddMultipleBooksThatExistInInventoryToShoppingCart_ThenUserShouldBeAskedToPaySumOfAllBookPrices() throws Exception {
		cart.add("Effective Java",1);
		cart.add("OpenShift Cookbook",1);
		final int checkoutAmount = cart.checkout();
		assertThat(checkoutAmount, is(equalTo(85)));
	}
	
	@Test
	public void givenBookInventory_WhenUserTriesToCheckoutAnEmptyCart_ThenExceptionIsThrown() throws Exception {
		expectedException.expect(EmptyShoppingCartException.class);
		expectedException.expectMessage("You can't checkout an empty cart. Please first add items to the cart.");
		cart.checkout();
	}
	
	/*
	 * ****************************** User Story 3*************************************
	 * As a customer
	 * I want the ability to add multiple quantities of a book to the shopping cart
	 * So that I do bulk buying for a book
	 */
	
	@Test
	public void givenBookInventory_WhenUserTriesToDoBulkCheckoutOfABook_ThenCheckoutAmountIsQuanityTimesBookPrice() throws Exception {
		cart.add("OpenShift Cookbook", 5);
		int checkoutAmount = cart.checkout();
		assertThat(checkoutAmount, is(equalTo(275)));
		
	}
	
	
	

}
