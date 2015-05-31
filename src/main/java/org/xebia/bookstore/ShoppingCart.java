package org.xebia.bookstore;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.xebia.bookstore.exceptions.BookNotInInventoryException;
import org.xebia.bookstore.exceptions.EmptyShoppingCartException;
import org.xebia.bookstore.exceptions.ExpiredDisountCouponException;
import org.xebia.bookstore.exceptions.NotEnoughBooksInInventoryException;
import org.xebia.bookstore.model.DisountCoupon;
import org.xebia.bookstore.model.EmptyDiscountCoupon;
import org.xebia.bookstore.service.Inventory;

public class ShoppingCart {

	private final Inventory inventory;

	private Map<String, Integer> itemsInCart = new HashMap<>();

	public ShoppingCart(Inventory inventory) {
		this.inventory = inventory;
	}

	public void add(String... books) throws BookNotInInventoryException {
		Arrays.stream(books).forEach(book -> add(book, 1));
	}

	public void add(String title, int quantity) throws BookNotInInventoryException, NotEnoughBooksInInventoryException {
		if (!inventory.exists(title)) {
			throw new BookNotInInventoryException(title);
		}
		if (!inventory.hasEnoughCopies(title, quantity)) {
			throw new NotEnoughBooksInInventoryException(title);
		}
		itemsInCart.put(title, itemsInCart.compute(title, (k, v) -> (k == null) ? quantity : (v == null ? 0 : v) + quantity));
	}

	public int size() {
		return itemsInCart.values().stream().reduce(0, (total, quantity) -> total + quantity);
	}

	public Map<String, Integer> items() {
		return Collections.unmodifiableMap(itemsInCart);
	}

	public int checkout() throws EmptyShoppingCartException {
		return checkout(new EmptyDiscountCoupon());
	}

	public int checkout(final DisountCoupon coupon) throws EmptyShoppingCartException, ExpiredDisountCouponException {
		if (itemsInCart.isEmpty()) {
			throw new EmptyShoppingCartException();
		}
		if (coupon.isExpired()) {
			throw new ExpiredDisountCouponException();
		}
		int checkoutAmount = items().entrySet().stream().map(entry -> entry.getValue() * inventory.price(entry.getKey())).reduce(0, (sum, element) -> sum += element).intValue();
		return checkoutAmount - (checkoutAmount * coupon.getPercentageDiscount()) / 100;
	}

}
