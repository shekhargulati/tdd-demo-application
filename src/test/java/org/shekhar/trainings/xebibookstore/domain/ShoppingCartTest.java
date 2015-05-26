package org.shekhar.trainings.xebibookstore.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
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
import org.shekhar.trainings.xebibookstore.exceptions.BookNotInInventoryException;

public class ShoppingCartTest {

	private final Inventory inventory = mock(Inventory.class);
	private final ShoppingCart cart = new ShoppingCart(inventory);

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void canAddMultipleBooksToTheShoppingCart() throws Exception {
		when(inventory.exists("Effective Java")).thenReturn(true);
		when(inventory.exists("OpenShift Cookbook")).thenReturn(true);

		cart.add("Effective Java");
		cart.add("OpenShift Cookbook");

		assertThat(cart.size(), is(equalTo(2)));

		verify(inventory, times(1)).exists("Effective Java");
		verify(inventory, times(1)).exists("OpenShift Cookbook");
		verifyNoMoreInteractions(inventory);
	}

	@Test
	public void canAddMultipleBooksToTheShoppingCartInOneGo() throws Exception {
		when(inventory.exists(anyString())).thenReturn(true);

		cart.add("Effective Java", "OpenShift Cookbook", "Java Concurrency in Practice");

		assertThat(cart.items().size(), equalTo(3));
		assertThat(cart.items(), IsMapContaining.hasEntry(equalTo("Effective Java"), equalTo(1)));

		verify(inventory, times(3)).exists(anyString());
		verify(inventory, times(3)).exists(anyString());
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
		cart.add("OpenShift Cookbook", "Effective Java", "Clean Code");
		verify(inventory, times(3)).exists(anyString());

		when(inventory.bookPrice("OpenShift Cookbook")).thenReturn(45);
		when(inventory.bookPrice("Effective Java")).thenReturn(30);
		when(inventory.bookPrice("Clean Code")).thenReturn(55);

		int cartAmount = cart.checkout();

		assertThat(cartAmount, is(equalTo(130)));

		verify(inventory, times(3)).bookPrice(anyString());
		verifyNoMoreInteractions(inventory);
	}

	@Test
	public void canAddMultipleQuantiesOfBook() throws Exception {
		when(inventory.exists("Effective Java")).thenReturn(true);

		cart.add("Effective Java", 10);

		assertThat(cart.size(), is(equalTo(10)));

		verify(inventory, times(1)).exists("Effective Java");
		verifyNoMoreInteractions(inventory);
	}
	
	@Test
	public void canCheckoutMultipleQuantiesOfBook() throws Exception {
		when(inventory.exists(anyString())).thenReturn(true);
		cart.add("OpenShift Cookbook",2);
		cart.add("Effective Java",5);
		cart.add("Clean Code",10);
		
		verify(inventory, times(3)).exists(anyString());

		when(inventory.bookPrice("OpenShift Cookbook")).thenReturn(45);
		when(inventory.bookPrice("Effective Java")).thenReturn(30);
		when(inventory.bookPrice("Clean Code")).thenReturn(55);

		int cartAmount = cart.checkout();

		assertThat(cartAmount, is(equalTo(790)));

		verify(inventory, times(3)).bookPrice(anyString());
		verifyNoMoreInteractions(inventory);
	}
}
