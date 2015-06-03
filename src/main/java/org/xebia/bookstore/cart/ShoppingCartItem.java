package org.xebia.bookstore.cart;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((book == null) ? 0 : book.getTitle().hashCode());
		result = prime * result + quantity;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShoppingCartItem other = (ShoppingCartItem) obj;
		if (book == null) {
			if (other.book != null)
				return false;
		} else if (!book.getTitle().equals(other.book.getTitle()))
			return false;
		if (quantity != other.quantity)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ShoppingCartItem [book=" + book.getTitle() + ", quantity=" + quantity + "]";
	}
}
