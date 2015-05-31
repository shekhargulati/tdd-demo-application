package org.xebia.bookstore.model;

import java.time.LocalDateTime;

public class EmptyDiscountCoupon extends DiscountCoupon {

	public EmptyDiscountCoupon() {
		super(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
	}

	@Override
	public int calculateDiscountAmount(int checkountAmount) {
		return 0;
	}

}
