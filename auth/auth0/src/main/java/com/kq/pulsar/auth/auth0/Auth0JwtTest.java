package com.kq.pulsar.auth.auth0;

/**
 * This example demonstrates Kafkaesque's the requirement and logic to verify auth0 JWT.
 * 
 */
public class Auth0JwtTest {


    public static void main(String[] args) {
        String token = "eyJhbGciOiJSUzI1NiIsInR supply a valid auth jwt";
        System.out.println(
            Auth0JwtValidator.create()
                             .acceptedLeewaySeconds(3)
                             .isValid(token)
        );
    }
}