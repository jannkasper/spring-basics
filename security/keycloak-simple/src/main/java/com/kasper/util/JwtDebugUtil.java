package com.kasper.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

/**
 * Utility class for debugging JWT tokens.
 */
public class JwtDebugUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtDebugUtil.class);

    /**
     * Logs all claims in a JWT token for debugging purposes.
     *
     * @param jwt The JWT token to debug
     */
    public static void logJwtClaims(Jwt jwt) {
        logger.debug("JWT Claims:");
        for (Map.Entry<String, Object> entry : jwt.getClaims().entrySet()) {
            logger.debug("  {} = {}", entry.getKey(), entry.getValue());
        }
        
        // Specifically log role-related claims
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            logger.debug("Realm roles: {}", realmAccess.get("roles"));
        }
        
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null) {
            logger.debug("Resource access: {}", resourceAccess);
            
            // Check for client-specific roles
            Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("spring-boot-client");
            if (clientAccess != null && clientAccess.containsKey("roles")) {
                logger.debug("Client roles for spring-boot-client: {}", clientAccess.get("roles"));
            }
        }
    }
} 