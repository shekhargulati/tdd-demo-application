package org.xebia.bookstore.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.xebia.bookstore.ShoppingCart;

public class IsShoppingCartWithMaxSize extends TypeSafeMatcher<ShoppingCart> {

	private final int maxItemsInCart;

	public IsShoppingCartWithMaxSize(int maxItemsInCart) {
		this.maxItemsInCart = maxItemsInCart;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText(String.format("a shopping cart containing %d items", maxItemsInCart));
	}

	@Override
	protected boolean matchesSafely(ShoppingCart cart) {
		return cart.size() <= maxItemsInCart;
	}

	public static <T> Matcher<ShoppingCart> constrainMaxItemsInCart(int itemsInCart) {
		return new IsShoppingCartWithMaxSize(itemsInCart);
	}

}
