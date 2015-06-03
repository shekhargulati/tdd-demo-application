package org.xebia.bookstore.cart;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	public int checkout() throws EmptyShoppingCartException {
		return _checkout(new EmptyDiscountCoupon());
	}

	public int checkout(final String couponCode) throws EmptyShoppingCartException, ExpiredDisountCouponException {
		DiscountCoupon discountCoupon = discountService.find(couponCode);
		if (discountCoupon.isExpired()) {
			throw new ExpiredDisountCouponException();
		}
		return _checkout(discountCoupon);
	}

	private int _checkout(DiscountCoupon discountCoupon) throws EmptyShoppingCartException {
		if (itemsInCart.isEmpty()) {
			throw new EmptyShoppingCartException();
		}
		List<ShoppingCartItem> items = items().entrySet().stream().map(entry -> new ShoppingCartItem(inventory.find(entry.getKey()),entry.getValue())).collect(Collectors.toList());
		return new CheckoutAmountCalculator(items, discountCoupon).checkoutAmount();
	}

	@Override
	public String toString() {
		return itemsInCart.toString();
	}

}