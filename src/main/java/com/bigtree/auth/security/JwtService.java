package com.bigtree.auth.security;

import com.bigtree.auth.entity.Identity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtService {

//    private static final String SECRET="abcdefghijklmnOPQRSTUVWXYZ";
//    private static final SecretKey SECRET = Jwts.SIG.HS256.key().build();
    private static final String SECRET="357638792F423F4428472B4B6250655368566D597133743677397A2443264629";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, Identity identity) {
        final String username = extractUsername(token);
        return (username.equals(identity.getEmail()) && !isTokenExpired(token));
    }

    public String generateIdToken(Identity identity){
        Map<String, Object> claims = new HashMap<>();
        claims.put("firstName", identity.getFirstName());
        claims.put("lastName", identity.getLastName());
        claims.put("clientId", identity.getClientId());
        claims.put("collectionId", identity.get_id());
        claims.put("clientType", identity.getClientType().getName());
        claims.put("email", identity.getEmail());
        claims.put("mobile", identity.getMobile());
        return createIdToken(claims, identity);
    }

    public String generateAccessToken(Identity identity) {
        log.info("The Secret Key Algo: {}, Key: ", getSecretKey().getAlgorithm(), getSecretKey().toString());
        return Jwts.builder()
                .subject(identity.getClientId())
                .issuer("www.auth.hoc.com")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+1000*60*60))// set expiry as 60 mins
                .signWith(getSecretKey())
                .compact();
    }

    private String createIdToken(Map<String, Object> claims, Identity identity) {
        return Jwts.builder()
                .claims(claims)
                .issuer("www.auth.hoc.com")
                .subject(identity.getClientId())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+1000*60*60))// set expiry as 60 mins
                .signWith(getSecretKey())
                .compact();
    }

    public String createPrivateKeyJwt(Map<String, String> claims, Identity identity) {
        return Jwts.builder()
                .claims(claims)
                .issuer("www.auth.lunchie-munchie.com")
                .subject(identity.getClientId())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+1000*60*60*24*365))// set expiry as 1 year
                .signWith(getSecretKey())
                .compact();
    }

    public Claims validateAccessToken(String token) {
        try {
            final Claims claims = Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
            return claims;
        } catch (ExpiredJwtException ex) {
            log.error("JWT expired", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("Token is null, empty or only whitespace", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("JWT is invalid", ex);
        } catch (UnsupportedJwtException ex) {
            log.error("JWT is not supported", ex);
        } catch (SignatureException ex) {
            log.error("Signature validation failed");
        }

        return null;
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public SecretKey getSecretKey(){
        return  Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }
}
