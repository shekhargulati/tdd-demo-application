package org.xebia.bookstore.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.xebia.bookstore.exceptions.InvalidDiscountCouponException;

public class CashDiscountCoupon extends DiscountCoupon {

	private final int cashDiscount;

	public CashDiscountCoupon(int cashDiscount, LocalDateTime start, LocalDateTime end) {
		this(cashDiscount, start, end, Collections.emptyList());
	}

	public CashDiscountCoupon(int cashDiscount, LocalDateTime start, LocalDateTime end, List<String> categories) {
		super(start, end, categories);
		this.cashDiscount = cashDiscount;
	}

	public int getCashDiscount() {
		return cashDiscount;
	}

	@Override
	public int calculateDiscountAmount(int discountApplicableAmount) {
		if ((discountApplicableAmount - cashDiscount) < 0) {
			return 0;
		}
		if ((discountApplicableAmount - cashDiscount) < (0.6 * discountApplicableAmount)) {
			throw new InvalidDiscountCouponException("This coupon is not applicable for this checkout amount.");
		}
		return cashDiscount;
	}

}
