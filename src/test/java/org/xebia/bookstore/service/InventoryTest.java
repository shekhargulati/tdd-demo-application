package org.xebia.bookstore.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

import org.hamcrest.collection.IsArrayWithSize;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xebia.bookstore.exceptions.BookNotInInventoryException;
import org.xebia.bookstore.model.Book;
import org.xebia.bookstore.service.internal.InMemoryInventory;

public class InventoryTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private Inventory inventory;

	@Before
	public void setup() throws Exception {
		inventory = new InMemoryInventory();
	}

	@Test
	public void canAddSingleBookToTheInventory() {
		Book book = new Book("Effective Java", "Joshua Bloch", 40, LocalDate.of(2008, Month.MAY, 28), 1, Arrays.asList("java", "programming"));
		String bookId = inventory.add(book);
		assertNotNull(bookId);
	}

	@Test
	public void canAddMultipleBooksToTheInventory() throws Exception {
		String[] bookIds = inventory.add(books());
		assertThat(bookIds, IsArrayWithSize.arrayWithSize(equalTo(2)));
		assertNotNull(bookIds[0]);
		assertNotNull(bookIds[1]);
	}

	@Test
	public void bookAddedToTheInventoryShouldExist() throws Exception {
		inventory.add(books());
		boolean exists = inventory.exists("Effective Java");
		assertTrue(exists);
	}

	@Test
	public void bookNotAddedToTheInventorShouldNotExist() throws Exception {
		inventory.add(books());
		boolean exists = inventory.exists("Extreme Programming Explained");
		assertFalse(exists);
	}

	@Test
	public void findBookInOneMillionBooks() throws Exception {
		IntStream.rangeClosed(1, 100000).forEach(i -> inventory.add(new Book("book" + i, "author" + i, i, LocalDate.of(2008, Month.MAY, 21), 1, Collections.emptyList())));
// long start = System.currentTimeMillis();
		boolean exists = inventory.exists("book_test");
// System.out.println("Total time " + (System.currentTimeMillis() - start));
		assertFalse(exists);
	}

	@Test
	public void findPriceOfBookInInventory() throws Exception {
		inventory.add(books());
		int price = inventory.find("Effective Java").getPrice();
		assertThat(price, is(equalTo(40)));
	}

	@Test
	public void throwExceptionWhenBookNotInInventory() throws Exception {
		expectedException.expect(isA(BookNotInInventoryException.class));
		expectedException.expectMessage(is(equalTo("Sorry, 'Effective Java' not in stock!!")));

		inventory.find("Effective Java");
	}

	@Test
	public void trueWhenNotEnoughCopiesInInventory() throws Exception {
		inventory.add(books(2));

		boolean enoughCopies = inventory.hasEnoughCopies("OpenShift Cookbook", 2);

		assertTrue(enoughCopies);
	}

	@Test
	public void falseWhenNotEnoughCopiesInInventory() throws Exception {
		inventory.add(books(2));

		boolean enoughCopies = inventory.hasEnoughCopies("OpenShift Cookbook", 5);

		assertFalse(enoughCopies);
	}

	@Test
	public void findBookInInventory() throws Exception {
		inventory.add(books(2));
		Book book = inventory.find("Effective Java");
		assertThat(book.getPrice(), is(equalTo(40)));
	}

	@Test
	public void findThrowExceptionWhenBookNotInInventory() throws Exception {
		expectedException.expect(isA(BookNotInInventoryException.class));
		expectedException.expectMessage(is(equalTo("Sorry, 'Effective Java' not in stock!!")));

		inventory.find("Effective Java");
	}

	private static Book[] books() {
		return books(1);
	}

	private static Book[] books(int quanity) {
		Book book1 = new Book("Effective Java", "Joshua Bloch", 40, LocalDate.of(2008, Month.MAY, 28), quanity, Arrays.asList("java", "programming"));
		Book book2 = new Book("OpenShift Cookbook", "Shekhar Gulati", 55, LocalDate.of(2014, Month.OCTOBER, 26), quanity, Arrays.asList("cloud", "programming"));
		return new Book[] { book1, book2 };
	}
}
