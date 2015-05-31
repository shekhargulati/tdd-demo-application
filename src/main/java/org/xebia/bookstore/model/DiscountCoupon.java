package org.xebia.bookstore.model;

import java.time.LocalDateTime;

import org.xebia.bookstore.exceptions.InvalidDiscountCouponException;

public abstract class DiscountCoupon {

	private String couponCode;

	private final LocalDateTime start;
	private final LocalDateTime end;

	public DiscountCoupon(LocalDateTime start, LocalDateTime end) {
		if (start.isAfter(end)) {
			throw new InvalidDiscountCouponException("'start' can not be greater than 'end'.");
		}
		this.start = start;
		this.end = end;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
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
	
	public abstract int calculateDiscountAmount(int checkoutAmount);

}
