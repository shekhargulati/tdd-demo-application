package org.xebia.bookstore;

import org.xebia.bookstore.model.Book;

public class ShoppingCartItem {

	private final Book book;
	private final int quantity;

	public ShoppingCartItem(Book book, int quantity) {
		this.book = book;
		this.quantity = quantity;
	}

	public Book getBook() {
		return book;
	}

	public int getQuantity() {
		return quantity;
	}

}
