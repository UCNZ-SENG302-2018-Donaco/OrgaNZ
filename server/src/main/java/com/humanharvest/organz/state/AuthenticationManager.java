package com.humanharvest.organz.state;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

public class AuthenticationManager {

    /**
     * Gets the secret used to sign tokens.
     */
    private static String getSecret() {
        return "secret";
    }

    /**
     * Given an authentication token, check if it is a valid administrator
     *
     * @param authenticationToken The authentication token to check
     * @throws AuthenticationException Thrown if the authentication is invalid for any reason
     */
    public void verifyAdminAccess(String authenticationToken) throws AuthenticationException {
        String identifier = getIdentifierFromToken(authenticationToken);

        if (!checkAdmin(identifier)) {
            throw new AuthenticationException("X-Auth-Token does not match any allowed user type");
        }
    }

    /**
     * Given an authentication token, check if it is a valid administrator or clinician
     *
     * @param authenticationToken The authentication token to check
     * @throws AuthenticationException Thrown if the authentication is invalid for any reason
     */
    public void verifyClinicianOrAdmin(String authenticationToken) throws AuthenticationException {
        String identifier = getIdentifierFromToken(authenticationToken);

        if (checkClinician(identifier) ||
                checkAdmin(identifier)) {
            return;
        }
        throw new AuthenticationException("X-Auth-Token does not match any allowed user type");
    }

    /**
     * Given an authentication token and client, check if the token matches any administrator, any clinician, or the
     * given client
     *
     * @param authenticationToken The authentication token to check
     * @throws AuthenticationException Thrown if the authentication is invalid for any reason
     */
    public void verifyClientAccess(String authenticationToken, Client viewedClient) throws AuthenticationException {
        String identifier = getIdentifierFromToken(authenticationToken);

        //Check the three user types, if any match then they will return true and we can return due to success.
        if (checkClient(identifier, viewedClient) ||
                checkClinician(identifier) ||
                checkAdmin(identifier)) {
            return;
        }
        //Otherwise throw an exception
        throw new AuthenticationException("X-Auth-Token does not match any allowed user type");
    }

    /**
     * Given an authentication token and clinician, check if the token matches any administrator or the
     * given clinician
     *
     * @param authenticationToken The authentication token to check
     * @throws AuthenticationException Thrown if the authentication is invalid for any reason
     */
    public void verifyClinicianAccess(String authenticationToken, Clinician viewedClinician) throws
            AuthenticationException {
        String identifier = getIdentifierFromToken(authenticationToken);

        if (checkClinician(identifier, viewedClinician) ||
                checkAdmin(identifier)) {
            return;
        }
        //Otherwise throw an exception
        throw new AuthenticationException("X-Auth-Token does not match any allowed user type");
    }

    protected boolean checkClient(String identifier, Client viewedClient) throws AuthenticationException {
        if (identifier.startsWith("client:")) {
            int id = Integer.parseInt(identifier.substring(7));
            Optional<Client> client = State.getClientManager().getClientByID(id);
            if (!client.isPresent()) {
                throw new AuthenticationException("X-Auth-Token refers to an invalid client");
            } else if (!Objects.equals(client.get(), viewedClient)) {
                throw new AuthenticationException("X-Auth-Token refers to a different client");
            }
            return true;
        }
        return false;
    }

    protected boolean checkClinician(String identifier) throws AuthenticationException {
        if (identifier.startsWith("clinician:")) {
            int staffId = Integer.parseInt(identifier.substring(10));
            Optional<Clinician> clinican = State.getClinicianManager().getClinicianByStaffId(staffId);
            if (!clinican.isPresent()) {
                throw new AuthenticationException("X-Auth-Token refers to an invalid clinician");
            }
            return true;
        }
        return false;
    }

    protected boolean checkClinician(String identifier, Clinician viewedClinician) throws AuthenticationException {
        if (identifier.startsWith("clinician:")) {
            int staffId = Integer.parseInt(identifier.substring(10));
            Optional<Clinician> clinician = State.getClinicianManager().getClinicianByStaffId(staffId);
            if (!clinician.isPresent()) {
                throw new AuthenticationException("X-Auth-Token refers to an invalid clinician");
            } else if (!Objects.equals(clinician.get(), viewedClinician)) {
                throw new AuthenticationException("X-Auth-Token refers to a different clinician");
            }
            return true;
        }
        return false;
    }

    protected boolean checkAdmin(String identifier) throws AuthenticationException {
        if (identifier.startsWith("admin:")) {
            String username = identifier.substring(6);
            Optional<Administrator> administrator = State.getAdministratorManager()
                    .getAdministratorByUsername(username);
            if (!administrator.isPresent()) {
                throw new AuthenticationException("X-Auth-Token refers to an invalid administrator");
            }
            return true;
        }
        return false;
    }

    protected String getIdentifierFromToken(String token) throws AuthenticationException {
        if (token == null) {
            // TODO: Throw 401 instead?
            throw new AuthenticationException("X-Auth-Token does not exist");
        }

        try {
            Claims body = Jwts.parser()
                    .setSigningKey(getSecret())
                    .parseClaimsJws(token)
                    .getBody();
            return body.getId();
        } catch (SignatureException | MalformedJwtException e) {
            throw new AuthenticationException("X-Auth-Token is invalid or expired", e);
        }
    }

    /**
     * Generates an authentication token for a clinician.
     */
    public String generateClientToken(int id) {
        return generateToken("client:" + id);
    }

    /**
     * Generates an authentication token for a clinician.
     */
    public String generateClinicianToken(int staffId) {
        return generateToken("clinician:" + staffId);
    }

    /**
     * Generates an authentication token for an administrator.
     */
    public String generateAdministratorToken(String username) {
        return generateToken("admin:" + username);
    }

    private static String generateToken(String id) {
        return Jwts.builder()
                .setId(id)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(86400))) // 24 hours
                .signWith(SignatureAlgorithm.HS512, getSecret())
                .compact();
    }
}
