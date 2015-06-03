package org.xebia.bookstore.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.xebia.bookstore.cart.ShoppingCart;

public class IsShoppingCartWithSize extends TypeSafeMatcher<ShoppingCart> {

	private final int itemsInCart;

	public IsShoppingCartWithSize(int itemsInCart) {
		this.itemsInCart = itemsInCart;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText(String.format("a shopping cart containing %d items", itemsInCart));
	}

	@Override
	protected boolean matchesSafely(ShoppingCart cart) {
		return cart.size() == itemsInCart;
	}

	public static <T> Matcher<ShoppingCart> hasSize(int itemsInCart) {
		return new IsShoppingCartWithSize(itemsInCart);
	}

}
