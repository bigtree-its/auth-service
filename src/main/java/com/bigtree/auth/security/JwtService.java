package com.bigtree.auth.security;

import com.bigtree.auth.entity.User;
import com.bigtree.auth.entity.UserType;
import com.bigtree.auth.error.ApiException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDate;
import java.time.ZoneId;
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


    public void authenticate(String customerId, String token) {
        Claims claims = validateAccessToken(token);
        if ( claims == null){
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorised");
        }
        if (extractUsername(token).equalsIgnoreCase(customerId)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorised");
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        log.info("Checking expiry..");
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, User user) {
        log.info("Validating token...");
        if (isTokenExpired(token)){
            log.error("Token expired...");
            return false;
        }
        final String username = extractUsername(token);
        return (username.equals(user.getEmail()) && !isTokenExpired(token));
    }

    public String generateIdToken(User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", user.getName());
        claims.put("clientId", user.getUserId());
        claims.put("recordId", user.get_id());
        claims.put("clientType", user.getUserType().getName());
        claims.put("email", user.getEmail());
        claims.put("mobile", user.getMobile());
        return createIdToken(claims, user);
    }

    public String generateAccessToken(User user) {
        log.info("The Secret Key Algo: {}, Key: {}", getSecretKey().getAlgorithm(), getSecretKey().toString());
        Date expiry = new Date(System.currentTimeMillis() + 1000 * 60 * 60);
        if ( user.getUserType() == UserType.CustomerApp || user.getUserType() == UserType.SupplierApp){
            expiry = Date.from(LocalDate.now().plusDays(365).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("clientId", user.getUserId());
        claims.put("recordId", user.get_id());
        claims.put("clientType", user.getUserType().getName());
        return Jwts.builder()
                .claims(claims)
                .subject(user.get_id())
                .issuer("www.auth.dudul.com")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiry)// set expiry as 60 mins
                .signWith(getSecretKey())
                .compact();
    }

    private String createIdToken(Map<String, Object> claims, User user) {
        return Jwts.builder()
                .claims(claims)
                .issuer("auth.desitimes.co.uk")
                .subject(user.getUserId())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+1000*60*60))// set expiry as 60 mins
                .signWith(getSecretKey())
                .compact();
    }

    public String createPrivateKeyJwt(Map<String, String> claims, User user) {
        return Jwts.builder()
                .claims(claims)
                .issuer("auth.desitimes.co.uk")
                .subject(user.getUserId())
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
            log.error("JWT expired {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("Token is null, empty or only whitespace {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("JWT is invalid", ex);
        } catch (UnsupportedJwtException ex) {
            log.error("JWT is not supported", ex);
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
