package org.xebia.bookstore.model;

import java.time.LocalDateTime;

import org.junit.Test;
import org.xebia.bookstore.exceptions.InvalidDiscountCouponException;

public class PercentageDisountCouponTest {

	@Test(expected = InvalidDiscountCouponException.class)
	public void startDateShouldBeLessThanEndDate() {
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.minusHours(12);
		new PercentageDiscountCoupon(20, start, end);
	}

	@Test(expected = InvalidDiscountCouponException.class)
	public void discountCouponCantBeNegative() throws Exception {
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(12);
		new PercentageDiscountCoupon(-20, start, end);
	}

}
