package org.xebia.bookstore.model;

import java.time.LocalDateTime;

import org.xebia.bookstore.exceptions.InvalidDiscountCouponException;

public class CashDiscountCoupon extends DiscountCoupon {

	private final int cashDiscount;

	public CashDiscountCoupon(int cashDiscount, LocalDateTime start, LocalDateTime end) {
		super(start, end);
		this.cashDiscount = cashDiscount;
	}

	public int getCashDiscount() {
		return cashDiscount;
	}

	@Override
	public int calculateDiscountAmount(int checkountAmount) {
		if (checkountAmount - cashDiscount < 0.6 * checkountAmount) {
			throw new InvalidDiscountCouponException("This coupon is not applicable for this checkout amount.");
		}
		return cashDiscount;
	}

}
