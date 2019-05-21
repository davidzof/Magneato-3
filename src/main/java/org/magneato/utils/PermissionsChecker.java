package org.magneato.utils;

import javax.ws.rs.core.SecurityContext;
/*
 * Permisions are Create, Read, Update, Delete for : owner, group, other
 */
public class PermissionsChecker {
    private static final int CREATE = 0b100010001000;
    private static final int READ =   0b010001000100;
    private static final int UPDATE = 0b001000100010;
    private static final int DELETE = 0b000100010001;

    public static boolean isAllowed(String[] roles, SecurityContext security) {
        for (String role:roles) {
            if (security.isUserInRole(role)) {
                return true;
            }
        }

        return false;
    }
}
