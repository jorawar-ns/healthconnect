package com.healthconnect.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT utility — jjwt 0.9.1 API.
 *
 * NOTE: This class uses the older jjwt API (setSubject / setSigningKey).
 * The 0.11+ rewrite was evaluated in 2023 but migration was not scheduled
 * before the end of the release cycle.  Do not upgrade without a full
 * regression pass on auth-service and api-gateway.
 */
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private final String secret;
    private final long accessTokenTtlMs;
    private final long refreshTokenTtlMs;

    public JwtUtil(String secret, long accessTokenTtlMs, long refreshTokenTtlMs) {
        this.secret = secret;
        this.accessTokenTtlMs = accessTokenTtlMs;
        this.refreshTokenTtlMs = refreshTokenTtlMs;
    }

    // ── Token generation ─────────────────────────────────────────────────────

    public String generateAccessToken(String subject, Map<String, Object> extraClaims) {
        return buildToken(subject, extraClaims, accessTokenTtlMs);
    }

    public String generateRefreshToken(String subject) {
        return buildToken(subject, null, refreshTokenTtlMs);
    }

    private String buildToken(String subject, Map<String, Object> claims, long ttlMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ttlMs);

        return Jwts.builder()
                .setClaims(claims != null ? claims : new java.util.HashMap<>())
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS512, secret.getBytes())
                .compact();
    }

    // ── Token validation ─────────────────────────────────────────────────────

    public boolean isValid(String token) {
        try {
            getAllClaims(token);
            return true;
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Empty JWT claims: {}", e.getMessage());
        }
        return false;
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(getAllClaims(token));
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }
}
