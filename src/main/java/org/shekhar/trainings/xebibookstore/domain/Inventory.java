package org.shekhar.trainings.xebibookstore.domain;

import org.shekhar.trainings.xebibookstore.exceptions.BookNotInInventoryException;

public interface Inventory {

	boolean exists(String book);

	int bookPrice(String book) throws BookNotInInventoryException;

}
