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

	}

	@Test
	public void checkNotAdminCantDelete() throws IOException {
		createMocks();
		Mockito.when(security.isUserInRole("ADMIN")).thenReturn(false);
		boolean isAllowed = PermissionsChecker
				.canDelete("", security, "", 0);
		Assert.assertFalse(isAllowed);

	}

	@Test
	public void checkAdminCanDelete() throws IOException {
		createMocks();
		Mockito.when(security.isUserInRole("ADMIN")).thenReturn(true);
		boolean isAllowed = PermissionsChecker
				.canDelete("", security, "", 0);
		Assert.assertTrue(isAllowed);

	}

	@Test
	public void checkOwnerCanDelete() throws IOException {
		createMocks();
		Mockito.when(security.isUserInRole("")).thenReturn(false);
		boolean isAllowed = PermissionsChecker.canDelete("", security,
				"testuser", 0b111101110000);
		Assert.assertTrue(isAllowed);

	}

	@Test
	public void checkOwnerCantDelete() throws IOException {
		createMocks();
		Mockito.when(security.isUserInRole("")).thenReturn(false);
		boolean isAllowed = PermissionsChecker.canDelete("", security,
				"testuser", 0b111001110000);
		Assert.assertFalse(isAllowed);

	}
}
