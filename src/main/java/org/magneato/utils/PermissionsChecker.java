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
	public static final int DELETE = 0b000100010001;
	public static final int OWNER = 0b111100000000;

	public static boolean canDelete(String role, SecurityContext security,
			String owner, int perms) {

		return PermissionsChecker.isAllowed(role, security, owner, perms,
				PermissionsChecker.DELETE);
	}

	/*
	 * Check if admin - special super user role otherwise check if same role as
	 * file
	 */
	private static boolean isAllowed(String role, SecurityContext security,
			String owner, int resourcePerms, int allowedPerms) {
		if (security == null) {
			// not logged in
			return false;
		}

		if (security.isUserInRole("ADMIN")) {
			return true;
		}

		// check resource permissions
		Principal principal = security.getUserPrincipal();
		if (principal != null) {
			String user = principal.getName();
			if (user.equals(owner)) {
				// check permis
				int perms = resourcePerms & OWNER & allowedPerms;
				System.out.println("perms " + perms);
				if (perms > 0) {
					return true;
				}
			}
		}

		return false;
	}

}
