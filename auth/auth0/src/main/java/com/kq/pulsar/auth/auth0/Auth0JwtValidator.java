package com.kq.pulsar.auth.auth0;

import java.security.interfaces.RSAPublicKey;
import java.util.HashSet;
import java.util.Set;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

/**
 * This example demonstrates Kafkaesque's the requirement and logic to verify auth0 JWT.
 * 
 * Algorithm 
 *      supports RSA 256
 *      public key must be accessible (wget) from https://<your-domain>/.well-known/jwks.json
 *      your-domain is provided by auth0 in the form of auth0.com
 *      we use the issuer as `your-domain`
 * 
 * 
 * Time validation includes 10 seconds leeway
 *      iat - issued in a past date < the current timestamp
 *      exp - expired date > the current timestamp
 *      nbf - no good before < the current timestamp
 * 
 * Permission authorization
 *      auth0 API -> Permissions are used for authorization
 *      produce:topic
 *      consume:topic
 * 
 * Kafkaeque does not store JWT.
 */
public class Auth0JwtValidator {

    private String issuer = null;
    private Set<String> requirePermissions = new HashSet<String>();
    private int acceptedTimeLeeway = 10; //seconds

    private Auth0JwtValidator() {}

    public static Auth0JwtValidator create() {
        return new Auth0JwtValidator();
    }

    /**
     * Set required token issuer. It is optional.
     * If it's not provided, the issuer in the token will be used for public key decoding.
     * @param iss
     * @return
     */
    public Auth0JwtValidator issuer(String iss) {
        this.issuer = iss;
        return this;
    }

    /**
     * A set of required permissions. It is optional.
     * If it's not provided, no authorization will be performed.
     * @param permissions
     * @return
     */
    public Auth0JwtValidator permission(Set<String> permissions) {
        this.requirePermissions.addAll(permissions);
        return this;
    }

    /**
     * Time verification leeway in seconds for exp, iat, nbf.
     * @param sec
     * @return
     */
    public Auth0JwtValidator acceptedLeewaySeconds(int sec) {
        this.acceptedTimeLeeway = sec;
        return this;
    }

    /**
     * Verify the token with only boolean return.
     * @param token
     * @return
     */
    public boolean isValid(String token) {
        try {
            validate(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Validate the auth0 JWT.
     * @param token
     * @throws JWTDecodeException
     * @throws JWTVerificationException
     * @throws JwkException
     */
    public void validate(String token) throws JWTDecodeException, JWTVerificationException, JwkException {
        DecodedJWT jwt = JWT.decode(token);
        if (!jwt.getAlgorithm().equals("RS256")) {
            throw new JWTDecodeException("unsupport algorithm " + jwt.getAlgorithm());
        }
        
        if (this.issuer == null) {
            this.issuer = jwt.getIssuer();
        }

        JwkProvider jwkProvider = new JwkProviderBuilder(this.issuer).build();
        Jwk jwk = jwkProvider.get(jwt.getKeyId());
        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

        JWTVerifier verifier = JWT.require(algorithm)
            .acceptLeeway(acceptedTimeLeeway)
            .withIssuer(this.issuer)
            .build();
        DecodedJWT decodedJwt = verifier.verify(token);
        Set<String> permission = new HashSet<>(decodedJwt.getClaim("permissions").asList(String.class));
        System.out.println(permission.size());
        this.requirePermissions.forEach(v -> {
            if (!permission.contains(v)) {
                throw new JWTVerificationException("missing required permission " + v);
            }
        }); 
    }
}