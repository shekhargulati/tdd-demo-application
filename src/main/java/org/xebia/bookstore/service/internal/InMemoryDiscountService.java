package org.xebia.bookstore.service.internal;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.xebia.bookstore.exceptions.InvalidDiscountCouponException;
import org.xebia.bookstore.model.DiscountCoupon;
import org.xebia.bookstore.service.DiscountService;

public class InMemoryDiscountService implements DiscountService {

	private Map<String, DiscountCoupon> coupons = new ConcurrentHashMap<>();

	@Override
	public DiscountCoupon find(String couponCode) throws InvalidDiscountCouponException {
		if (!coupons.containsKey(couponCode)) {
			throw new InvalidDiscountCouponException("Sorry, the coupon code you entered does not exist.");
		}
		return coupons.get(couponCode);
	}

	@Override
	public String create(DiscountCoupon discountCoupon) {
		String couponCode = UUID.randomUUID().toString();
		discountCoupon.setCouponCode(couponCode);
		coupons.put(couponCode, discountCoupon);
		return couponCode;
	}

}
