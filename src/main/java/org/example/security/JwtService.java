package org.example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${security.jwt.secret-key}")
  private String secretKey;

  @Value("${security.jwt.expiration-time}")
  private long jwtExpiration;

  private final Set<String> blacklistedTokens = Collections.newSetFromMap(new ConcurrentHashMap<>());

  public String extractUsername(final String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String generateToken(final UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public String generateToken(final Map<String, Object> extraClaims, final UserDetails userDetails) {
    return buildToken(extraClaims, userDetails, jwtExpiration);
  }

  public long getExpirationTime() {
    return jwtExpiration;
  }

  private String buildToken(final Map<String, Object> extraClaims, final UserDetails userDetails, final long expiration) {
    return Jwts.builder()
               .setClaims(extraClaims)
               .setSubject(userDetails.getUsername())
               .setIssuedAt(new Date(System.currentTimeMillis()))
               .setExpiration(new Date(System.currentTimeMillis() + expiration))
               .signWith(getSignInKey(), SignatureAlgorithm.HS256)
               .compact();
  }

  public boolean isTokenValid(final String token, final UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token) && !isTokenBlacklisted(token);
  }

  private boolean isTokenExpired(final String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(final String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(final String token) {
    return Jwts.parser()
               .setSigningKey(getSignInKey())
               .build()
               .parseClaimsJws(token)
               .getBody();
  }

  private Key getSignInKey() {
    var keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public void invalidateToken(final String token) {
    blacklistedTokens.add(token);
  }

  private boolean isTokenBlacklisted(final String token) {
    return blacklistedTokens.contains(token);
  }

}