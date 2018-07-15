package com.humanharvest.organz.state;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

public class AuthenticationManager {

    /**
     * Gets the secret used to sign tokens.
     */
    private String getSecret() {
        // TODO
        return "secret";
    }

    public void verifyAdminAccess(String authenticationToken) throws AuthenticationException {
        if (authenticationToken == null) {
            throw new AuthenticationException("X-Auth-Token does not exist");
        }

        try {
            String username = Jwts.parser()
                    .setSigningKey(getSecret())
                    .parseClaimsJws(authenticationToken)
                    .getBody().getSubject();

            Optional<Administrator> administrator = State.getAdministratorManager()
                    .getAdministratorByUsername(username);

            if (!administrator.isPresent()) {
                throw new AuthenticationException("X-Auth-Token refers to an invalid administrator");
            }
        } catch (SignatureException e) {
            throw new AuthenticationException("X-Auth-Token is invalid or expired", e);
        }
    }

    /**
     * Generates an authentication token for an administrator.
     */
    public String generateAdministratorToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(86400))) // 24 hours
                .signWith(SignatureAlgorithm.HS512, getSecret())
                .compact();
    }
}
