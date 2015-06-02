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
import org.xebia.bookstore.exceptions.BookNotInInventoryException;
import org.xebia.bookstore.exceptions.EmptyShoppingCartException;
import org.xebia.bookstore.exceptions.NotEnoughBooksInInventoryException;
import org.xebia.bookstore.model.Book;
import org.xebia.bookstore.model.CashDiscountCoupon;
import org.xebia.bookstore.model.PercentageDiscountCoupon;
import org.xebia.bookstore.service.DiscountService;
import org.xebia.bookstore.service.Inventory;
import org.xebia.bookstore.service.internal.InMemoryDiscountService;
import org.xebia.bookstore.service.internal.InMemoryInventory;

public class XebiBookstoreTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private final DiscountService discountService = new InMemoryDiscountService();
	private final Inventory inventory = new InMemoryInventory();
	private final ShoppingCart cart = new ShoppingCart(inventory, discountService);

	/*
	 * ****************************** User Story 1 *************************************
	 * As a customer
	 * I want the ability to checkout a book at its actual price.
	 */
	@Test
	public void givenBookInventory_WhenUserCheckoutABookThatExistInInventory_ThenUserIsAskedToPayTheActualPrice() {
		inventory.add(books(2));

		cart.add("Effective Java");
		final int price = cart.checkout();
		assertThat(price, is(equalTo(40)));
	}

	@Test
	public void givenBookInventory_WhenUserCheckoutABookThatDoesNotExistInInventory_ThenExceptionIsThrown() throws Exception {
		inventory.add(books(2));
		
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
		inventory.add(books(2));
		
		cart.add("Effective Java");
		cart.add("OpenShift Cookbook");
		
		final int checkoutAmount = cart.checkout();
		assertThat(checkoutAmount, is(equalTo(95)));
	}
	
	@Test
	public void givenBookInventory_WhenUserTriesToCheckoutAnEmptyCart_ThenExceptionIsThrown() throws Exception {
		inventory.add(books(2));
		
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
		inventory.add(books(5));
		
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
		inventory.add(books());
		
		cart.add("OpenShift Cookbook");
		int checkoutAmount = cart.checkout();
		assertThat(checkoutAmount, is(equalTo(55)));
		
		inventory.add(new Book("Refacting to Patterns", "Josua Kerievsky", 50, LocalDate.of(2005, Month.FEBRUARY, 22), 1, Collections.emptyList()));
		
		cart.add("Refacting to Patterns");
		checkoutAmount = cart.checkout();
		assertThat(checkoutAmount, is(equalTo(105)));
	}
	
	
	/*
	 * ****************************** User Story 5 *************************************
	 * As a user
	 * I want to be notified when I add more copies than available in inventory
	 * So that I can remove them from the cart
	 */
	@Test
	public void givenOnlyTwoOpenShiftCookbooksInTheInventory_WhenUserAddMoreThanTwoCopiesOfOpenShiftCookbookInTheCart_ThenErrorMessageShouldBeShownToTheUser() throws Exception {
		inventory.add(books(2));
		
		expectedException.expect(NotEnoughBooksInInventoryException.class);
		expectedException.expectMessage(is(equalTo("There are not enough copies of 'OpenShift Cookbook' in the inventory.")));

		cart.add("OpenShift Cookbook", 5);
	}
	
	
	/*
	 * ****************************** User Story 6 *************************************
	 * As a marketing manager
	 * I want to create flat percentage discount coupons
	 * So that users can apply them during checkout and get discounted checkout price
	 */
	@Test
	public void givenCustomerHasValidFlatPercentageDiscountCoupon_WhenCustomerAppliesTheCouponDuringCheckout_ThenDiscountIsAppliedToCheckoutAmount() throws Exception {
		inventory.add(books(5));
		
		cart.add("Effective Java", 2);
		cart.add("OpenShift Cookbook", 3);
		
		String couponCode = createFlatPercentageDiscountCoupon(20);
		
		int amount = cart.checkout(couponCode);
		
		assertThat(amount, is(equalTo(196)));
	}

	
	/*
	 * ****************************** User Story 7 *************************************
	 * As a marketing manager
	 * I want to create flat cash discount coupon
	 * So that users can apply them during checkout and get discounted checkout price
	 */
	@Test
	public void givenCustomerHasValidFlatCashDiscountCoupon_WhenCustomerAppliesTheCouponDuringCheckout_ThenCheckoutAmountIsReducedByFlatCashDiscountCouponAmount() throws Exception {
		inventory.add(books(5));
		
		cart.add("Effective Java", 2);
		cart.add("OpenShift Cookbook", 3);
		
		String couponCode = createFlatCashDiscountCoupon(20);
		
		int amount = cart.checkout(couponCode);
		
		assertThat(amount, is(equalTo(225)));
	}
	
	@Test
	public void givenCustomerHasValidFlatCashDiscountCoupon_WhenCheckoutAmountAfterApplyingDiscountCouponIsLessThan60PercentOfCheckoutAmount_ThenErrorMessageIsShownToTheUser() throws Exception {
		inventory.add(books(5));

		cart.add("Effective Java", 1);

		String couponCode = createFlatCashDiscountCoupon(30);
		
		expectedException.expectMessage(is(equalTo("This coupon is not applicable for this checkout amount.")));
		
		cart.checkout(couponCode);
	}
	
	/*
	 * ****************************** User Story 8 *************************************
	 * As a marketing manager
	 * I want to create discount(cash or percentage) coupons applicable for specific book categories
	 * So that users can apply them during checkout and get discounted checkout price
	 */
	
	@Test
	public void givenCustomerHasValidPercentageDiscountCouponApplicableToJavaAndDevOpsCategories_WhenCustomerAppliesToACartWithFewBooksInApplicableCategories_ThenDiscountIsApplied() throws Exception {
		inventory.add(books(5));

		cart.add("Effective Java", 2);
		cart.add("Learning Chef", 1);
		cart.add("Eloquent JavaScript", 1);
		cart.add("JavaScript -- The Good Parts", 1);

		String couponCode = createFlatPercentageDiscountCoupon(10, "devops", "java");

		int amount = cart.checkout(couponCode);

		assertThat(amount, is(equalTo(157)));
	}

	@Test
	public void givenCustomerHasValidCashDiscountCouponApplicableToScalaCategory_WhenCustomerAppliesToACartWithBooksNotInApplicableCategories_ThenNoDiscountIsApplied() throws Exception {
		inventory.add(books(5));

		cart.add("Effective Java", 2);
		cart.add("Learning Chef", 1);
		cart.add("Eloquent JavaScript", 1);
		cart.add("JavaScript -- The Good Parts", 1);

		String couponCode = createFlatCashDiscountCoupon(10, "scala");

		int amount = cart.checkout(couponCode);

		assertThat(amount, is(equalTo(170)));
	}

	@Test
	public void givenCustomerHasValidCashDiscountCouponApplicableToJavaAndDevOpsCategories_WhenCustomerAppliesToACartWithFewBooksInApplicableCategories_ThenDiscountIsApplied() throws Exception {
		inventory.add(books(5));

		cart.add("Effective Java", 2);
		cart.add("Learning Chef", 1);
		cart.add("Eloquent JavaScript", 1);
		cart.add("JavaScript -- The Good Parts", 1);

		String couponCode = createFlatCashDiscountCoupon(50, "devops", "java");

		int amount = cart.checkout(couponCode);

		assertThat(amount, is(equalTo(120)));
	}
	
	
	//*************************** Utility methods*********************************************//
	
	private String createFlatCashDiscountCoupon(int amount, String... categories) {
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(24);
		String couponCode = discountService.create(new CashDiscountCoupon(amount, start, end));
		return couponCode;
	}

	private static Book[] books() {
		return books(1);
	}

	private static Book[] books(int quantity) {
		Book book1 = new Book("Effective Java", "Joshua Bloch", 40, LocalDate.of(2008, Month.MAY, 28), quantity, Arrays.asList("java", "programming"));
		Book book2 = new Book("Head First Java", "Kathy Siera", 50, LocalDate.of(2008, Month.MAY, 28), quantity, Arrays.asList("java", "programming"));
		Book book3 = new Book("OpenShift Cookbook", "Shekhar Gulati", 55, LocalDate.of(2014, Month.OCTOBER, 26), quantity, Arrays.asList("cloud", "programming"));
		Book book4 = new Book("Learning Chef", "Mischa Taylor", 50, LocalDate.of(2014, Month.OCTOBER, 26), quantity, Arrays.asList("cloud", "devops"));
		Book book5 = new Book("Eloquent JavaScript", "Marjin Haverbeke", 10, LocalDate.of(2014, Month.OCTOBER, 26), quantity, Arrays.asList("javascript", "programming"));
		Book book6 = new Book("JavaScript -- The Good Parts", "Douglas Crockford", 30, LocalDate.of(2014, Month.OCTOBER, 26), quantity, Arrays.asList("javascript", "programming"));
		return new Book[] { book1, book2, book3, book4, book5, book6 };
	}

	private String createFlatPercentageDiscountCoupon(int discount, String... categories) {
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(24);
		String couponCode = discountService.create(new PercentageDiscountCoupon(discount, start, end));
		return couponCode;
	}
	
}
