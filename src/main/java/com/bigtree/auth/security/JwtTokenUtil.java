package com.bigtree.auth.security;


import com.bigtree.auth.entity.Identity;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    private static final String SECRET="357638792F423F4428472B4B6250655368566D597133743677397A2443264629";

    //retrieve username from jwt token
    public String getUserId(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        JwtParser parser = Jwts.parser().setSigningKey(SECRET).build();
        return parser.parseClaimsJws(token).getBody();
    }

    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //generate token for user
    public String generateToken(Identity identity) {
        log.info("Generating access token..");
        Map<String, Object> claims = new HashMap<>();
        claims.put("firstName", identity.getFirstName());
        claims.put("lastName", identity.getLastName());
        claims.put("clientId", identity.getClientId());
        claims.put("recordId", identity.get_id());
        claims.put("clientType", identity.getClientType().getName());
        claims.put("email", identity.getEmail());
        claims.put("mobile", identity.getMobile());
        return doGenerateToken(claims, identity.get_id());
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();
    }

    //validate token
    public Boolean validateToken(String token, Identity identity) {
        log.info("Validating token");
        if (isTokenExpired(token)){
            log.info("Token expired...");
            return false;
        }
        final String userId = getUserId(token);
        return (userId.equals(identity.get_id()) && !isTokenExpired(token));
    }
}