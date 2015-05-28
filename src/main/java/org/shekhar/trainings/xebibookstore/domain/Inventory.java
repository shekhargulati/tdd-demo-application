package org.shekhar.trainings.xebibookstore.domain;

import org.shekhar.trainings.xebibookstore.domain.exceptions.BookNotInInventoryException;

public interface Inventory {

	boolean exists(String title);

	int price(String title) throws BookNotInInventoryException;

	String[] add(Book... books);

	String add(Book book);

	boolean hasEnoughCopies(String title, int quantity);

}
