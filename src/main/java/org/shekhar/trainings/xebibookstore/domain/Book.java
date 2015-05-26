package org.shekhar.trainings.xebibookstore.domain;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class Book {

	private final String title;
	private final String author;
	private final int price;
	private final LocalDate publishedOn;
	private final int quantity;
	private final List<String> categories;
	
	private String id;

	public Book(String title, String author, int price, LocalDate publishedOn, int quantity, List<String> categories) {
		this.title = title;
		this.author = author;
		this.price = price;
		this.publishedOn = publishedOn;
		this.quantity = quantity;
		this.categories = Collections.unmodifiableList(categories);
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public int getPrice() {
		return price;
	}

	public LocalDate getPublishedOn() {
		return publishedOn;
	}

	public int getQuantity() {
		return quantity;
	}

	public List<String> getCategories() {
		return categories;
	}
	
	public void assignBookId(String id){
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	
	

}
