package org.shekhar.trainings.xebibookstore;

import org.shekhar.trainings.xebibookstore.domain.FileBasedInventoryManager;
import org.shekhar.trainings.xebibookstore.domain.ShoppingCart;
import org.shekhar.trainings.xebibookstore.exceptions.BookNotInInventoryException;
import org.shekhar.trainings.xebibookstore.exceptions.EmptyShoppingCartException;

public class XebiBookstore {

	private final String inventory;

	public XebiBookstore(final String inventory) {
		this.inventory = inventory;
	}

	public int checkout(String... books) throws BookNotInInventoryException {
		if (books == null || books.length == 0) {
			throw new EmptyShoppingCartException();
		}
		ShoppingCart cart = new ShoppingCart(new FileBasedInventoryManager(inventory));
		cart.add(books);
		return cart.amount();
	}

	public static void main(String[] args) {
		System.out.println("************Welcome to Xebibookstore***********");
		if (args == null || args.length < 2) {
			throw new IllegalArgumentException("Please provide values for inventory file and book name.");
		}
		final String inventory = args[0];
		final String bookToBuy = args[1];
		XebiBookstore bookstore = new XebiBookstore(inventory);
		int price = bookstore.checkout(bookToBuy);
		System.out.println(price);

	}
}
