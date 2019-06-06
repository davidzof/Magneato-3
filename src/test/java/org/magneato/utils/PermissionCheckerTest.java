package org.magneato.utils;

import java.io.IOException;
import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import org.eclipse.jetty.security.AbstractLoginService.UserPrincipal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class PermissionCheckerTest {
	SecurityContext security;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	private void createMocks() {
		security = Mockito.mock(SecurityContext.class);
		Principal principal = new UserPrincipal("testuser", null);
		// Credential credential = new Credential();
		Mockito.when(security.getUserPrincipal()).thenReturn(principal);
		Mockito.when(security.isUserInRole("ADMIN")).thenReturn(true);

	}

	/*
	 * We're admin but special roles delete bit is off - we can even prevent
	 * special users from deleting a file if we want. In this case you need to
	 * use the raw JSON interface to reenable access
	 */
	@Test
	public void checkNotAdminCantDelete() throws IOException {
		createMocks();
		boolean isAllowed = PermissionsChecker.canDelete(security, "", null,
				0b1110111111110000);
		Assert.assertFalse(isAllowed);
		
		int i = 0b1111111111000000;
		System.out.println(i);
	}

	/*
	 * We're admin special roles delete is on for the resource
	 */
	@Test
	public void checkAdminCanDelete() throws IOException {
		createMocks();
		boolean isAllowed = PermissionsChecker.canDelete(security, "", null,
				0b0001000000000000);
		Assert.assertTrue(isAllowed);

	}

	@Test
	public void checkOwnerCanDelete() throws IOException {
		createMocks();
		Mockito.when(security.isUserInRole("")).thenReturn(false);
		boolean isAllowed = PermissionsChecker.canDelete(security,
				"testuser", null, 0b111101110000);
		Assert.assertTrue(isAllowed);

	}

	@Test
	public void checkOwnerCantDelete() throws IOException {
		createMocks();
		Mockito.when(security.isUserInRole("")).thenReturn(false);
		boolean isAllowed = PermissionsChecker.canDelete(security,
				"testuser", null, 0b111001110000);
		Assert.assertFalse(isAllowed);

	}
}
