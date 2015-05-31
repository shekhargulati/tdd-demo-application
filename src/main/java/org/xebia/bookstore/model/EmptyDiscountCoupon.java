package org.xebia.bookstore.model;

import java.time.LocalDateTime;

public class EmptyDiscountCoupon extends DisountCoupon {

	public EmptyDiscountCoupon() {
		super(0, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
	}

}
