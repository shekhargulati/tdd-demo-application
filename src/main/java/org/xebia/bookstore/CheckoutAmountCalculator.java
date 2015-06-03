package org.xebia.bookstore;

import java.util.Collections;
import java.util.List;

import org.xebia.bookstore.model.DiscountCoupon;

public class CheckoutAmountCalculator {

	private List<ShoppingCartItem> items;
	private DiscountCoupon discountCoupon;
	private int checkoutAmount;

	public CheckoutAmountCalculator(List<ShoppingCartItem> items, DiscountCoupon discountCoupon) {
		this.items = items;
		this.discountCoupon = discountCoupon;
		this.checkoutAmount = checkoutAmountWithoutDiscount() - discount();
	}

	public int checkoutAmount() {
		return checkoutAmount;
	}

	private int discount() {
		return discountCoupon.calculateDiscountAmount(applicableCheckoutAmount());
	}

	private int checkoutAmountWithoutDiscount() {
		return items.stream().map(item -> item.getQuantity() * item.getBook().getPrice()).reduce(0, (sum, element) -> sum += element).intValue();
	}

	private int applicableCheckoutAmount() {
		return items.stream().filter(item -> discountCoupon.getCategories().isEmpty() || !Collections.disjoint(discountCoupon.getCategories(), item.getBook().getCategories())).map(item -> item.getQuantity() * item.getBook().getPrice()).reduce(0, (sum, element) -> sum += element).intValue();
	}

}
