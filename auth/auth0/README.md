# Auth0 JWT verification and validation

`Auth0JwtValidator` validates a JWT for Pulsar integration. This is the logic and requirement to validate auth0 JWT in order to generate Pulsar bearer token.

#### Algorithm 
    supports RSA 256
     public key must be accessible (wget) from https://<your-domain>/.well-known/jwks.json
     your-domain is provided by auth0 in the form of auth0.com
     we use the issuer as `your-domain`


#### Time validation includes 10 seconds leeway
     iat - issued in a past date < the current timestamp
     exp - expired date > the current timestamp
     nbf - no good before < the current timestamp

#### Permission authorization
     auth0 API -> Permissions are used for authorization
     produce:topic
     consume:topic

#### Kafkaeque does not store JWT.
