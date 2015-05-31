package org.xebia.bookstore.service;

import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;

import org.junit.Test;
import org.xebia.bookstore.exceptions.InvalidDiscountCouponException;
import org.xebia.bookstore.model.PercentageDiscountCoupon;
import org.xebia.bookstore.service.internal.InMemoryDiscountService;

public class DiscountServiceTest {

	private DiscountService discountService = new InMemoryDiscountService();

	@Test
	public void createNewDiscountCouponWithUniqueCouponCode() {
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(24);
		String couponCode = discountService.create(new PercentageDiscountCoupon(20, start, end));
		assertNotNull(couponCode);
	}

	@Test(expected = InvalidDiscountCouponException.class)
	public void throwExceptionWhenDiscountCouponDoesNotExist() throws Exception {
		discountService.find("invalid_coupon_code");
	}

}
