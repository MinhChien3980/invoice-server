package com.example.invoiceserver.config;

import com.example.invoiceserver.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret:defaultSecretKeyThatIsAtLeast32BytesLongForHs256Algorithm}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}") // Default: 24 hours
    private long jwtExpirationMs;
    
    private Key key;
    
    private final UserDetailsService userDetailsService;
    
    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @PostConstruct
    protected void init() {
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        org.springframework.security.core.userdetails.User user = 
            (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        
        Set<String> roles = user.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toSet());
        
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("roles", roles)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
            
        return claims.getSubject();
    }
    
    @SuppressWarnings("unchecked")
    public Set<UserRole> getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
            
        List<String> roles = (List<String>) claims.get("roles");
        return roles.stream()
            .map(UserRole::valueOf)
            .collect(Collectors.toSet());
    }
    
    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        var authorities = userDetails.getAuthorities();
        
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 