package com.dougdomingos.expensetracker.auth;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

    public static UUID getAuthenticatedUserID() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userID = UUID.fromString(authentication.getName());
        return userID;
    }
}
