package org.xebia.bookstore.service;

import org.xebia.bookstore.model.Book;

public interface Inventory {

	boolean exists(String title);

	String[] add(Book... books);

	String add(Book book);

	boolean hasEnoughCopies(String title, int quantity);

	Book find(String title);

}
