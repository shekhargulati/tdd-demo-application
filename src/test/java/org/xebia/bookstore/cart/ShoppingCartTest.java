package org.xebia.bookstore.cart;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.xebia.bookstore.matchers.IsShoppingCartWithMaxSize.constrainMaxItemsInCart;
import static org.xebia.bookstore.matchers.IsShoppingCartWithSize.hasSize;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import org.hamcrest.collection.IsMapContaining;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xebia.bookstore.cart.ShoppingCart;
import org.xebia.bookstore.exceptions.BookNotInInventoryException;
import org.xebia.bookstore.exceptions.ExpiredDisountCouponException;
import org.xebia.bookstore.exceptions.InvalidDiscountCouponException;
import org.xebia.bookstore.exceptions.NotEnoughBooksInInventoryException;
import org.xebia.bookstore.model.Book;
import org.xebia.bookstore.model.CashDiscountCoupon;
import org.xebia.bookstore.model.PercentageDiscountCoupon;
import org.xebia.bookstore.service.DiscountService;
import org.xebia.bookstore.service.Inventory;

public class ShoppingCartTest {

	private final Inventory inventory = mock(Inventory.class);
	private final DiscountService discountService = mock(DiscountService.class);
	private final ShoppingCart cart = new ShoppingCart(inventory, discountService);

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void canAddMultipleBooksToTheShoppingCart() throws Exception {
		when(inventory.exists("Effective Java")).thenReturn(true);
		when(inventory.exists("OpenShift Cookbook")).thenReturn(true);

		when(inventory.hasEnoughCopies(anyString(), anyInt())).thenReturn(true);

		cart.add("Effective Java");
		cart.add("OpenShift Cookbook");

		assertThat(cart, hasSize(2));
		assertThat(cart, constrainMaxItemsInCart(10));

		verify(inventory, times(1)).exists("Effective Java");
		verify(inventory, times(1)).exists("OpenShift Cookbook");
		verify(inventory, times(2)).hasEnoughCopies(anyString(), anyInt());
		verifyNoMoreInteractions(inventory);
	}

	@Test
	public void canAddMultipleBooksToTheShoppingCartInOneGo() throws Exception {
		when(inventory.exists(anyString())).thenReturn(true);
		when(inventory.hasEnoughCopies(anyString(), anyInt())).thenReturn(true);

		cart.add("Effective Java", "OpenShift Cookbook", "Java Concurrency in Practice");

		assertThat(cart.items().size(), equalTo(3));
		assertThat(cart.items(), IsMapContaining.hasEntry(equalTo("Effective Java"), equalTo(1)));

		verify(inventory, times(3)).exists(anyString());
		verify(inventory, times(3)).hasEnoughCopies(anyString(), anyInt());
		verifyNoMoreInteractions(inventory);

	}

	@Test
	public void throwExceptionWhenBookAddedToTheCartDoesNotExistInInventory() throws Exception {
		String book = "Effective Java";
		when(inventory.exists(book)).thenReturn(false);

		expectedException.expect(BookNotInInventoryException.class);
		expectedException.expectMessage(equalTo("Sorry, 'Effective Java' not in stock!!"));

		cart.add(book);
	}

	@Test
	public void cartAmountIsEqualToSumOfAllItemPrices() throws Exception {
		when(inventory.exists(anyString())).thenReturn(true);
		when(inventory.hasEnoughCopies(anyString(), anyInt())).thenReturn(true);
		
		cart.add("OpenShift Cookbook", "Effective Java", "Clean Code");
		verify(inventory, times(3)).exists(anyString());

		when(inventory.find("OpenShift Cookbook")).thenReturn(openshiftCookbook());
		when(inventory.find("Effective Java")).thenReturn(effectiveJava());
		when(inventory.find("Clean Code")).thenReturn(cleanCodeBook());

		int cartAmount = cart.checkout();

		assertThat(cartAmount, is(equalTo(135)));

		verify(inventory, times(3)).find(anyString());
		verify(inventory, times(3)).hasEnoughCopies(anyString(), anyInt());
		verifyNoMoreInteractions(inventory);
	}

