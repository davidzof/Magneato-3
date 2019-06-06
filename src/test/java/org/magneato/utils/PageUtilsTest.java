package org.magneato.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PageUtilsTest {
	PageUtils pageUtils;

	@Before
	public void setUp() {
		pageUtils = new PageUtils();
	}

	@Test
	public void getId() {
		String id = PageUtils
				.getId("http://localhost:9090/r95eec7d13e7a/skating-to-the-cret-luisard");
		Assert.assertEquals(id, "r95eec7d13e7a");
	}

	@Test
	public void getIdShortPath() {
		String id = PageUtils
				.getId("/r95eec7d13e7a/skating-to-the-cret-luisard");
		Assert.assertEquals(id, "r95eec7d13e7a");
	}

	@Test
	public void getIdShortPath2() {
		String id = PageUtils
				.getId("r95eec7d13e7a/skating-to-the-cret-luisard");
		Assert.assertEquals(id, null);
	}

	@Test
	public void getIdEdit() {
		String id = PageUtils
				.getId("http://localhost:9090/edit/r95eec7d13e7a/skating-to-the-cret-luisard");
		Assert.assertEquals(id, "r95eec7d13e7a");
	}

}
