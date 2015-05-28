package org.xebia.bookstore.exceptions;

public class NotEnoughBooksInInventoryException extends RuntimeException {

	private static final long serialVersionUID = 3756692560252766409L;

	public NotEnoughBooksInInventoryException(String tittle) {
		super(String.format("There are not enough copies of '%s' in the inventory.", tittle));
	}
}
