package org.magneato.utils;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

/*
 * Permisions are Create, Read, Update, Delete for : owner, group, other
 */
public class PermissionsChecker {
	private static final int CREATE = 0b100010001000;
	private static final int READ = 0b010001000100;
	private static final int UPDATE = 0b001000100010;
	private static final int DELETE = 0b000100010001;

	/*
	 * Check if admin - special super user role
	 * otherwise check if same role as file
	 */
	public static boolean isAllowed(String[] roles, SecurityContext security,
			String owner, int perms) {
		if (security == null) {
			// not logged in
			return false;
		}

		for (String role : roles) {
			if (security.isUserInRole(role)) {
				return true;
			}
		}

		// check resource permissions
		Principal principal = security.getUserPrincipal();
		System.out.println(">>> principal " + principal);
		if (principal != null) {
			String user = principal.getName();
			// check delete
		}

		return false;
	}
}
