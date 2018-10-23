package org.magneato.core;

import javax.ws.rs.core.SecurityContext;

public class Security {

    boolean isAllowed4Create(SecurityContext context) {
        return true;
    }
}
