package com.bigtree.auth.security;


import com.bigtree.auth.entity.UserType;
import com.bigtree.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
    public static final long LONG_JWT_TOKEN_VALIDITY = 365* 24 * 60 * 60;

    //    private static final String SECRET="357638792F423F4428472B4B6250655368566D597133743677397A2443264629";
    private static final String SECRET = "e16c3a9902d10ae182103e04e854706dc79fa989361bf7e8f047c94ffb2a9e059a1667cbfda42587f49dda731a68608ea1c21614e3d90dbe79e93acd89f086d0";

    //retrieve username from jwt token
    public String getUserId(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getSubject(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getSubjectType(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.get("clientType", String.class);
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
    public Claims getAllClaimsFromToken(String token) {
        Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
        JwtParser parser = Jwts.parser().setSigningKey(key).build();
        return parser.parseClaimsJws(token.trim()).getBody();
    }

    //check if the token has expired
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        log.info("Expiry date {}", expiration);
        return expiration.before(new Date());
    }

    //generate token for user
    public String generateToken(User user) {
        log.info("Generating access token..");
        Map<String, String> claims = new HashMap<>();
        claims.put("name", user.getName());
        claims.put("userType", user.getUserType().getName());
        if (user.getUserType() == UserType.Business){
            claims.put("businessType", user.getBusinessType());
            claims.put("businessId", user.getBusinessId());
        }
        claims.put("mobile", user.getMobile());
        claims.put("recordId", user.get_id());
        claims.put("userId", user.getUserId());
        return doGenerateToken(claims, user);
    }

    public String createPrivateKeyJwt(Map<String, String> claims, User user) {
        return doGenerateToken(claims, user);
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, String> claims, User user) {
        long l = (user.getUserType() == UserType.Customer) ? JWT_TOKEN_VALIDITY : LONG_JWT_TOKEN_VALIDITY;
        Date expiry = new Date(System.currentTimeMillis() + l  * 1000);
        Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
        String token = Jwts.builder().setClaims(claims).signWith(key, SignatureAlgorithm.HS512).setExpiration(expiry).subject(user.getEmail()).compact();
        return token;
    }

}