package org.shekhar.trainings.xebibookstore.domain.exceptions;

import java.util.List;
import java.util.stream.Collectors;

public class BookNotInInventoryException extends RuntimeException {

	private static final long serialVersionUID = 1952491292203877872L;
	
	private static final String SORRY_S_NOT_IN_STOCK = "Sorry, '%s' not in stock!!";

	public BookNotInInventoryException(String book) {
		super(String.format(SORRY_S_NOT_IN_STOCK, book));
	}

	public BookNotInInventoryException(List<String> books) {
		super(books.stream().map(book -> String.format(SORRY_S_NOT_IN_STOCK, book)).collect(Collectors.joining("\\n")));
	}

}
