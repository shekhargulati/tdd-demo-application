package org.xebia.bookstore.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.xebia.bookstore.exceptions.InvalidDiscountCouponException;

public class DisountCoupon {

	private final String couponCode;
	private final int percentageDiscount;
	private final LocalDateTime start;
	private final LocalDateTime end;

	public DisountCoupon(int percentageDiscount, LocalDateTime start, LocalDateTime end) {
		if (start.isAfter(end)) {
			throw new InvalidDiscountCouponException("'start' can not be greater than 'end'.");
		}
		if (percentageDiscount < 0) {
			throw new InvalidDiscountCouponException("'discount' can not be negative.");
		}
		this.couponCode = UUID.randomUUID().toString();
		this.percentageDiscount = percentageDiscount;
		this.start = start;
		this.end = end;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public int getPercentageDiscount() {
		return percentageDiscount;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public boolean isExpired() {
		return getEnd().compareTo(LocalDateTime.now()) < 0;
	}

}