	@Test
	public void canAddMultipleQuantiesOfBook() throws Exception {
		when(inventory.exists("Effective Java")).thenReturn(true);

		when(inventory.hasEnoughCopies("Effective Java", 10)).thenReturn(true);
		cart.add("Effective Java", 10);

		assertThat(cart.size(), is(equalTo(10)));

		verify(inventory, times(1)).exists("Effective Java");
		verify(inventory, times(1)).hasEnoughCopies("Effective Java", 10);
		verifyNoMoreInteractions(inventory);
	}

	@Test
	public void canCheckoutMultipleQuantiesOfBook() throws Exception {
		when(inventory.exists(anyString())).thenReturn(true);
		when(inventory.hasEnoughCopies("OpenShift Cookbook", 2)).thenReturn(true);
		when(inventory.hasEnoughCopies("Effective Java", 5)).thenReturn(true);
		when(inventory.hasEnoughCopies("Clean Code", 10)).thenReturn(true);

		cart.add("OpenShift Cookbook", 2);
		cart.add("Effective Java", 5);
		cart.add("Clean Code", 10);

		verify(inventory, times(3)).exists(anyString());

		when(inventory.find("OpenShift Cookbook")).thenReturn(openshiftCookbook());
		when(inventory.find("Effective Java")).thenReturn(effectiveJava());
		when(inventory.find("Clean Code")).thenReturn(cleanCodeBook());


		int cartAmount = cart.checkout();

		assertThat(cartAmount, is(equalTo(710)));

		verify(inventory, times(3)).find(anyString());
		verify(inventory, times(1)).hasEnoughCopies("OpenShift Cookbook", 2);
		verify(inventory, times(1)).hasEnoughCopies("Effective Java", 5);
		verify(inventory, times(1)).hasEnoughCopies("Clean Code", 10);
		verifyNoMoreInteractions(inventory);
	}

	@Test
	public void throwExceptionWhenMoreItemsAreAddedToTheCartThanAvailableInInventory() throws Exception {
		when(inventory.exists("OpenShift Cookbook")).thenReturn(true);
		when(inventory.hasEnoughCopies("OpenShift Cookbook", 5)).thenReturn(false);

		expectedException.expect(NotEnoughBooksInInventoryException.class);
		expectedException.expectMessage(is(equalTo("There are not enough copies of 'OpenShift Cookbook' in the inventory.")));

		cart.add("OpenShift Cookbook", 5);
		verify(inventory, times(1)).exists("OpenShift Cookbook");
		verify(inventory, times(1)).hasEnoughCopies("OpenShift Cookbook", 5);
		verifyNoMoreInteractions(inventory);
	}

	@Test
	public void applyDiscountWhenAValidFlatPercentageDisountCouponIsUsedDuringCheckout() throws Exception {
		when(inventory.exists(anyString())).thenReturn(true);
		when(inventory.hasEnoughCopies("OpenShift Cookbook", 3)).thenReturn(true);
		when(inventory.hasEnoughCopies("Effective Java", 2)).thenReturn(true);

		cart.add("OpenShift Cookbook", 3);
		cart.add("Effective Java", 2);

		verify(inventory, times(2)).exists(anyString());

		when(inventory.find("OpenShift Cookbook")).thenReturn(openshiftCookbook());
		when(inventory.find("Effective Java")).thenReturn(effectiveJava());


		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(24);

		String couponCode = "valid_discount_coupon";

		when(discountService.find(couponCode)).thenReturn(new PercentageDiscountCoupon(20, start, end));

		int cartAmount = cart.checkout(couponCode);

		assertThat(cartAmount, is(equalTo(196)));

		verify(inventory, times(2)).find(anyString());
		verify(inventory, times(1)).hasEnoughCopies("OpenShift Cookbook", 3);
		verify(inventory, times(1)).hasEnoughCopies("Effective Java", 2);
		verify(discountService, times(1)).find(couponCode);
		verifyNoMoreInteractions(inventory, discountService);
	}

