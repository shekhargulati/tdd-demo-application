package org.xebia.bookstore.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.xebia.bookstore.exceptions.InvalidDiscountCouponException;

public abstract class DiscountCoupon {

	private String couponCode;

	private final LocalDateTime start;
	private final LocalDateTime end;
	private final List<String> categories;

	public DiscountCoupon(LocalDateTime start, LocalDateTime end, List<String> categories) {
		if (start.isAfter(end)) {
			throw new InvalidDiscountCouponException("'start' can not be greater than 'end'.");
		}
		this.start = start;
		this.end = end;
		this.categories = Collections.unmodifiableList(categories);
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
	
	public List<String> getCategories() {
		return categories;
	}

}
