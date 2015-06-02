package org.xebia.bookstore.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.xebia.bookstore.exceptions.InvalidDiscountCouponException;

public class PercentageDiscountCoupon extends DiscountCoupon {

	private final int percentageDiscount;

	public PercentageDiscountCoupon(int percentageDiscount, LocalDateTime start, LocalDateTime end) {
		this(percentageDiscount, start, end, Collections.emptyList());
	}

	public PercentageDiscountCoupon(int percentageDiscount, LocalDateTime start, LocalDateTime end, List<String> categories) {
		super(start, end, categories);
		if (percentageDiscount < 0) {
			throw new InvalidDiscountCouponException("'discount' can not be negative.");
		}
		this.percentageDiscount = percentageDiscount;
	}

	public int getPercentageDiscount() {
		return percentageDiscount;
	}

	@Override
	public int calculateDiscountAmount(int checkoutAmount) {
		return (checkoutAmount * this.getPercentageDiscount()) / 100;
	}

}
