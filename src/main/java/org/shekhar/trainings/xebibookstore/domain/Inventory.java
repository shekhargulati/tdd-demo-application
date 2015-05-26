package org.shekhar.trainings.xebibookstore.domain;

import org.shekhar.trainings.xebibookstore.exceptions.BookNotInInventoryException;

public interface Inventory {

	boolean exists(String title);

	int price(String title) throws BookNotInInventoryException;

	String[] add(Book... books);

	String add(Book book);

}
