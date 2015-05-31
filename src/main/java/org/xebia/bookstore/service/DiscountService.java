package org.xebia.bookstore.service;

import org.xebia.bookstore.exceptions.InvalidDiscountCouponException;
import org.xebia.bookstore.model.DiscountCoupon;

public interface DiscountService {

	DiscountCoupon find(String couponCode) throws InvalidDiscountCouponException;

	String create(DiscountCoupon discountCoupon);


}
