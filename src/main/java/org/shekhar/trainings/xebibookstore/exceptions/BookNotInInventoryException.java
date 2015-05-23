package org.shekhar.trainings.xebibookstore.exceptions;

public class BookNotInInventoryException extends RuntimeException {

	private static final long serialVersionUID = 1952491292203877872L;

	public BookNotInInventoryException(String book) {
		super(String.format("Sorry, '%s' not in stock!!", book));
	}

}
