package org.shekhar.trainings.xebibookstore.exceptions;

public class EmptyShoppingCartException extends RuntimeException {

	private static final long serialVersionUID = 5772364793277495679L;
	
	public EmptyShoppingCartException() {
		super("You can't checkout an empty cart. Please first add items to the cart.");
	}

}
