package org.shekhar.trainings.xebibookstore.domain;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FileBasedInventoryTest {

	private Inventory inventory = new FileBasedInventory("src/test/resources/books.txt");

	@Test
	public void existsShouldReturnTrueWhenBookExistsInInventory() throws Exception {
		assertTrue(inventory.exists("OpenShift Cookbook"));
	}
	
	

}
