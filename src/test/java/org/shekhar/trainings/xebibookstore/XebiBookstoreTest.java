package org.shekhar.trainings.xebibookstore;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.shekhar.trainings.xebibookstore.domain.Book;
import org.shekhar.trainings.xebibookstore.domain.FileBasedInventory;
import org.shekhar.trainings.xebibookstore.domain.InMemoryInventory;
import org.shekhar.trainings.xebibookstore.domain.Inventory;
import org.shekhar.trainings.xebibookstore.domain.ShoppingCart;
import org.shekhar.trainings.xebibookstore.exceptions.BookNotInInventoryException;
import org.shekhar.trainings.xebibookstore.exceptions.EmptyShoppingCartException;

public class XebiBookstoreTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/*
	 * ****************************** User Story 1 *************************************
	 * As a customer
	 * I want the ability to checkout a book at its actual price.
	 */
	@Test
	public void givenBookInventory_WhenUserCheckoutABookThatExistInInventory_ThenUserIsAskedToPayTheActualPrice() {
		ShoppingCart cart = new ShoppingCart(new FileBasedInventory("src/test/resources/books.txt"));
		cart.add("Effective Java");
		final int price = cart.checkout();
		assertThat(price, is(equalTo(30)));
	}

	@Test
	public void givenBookInventory_WhenUserCheckoutABookThatDoesNotExistInInventory_ThenExceptionIsThrown() throws Exception {
		ShoppingCart cart = new ShoppingCart(new FileBasedInventory("src/test/resources/books.txt"));
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
		ShoppingCart cart = new ShoppingCart(new FileBasedInventory("src/test/resources/books.txt"));
		cart.add("Effective Java");
		cart.add("OpenShift Cookbook");
		final int checkoutAmount = cart.checkout();
		assertThat(checkoutAmount, is(equalTo(85)));
	}
	
	@Test
	public void givenBookInventory_WhenUserTriesToCheckoutAnEmptyCart_ThenExceptionIsThrown() throws Exception {
		ShoppingCart cart = new ShoppingCart(new FileBasedInventory("src/test/resources/books.txt"));
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
		ShoppingCart cart = new ShoppingCart(new FileBasedInventory("src/test/resources/books.txt"));
		cart.add("OpenShift Cookbook", 5);
		int checkoutAmount = cart.checkout();
		assertThat(checkoutAmount, is(equalTo(275)));
	}
	
	
	/*
	 * ************************************User Story 4**********************************
	 * As a store supervisor
	 * I want the ability to add book(s) to the inventory
	 */
	@Test
	public void givenBooks_WhenSupervisorAddBooksToTheInventory_ThenUserCanCheckoutThem() throws Exception {
		Inventory inventory = new InMemoryInventory();
		inventory.add(books());
		
		ShoppingCart cart = new ShoppingCart(inventory);
		
		cart.add("OpenShift Cookbook");
		int checkoutAmount = cart.checkout();
		assertThat(checkoutAmount, is(equalTo(55)));
		
		inventory.add(new Book("Refacting to Patterns", "Josua Kerievsky", 50, LocalDate.of(2005, Month.FEBRUARY, 22), 1, Collections.emptyList()));
		
		cart.add("Refacting to Patterns");
		checkoutAmount = cart.checkout();
		assertThat(checkoutAmount, is(equalTo(105)));
	}
	
	private static Book[] books() {
		Book book1 = new Book("Effective Java", "Joshua Bloch", 40, LocalDate.of(2008, Month.MAY, 28), 1, Arrays.asList("java", "programming"));
		Book book2 = new Book("OpenShift Cookbook", "Shekhar Gulati", 55, LocalDate.of(2014, Month.OCTOBER, 26), 1, Arrays.asList("cloud", "programming"));
		return new Book[] { book1, book2 };
	}
	
}