	@Test
	public void throwExceptionWhenExpiredDisountCouponIsUsedDuringCheckout() throws Exception {
		when(inventory.exists(anyString())).thenReturn(true);
		when(inventory.hasEnoughCopies("OpenShift Cookbook", 3)).thenReturn(true);
		when(inventory.hasEnoughCopies("Effective Java", 2)).thenReturn(true);

		cart.add("OpenShift Cookbook", 3);
		cart.add("Effective Java", 2);

		verify(inventory, times(2)).exists(anyString());
		verify(inventory, times(1)).hasEnoughCopies("OpenShift Cookbook", 3);
		verify(inventory, times(1)).hasEnoughCopies("Effective Java", 2);
		verifyNoMoreInteractions(inventory);

		LocalDateTime start = LocalDateTime.now().minusDays(2);
		LocalDateTime end = start.plusHours(24);

		String couponCode = "expired_discount_coupon";

		when(discountService.find(couponCode)).thenReturn(new PercentageDiscountCoupon(20, start, end));

		expectedException.expect(isA(ExpiredDisountCouponException.class));
		expectedException.expectMessage(is(equalTo("Sorry, the coupon code has expired.")));
		cart.checkout("expired_discount_coupon");
	}

	@Test
	public void throwExceptionWhenCouponCodeDoesNotExist() throws Exception {
		when(inventory.exists(anyString())).thenReturn(true);
		when(inventory.hasEnoughCopies("OpenShift Cookbook", 3)).thenReturn(true);
		when(inventory.hasEnoughCopies("Effective Java", 2)).thenReturn(true);

		cart.add("OpenShift Cookbook", 3);
		cart.add("Effective Java", 2);

		verify(inventory, times(2)).exists(anyString());
		verify(inventory, times(1)).hasEnoughCopies("OpenShift Cookbook", 3);
		verify(inventory, times(1)).hasEnoughCopies("Effective Java", 2);
		verifyNoMoreInteractions(inventory);

		String couponCode = "invalid_discount_coupon";

		when(discountService.find(couponCode)).thenThrow(new InvalidDiscountCouponException("Sorry, the coupon code you entered does not exist."));

		expectedException.expect(isA(InvalidDiscountCouponException.class));
		expectedException.expectMessage(is(equalTo("Sorry, the coupon code you entered does not exist.")));

		cart.checkout(couponCode);
	}

	@Test
	public void applyDiscountWhenAValidFlatCashDisountCouponIsUsedDuringCheckout() throws Exception {
		when(inventory.exists(anyString())).thenReturn(true);
		when(inventory.hasEnoughCopies("OpenShift Cookbook", 3)).thenReturn(true);
		when(inventory.hasEnoughCopies("Effective Java", 2)).thenReturn(true);

		cart.add("OpenShift Cookbook", 3);
		cart.add("Effective Java", 2);

		verify(inventory, times(2)).exists(anyString());

		when(inventory.find("OpenShift Cookbook")).thenReturn(openshiftCookbook());
		when(inventory.find("Effective Java")).thenReturn(effectiveJava());


		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(24);

		String couponCode = "valid_discount_coupon";

		when(discountService.find(couponCode)).thenReturn(new CashDiscountCoupon(20, start, end));
		int cartAmount = cart.checkout(couponCode);

		assertThat(cartAmount, is(equalTo(225)));

		verify(inventory, times(2)).find(anyString());
		verify(inventory, times(1)).hasEnoughCopies("OpenShift Cookbook", 3);
		verify(inventory, times(1)).hasEnoughCopies("Effective Java", 2);
		verify(discountService, times(1)).find(couponCode);
		verifyNoMoreInteractions(inventory, discountService);
	}

