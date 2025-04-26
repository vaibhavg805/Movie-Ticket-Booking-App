package com.vaibhav.booking.system.util;

import com.vaibhav.booking.system.config.CustomPropertiesConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

    private final String SECRET_KEY;
    private static final long JWT_EXPIRATION = 1000 * 60 * 15;
    private static final long TEMP_TOKEN_VALIDITY = 1000 * 60 * 5; // 5 minutes
    private CustomPropertiesConfig customPropertiesConfig;

    public JwtUtil(CustomPropertiesConfig customPropertiesConfig){
        this.SECRET_KEY = customPropertiesConfig.getSecret();
    }

    private Key generateKey(){
        byte[] arr = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(arr);
    }
    public String generateToken(String username) {
        return createToken(Map.of("mfa_verified", false), username, TEMP_TOKEN_VALIDITY);
    }

    private String createToken(Map<String, Object> claims, String username, long expiration){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(generateKey())
                .compact();
    }

    public String generateTemporaryToken(String username){
        return createToken(Map.of("mfa_verified",true),username,JWT_EXPIRATION);
    }

    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    private <R> R extractClaims(String token, Function<Claims,R> claimsResolver) {
        final Claims getClaims = extractAllClaims(token);
        return claimsResolver.apply(getClaims);
    }

    private Claims extractAllClaims(String token){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(generateKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token, String username){
        try {
            String tokenUsername = extractUsername(token);
            return (tokenUsername.equals(username) && !isTokenExpired(token));
        }catch (ExpiredJwtException e) {
            log.warn("Token validation failed: token expired.");
            return false;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token){
        Date expirationTime = extractClaims(token,Claims::getExpiration);
        return expirationTime.before(new Date());
    }

    public boolean isTemporaryToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return Boolean.TRUE.equals(claims.get("mfa_verified", Boolean.class));
        } catch (Exception e) {
            return false;
        }
    }


}
