package org.xebia.bookstore.model;

import java.time.LocalDateTime;
import java.util.Collections;

public class EmptyDiscountCoupon extends DiscountCoupon {

	public EmptyDiscountCoupon() {
		super(LocalDateTime.now(), LocalDateTime.now().plusDays(1), Collections.emptyList());
	}

	@Override
	public int calculateDiscountAmount(int checkountAmount) {
		return 0;
	}

}