	@Test
	public void throwExceptionWhenCheckoutAmountAfterApplyingCashDiscountIsLessThan60PercentOfTotalCheckoutAmount() throws Exception {
		when(inventory.exists(anyString())).thenReturn(true);
		when(inventory.hasEnoughCopies("OpenShift Cookbook", 1)).thenReturn(true);

		cart.add("OpenShift Cookbook", 1);

		verify(inventory, times(1)).exists(anyString());

		when(inventory.find("OpenShift Cookbook")).thenReturn(openshiftCookbook());


		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(24);

		String couponCode = "valid_discount_coupon";

		when(discountService.find(couponCode)).thenReturn(new CashDiscountCoupon(40, start, end));
		expectedException.expect(isA(InvalidDiscountCouponException.class));
		expectedException.expectMessage(is(equalTo("This coupon is not applicable for this checkout amount.")));
		cart.checkout(couponCode);
	}

	@Test
	public void percentageDiscountIsAppliedToCheckoutAmountWhenCartHasBooksInCategoriesDiscountCouponIsApplicable() throws Exception {
		when(inventory.exists(anyString())).thenReturn(true);
		when(inventory.hasEnoughCopies("Effective Java", 2)).thenReturn(true);
		when(inventory.hasEnoughCopies("Learning Chef", 1)).thenReturn(true);
		when(inventory.hasEnoughCopies("Eloquent JavaScript", 1)).thenReturn(true);

		cart.add("Effective Java", 2);
		cart.add("Learning Chef", 1);
		cart.add("Eloquent JavaScript", 1);

		verify(inventory, times(3)).exists(anyString());
		verify(inventory, times(1)).hasEnoughCopies("Effective Java", 2);
		verify(inventory, times(1)).hasEnoughCopies("Learning Chef", 1);
		verify(inventory, times(1)).hasEnoughCopies("Eloquent JavaScript", 1);

		when(inventory.price("Effective Java")).thenReturn(40);
		when(inventory.price("Learning Chef")).thenReturn(50);
		when(inventory.price("Eloquent JavaScript")).thenReturn(10);

		when(inventory.find("Effective Java")).thenReturn(effectiveJava());
		when(inventory.find("Learning Chef")).thenReturn(new Book("Learning Chef", "Mischa Taylor", 50, LocalDate.of(2014, Month.OCTOBER, 26), 10, Arrays.asList("cloud", "devops")));
		when(inventory.find("Eloquent JavaScript")).thenReturn(new Book("Eloquent JavaScript", "Marjin Haverbeke", 10, LocalDate.of(2014, Month.OCTOBER, 26), 10, Arrays.asList("javascript", "programming")));

		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(24);

		String couponCode = "valid_discount_coupon";

		when(discountService.find(couponCode)).thenReturn(new PercentageDiscountCoupon(20, start, end, Arrays.asList("java", "devops")));

		int cartAmount = cart.checkout(couponCode);

		assertThat(cartAmount, is(equalTo(114)));

		verify(inventory, times(3)).find(anyString());
		verify(inventory, times(3)).find(anyString());
		verify(discountService, times(1)).find(couponCode);
		verifyNoMoreInteractions(inventory, discountService);

	}

