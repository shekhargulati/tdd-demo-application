package org.xebia.bookstore;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.xebia.bookstore.exceptions.BookNotInInventoryException;
import org.xebia.bookstore.exceptions.EmptyShoppingCartException;
import org.xebia.bookstore.exceptions.ExpiredDisountCouponException;
import org.xebia.bookstore.exceptions.NotEnoughBooksInInventoryException;
import org.xebia.bookstore.model.DiscountCoupon;
import org.xebia.bookstore.model.EmptyDiscountCoupon;
import org.xebia.bookstore.service.DiscountService;
import org.xebia.bookstore.service.Inventory;

public class ShoppingCart {

	private final Inventory inventory;
	private final DiscountService discountService;

	private Map<String, Integer> itemsInCart = new HashMap<>();

	public ShoppingCart(Inventory inventory, DiscountService discountService) {
		this.inventory = inventory;
		this.discountService = discountService;
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

	public int checkout() throws EmptyShoppingCartException, ExpiredDisountCouponException {
		return _checkout(new EmptyDiscountCoupon());
	}

	public int checkout(final String couponCode) {
		DiscountCoupon discountCoupon = discountService.find(couponCode);
		return _checkout(discountCoupon);
	}

	private int _checkout(DiscountCoupon discountCoupon) {
		if (itemsInCart.isEmpty()) {
			throw new EmptyShoppingCartException();
		}
		if (discountCoupon.isExpired()) {
			throw new ExpiredDisountCouponException();
		}
		
		return checkoutAmount() - discountCoupon.calculateDiscountAmount(applicableCheckoutAmount(discountCoupon));
	}

	private int applicableCheckoutAmount(DiscountCoupon discountCoupon) {
		return items().entrySet().stream().filter(entry -> discountCoupon.getCategories().isEmpty() || !Collections.disjoint(discountCoupon.getCategories(), inventory.find(entry.getKey()).getCategories())).map(entry -> entry.getValue() * inventory.price(entry.getKey())).reduce(0, (sum, element) -> sum += element).intValue();
	}

	private int checkoutAmount() {
		return items().entrySet().stream().map(entry -> entry.getValue() * inventory.price(entry.getKey())).reduce(0, (sum, element) -> sum += element).intValue();
	}

	@Override
	public String toString() {
		return itemsInCart.toString();
	}
	
}
