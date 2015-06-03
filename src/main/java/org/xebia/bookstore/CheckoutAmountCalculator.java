package org.xebia.bookstore;

import java.util.Collections;
import java.util.Map;

import org.xebia.bookstore.model.DiscountCoupon;
import org.xebia.bookstore.service.Inventory;

public class CheckoutAmountCalculator {

	private Inventory inventory;
	private Map<String, Integer> items;
	private DiscountCoupon discountCoupon;
	private int checkoutAmount;

	public CheckoutAmountCalculator(Inventory inventory, Map<String, Integer> items, DiscountCoupon discountCoupon) {
		this.inventory = inventory;
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
		return items.entrySet().stream().map(entry -> entry.getValue() * inventory.price(entry.getKey())).reduce(0, (sum, element) -> sum += element).intValue();
	}

	private int applicableCheckoutAmount() {
		return items.entrySet().stream().filter(entry -> discountCoupon.getCategories().isEmpty() || !Collections.disjoint(discountCoupon.getCategories(), inventory.find(entry.getKey()).getCategories())).map(entry -> entry.getValue() * inventory.price(entry.getKey())).reduce(0, (sum, element) -> sum += element).intValue();
	}

}