	@Test
	public void percentageDiscountIsNotAppliedToCheckoutAmountWhenCartHasBooksInCategoriesDiscountCouponIsNotApplicable() throws Exception {
		when(inventory.exists(anyString())).thenReturn(true);
		when(inventory.hasEnoughCopies("Effective Java", 2)).thenReturn(true);
		when(inventory.hasEnoughCopies("Learning Chef", 1)).thenReturn(true);
		when(inventory.hasEnoughCopies("Eloquent JavaScript", 1)).thenReturn(true);

		cart.add("Effective Java", 2);
		cart.add("Learning Chef", 1);
		cart.add("Eloquent JavaScript", 1);

		verify(inventory, times(3)).exists(anyString());
		verify(inventory, times(1)).hasEnoughCopies("Effective Java", 2);
		verify(inventory, times(1)).hasEnoughCopies("Learning Chef", 1);
		verify(inventory, times(1)).hasEnoughCopies("Eloquent JavaScript", 1);

		when(inventory.price("Effective Java")).thenReturn(40);
		when(inventory.price("Learning Chef")).thenReturn(50);
		when(inventory.price("Eloquent JavaScript")).thenReturn(10);

		when(inventory.find("Effective Java")).thenReturn(effectiveJava());
		when(inventory.find("Learning Chef")).thenReturn(new Book("Learning Chef", "Mischa Taylor", 50, LocalDate.of(2014, Month.OCTOBER, 26), 10, Arrays.asList("cloud", "devops")));
		when(inventory.find("Eloquent JavaScript")).thenReturn(new Book("Eloquent JavaScript", "Marjin Haverbeke", 10, LocalDate.of(2014, Month.OCTOBER, 26), 10, Arrays.asList("javascript", "programming")));

		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(24);

		String couponCode = "valid_discount_coupon";

		when(discountService.find(couponCode)).thenReturn(new PercentageDiscountCoupon(20, start, end, Arrays.asList("scala")));

		int cartAmount = cart.checkout(couponCode);

		assertThat(cartAmount, is(equalTo(140)));

		verify(inventory, times(3)).find(anyString());
		verify(discountService, times(1)).find(couponCode);
		verifyNoMoreInteractions(inventory, discountService);
	}

	@Test
	public void cashDiscountIsNotAppliedToCheckoutAmountWhenCartHasBooksInCategoriesDiscountCouponIsNotApplicable() throws Exception {
		when(inventory.exists(anyString())).thenReturn(true);
		when(inventory.hasEnoughCopies("Effective Java", 2)).thenReturn(true);
		when(inventory.hasEnoughCopies("Learning Chef", 1)).thenReturn(true);
		when(inventory.hasEnoughCopies("Eloquent JavaScript", 1)).thenReturn(true);

		cart.add("Effective Java", 2);
		cart.add("Learning Chef", 1);
		cart.add("Eloquent JavaScript", 1);

		verify(inventory, times(3)).exists(anyString());
		verify(inventory, times(1)).hasEnoughCopies("Effective Java", 2);
		verify(inventory, times(1)).hasEnoughCopies("Learning Chef", 1);
		verify(inventory, times(1)).hasEnoughCopies("Eloquent JavaScript", 1);

		when(inventory.price("Effective Java")).thenReturn(40);
		when(inventory.price("Learning Chef")).thenReturn(50);
		when(inventory.price("Eloquent JavaScript")).thenReturn(10);

		when(inventory.find("Effective Java")).thenReturn(effectiveJava());
		when(inventory.find("Learning Chef")).thenReturn(new Book("Learning Chef", "Mischa Taylor", 50, LocalDate.of(2014, Month.OCTOBER, 26), 10, Arrays.asList("cloud", "devops")));
		when(inventory.find("Eloquent JavaScript")).thenReturn(new Book("Eloquent JavaScript", "Marjin Haverbeke", 10, LocalDate.of(2014, Month.OCTOBER, 26), 10, Arrays.asList("javascript", "programming")));

		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(24);

		String couponCode = "valid_discount_coupon";

		when(discountService.find(couponCode)).thenReturn(new CashDiscountCoupon(20, start, end, Arrays.asList("scala")));

		int cartAmount = cart.checkout(couponCode);

		assertThat(cartAmount, is(equalTo(140)));

		verify(inventory, times(3)).find(anyString());
		verify(discountService, times(1)).find(couponCode);
		verifyNoMoreInteractions(inventory, discountService);
	}

	private Book effectiveJava() {
		return new Book("Effective Java", "Joshua Bloch", 40, LocalDate.of(2008, Month.MAY, 28), 10, Arrays.asList("java", "programming"));
	}

	private Book cleanCodeBook() {
		return new Book("Clean Code", "Robert C Martin", 40, LocalDate.of(2008, Month.MAY, 28), 10, Arrays.asList("java", "programming"));
	}

	private Book openshiftCookbook() {
		return new Book("OpenShift Cookbook", "Shekhar Gulati", 55, LocalDate.of(2014, Month.OCTOBER, 26), 10, Arrays.asList("cloud", "programming"));
	}

}
