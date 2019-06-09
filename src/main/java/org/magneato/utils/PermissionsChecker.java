package org.magneato.utils;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.SecurityContext;

/*
 * Permisions are Create, Read, Update, Delete for : special role, owner, group, other
 * 
 * Normally permissions for resource are: 0b1111111111101100
 * 
 * That is special users can do anything their permission mask allows
 * Owners can do anything
 * Groups members can do anything except delete
 * Other can do anything except update and delete
 */
public class PermissionsChecker {
	private static final int CREATE = 0b100010001000;
	private static final int READ = 0b010001000100;
	private static final int UPDATE = 0b001000100010;
	public static final int DELETE = 0b0001000100010001;
	public static final int OWNER = 0b111100000000;
	public static final int GROUP = 0b00011110000;
	public static final int OTHER = 0b000000001111;

	private static final Map<String, Integer> specialRoles;
	static {
		// this should be moved to config.yml
		// a special role consists consists of a Role Name and a set of
		// permissions, for example we could define a MODERATOR role with delete and update permission
		// this is more global than file groups
		Map<String, Integer> aMap = new HashMap<String, Integer>();
		aMap.put("ADMIN", 0b1111000000000000); // admin can do anything
		specialRoles = Collections.unmodifiableMap(aMap);
	}

	public static boolean canDelete(SecurityContext security,
			String owner, List<String> groups, int perms) {

		return PermissionsChecker.isAllowed(security, owner, groups, perms,
				PermissionsChecker.DELETE);
	}

	/*
	 * Check if admin - special super user role otherwise check if same role as
	 * file
	 */
	private static boolean isAllowed(SecurityContext security,
			String owner, List<String> groups, int resourcePerms, int allowedPerms) {
		if (security == null) {
			// not logged in
			return false;
		}
		
		// check special roles
		for (Map.Entry<String, Integer> entry : specialRoles.entrySet()) {
			String specialRole = entry.getKey();
			if (security.isUserInRole(specialRole)) {
				int rolePerms = entry.getValue();
				int perms = rolePerms & allowedPerms & resourcePerms;
				if (perms > 0) {
					return true;
				}
			}

		}

		// check resource permissions for principal
		Principal principal = security.getUserPrincipal();
		if (principal != null) {
			String user = principal.getName();
			if (user.equals(owner)) {
				// check perms
				int perms = resourcePerms & OWNER & allowedPerms;
				if (perms > 0) {
					return true;
				}
			}
		}
		
		// check groups, other than special roles
		
		// check other

		return false;
	}

}
