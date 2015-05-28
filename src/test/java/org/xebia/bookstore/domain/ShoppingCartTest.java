package org.xebia.bookstore.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.hamcrest.collection.IsMapContaining;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xebia.bookstore.ShoppingCart;
import org.xebia.bookstore.exceptions.BookNotInInventoryException;
import org.xebia.bookstore.exceptions.NotEnoughBooksInInventoryException;
import org.xebia.bookstore.service.Inventory;

public class ShoppingCartTest {

	private final Inventory inventory = mock(Inventory.class);
	private final ShoppingCart cart = new ShoppingCart(inventory);

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void canAddMultipleBooksToTheShoppingCart() throws Exception {
		when(inventory.exists("Effective Java")).thenReturn(true);
		when(inventory.exists("OpenShift Cookbook")).thenReturn(true);

		when(inventory.hasEnoughCopies(anyString(), anyInt())).thenReturn(true);

		cart.add("Effective Java");
		cart.add("OpenShift Cookbook");

		assertThat(cart.size(), is(equalTo(2)));

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

		when(inventory.price("OpenShift Cookbook")).thenReturn(45);
		when(inventory.price("Effective Java")).thenReturn(30);
		when(inventory.price("Clean Code")).thenReturn(55);

		int cartAmount = cart.checkout();

		assertThat(cartAmount, is(equalTo(130)));

		verify(inventory, times(3)).price(anyString());
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

		when(inventory.price("OpenShift Cookbook")).thenReturn(45);
		when(inventory.price("Effective Java")).thenReturn(30);
		when(inventory.price("Clean Code")).thenReturn(55);

		int cartAmount = cart.checkout();

		assertThat(cartAmount, is(equalTo(790)));

		verify(inventory, times(3)).price(anyString());
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
}
