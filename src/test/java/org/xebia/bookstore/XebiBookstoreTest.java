package org.xebia.bookstore;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xebia.bookstore.ShoppingCart;
import org.xebia.bookstore.exceptions.BookNotInInventoryException;
import org.xebia.bookstore.exceptions.EmptyShoppingCartException;
import org.xebia.bookstore.exceptions.NotEnoughBooksInInventoryException;
import org.xebia.bookstore.model.Book;
import org.xebia.bookstore.model.DisountCoupon;
import org.xebia.bookstore.service.Inventory;
import org.xebia.bookstore.service.internal.InMemoryInventory;

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
		Inventory inventory = new InMemoryInventory();
		inventory.add(books(2));
		
		ShoppingCart cart = new ShoppingCart(inventory);
		cart.add("Effective Java");
		final int price = cart.checkout();
		assertThat(price, is(equalTo(40)));
	}

	@Test
	public void givenBookInventory_WhenUserCheckoutABookThatDoesNotExistInInventory_ThenExceptionIsThrown() throws Exception {
		Inventory inventory = new InMemoryInventory();
		inventory.add(books(2));
		ShoppingCart cart = new ShoppingCart(inventory);
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
		Inventory inventory = new InMemoryInventory();
		inventory.add(books(2));
		
		ShoppingCart cart = new ShoppingCart(inventory);
		cart.add("Effective Java");
		cart.add("OpenShift Cookbook");
		final int checkoutAmount = cart.checkout();
		assertThat(checkoutAmount, is(equalTo(95)));
	}
	
	@Test
	public void givenBookInventory_WhenUserTriesToCheckoutAnEmptyCart_ThenExceptionIsThrown() throws Exception {
		Inventory inventory = new InMemoryInventory();
		inventory.add(books(2));
		ShoppingCart cart = new ShoppingCart(inventory);
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
		Inventory inventory = new InMemoryInventory();
		inventory.add(books(5));
		
		ShoppingCart cart = new ShoppingCart(inventory);
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
	
	
	/*
	 * As a user
	 * I want to be notified when I add more copies than available in inventory
	 * So that I can remove them from the cart
	 */
	@Test
	public void givenOnlyTwoOpenShiftCookbooksInTheInventory_WhenUserAddMoreThanTwoCopiesOfOpenShiftCookbookInTheCart_ThenErrorMessageShouldBeShownToTheUser() throws Exception {
		Inventory inventory = new InMemoryInventory();
		inventory.add(books(2));
		
		ShoppingCart cart = new ShoppingCart(inventory);
		expectedException.expect(NotEnoughBooksInInventoryException.class);
		expectedException.expectMessage(is(equalTo("There are not enough copies of 'OpenShift Cookbook' in the inventory.")));

		cart.add("OpenShift Cookbook", 5);
	}
	
	
	/*
	 * As a marketing manager
	 * I want to create flat percentage discount coupons
	 * So that users can apply them during checkout and get discounted checkout price and sales improve
	 */
	@Test
	public void givenCustomerHasValidFlatPercentageDiscountCoupon_WhenCustomerAppliesTheCouponDuringCheckout_ThenDiscountIsAppliedToCheckoutAmount() throws Exception {
		Inventory inventory = new InMemoryInventory();
		inventory.add(books(5));
		
		ShoppingCart cart = new ShoppingCart(inventory);
		cart.add("Effective Java", 2);
		cart.add("OpenShift Cookbook", 3);
		
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(24);
		int amount = cart.checkout(new DisountCoupon(20, start, end));
		
		assertThat(amount, is(equalTo(196)));
	}
	
	
	
	
	private static Book[] books() {
		return books(1);
	}
	private static Book[] books(int quantity) {
		Book book1 = new Book("Effective Java", "Joshua Bloch", 40, LocalDate.of(2008, Month.MAY, 28), quantity, Arrays.asList("java", "programming"));
		Book book2 = new Book("OpenShift Cookbook", "Shekhar Gulati", 55, LocalDate.of(2014, Month.OCTOBER, 26), quantity, Arrays.asList("cloud", "programming"));
		return new Book[] { book1, book2 };
	}
	
}
