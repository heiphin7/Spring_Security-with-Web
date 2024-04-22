package com.website.blogs.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtils {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration lifetime;

    public boolean validateToken(String token) {
        try {
            // Разбираем токен и извлекаем данные
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

            // Проверяем, не истек ли срок действия токена
            Date expiration = claims.getExpiration();

            return !expiration.before(new Date());
        } catch (Exception e) {
            // Ошибка при проверке токена
            return false;
        }
    }

    public String generateToken(UserDetails user){
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = user.getAuthorities().stream().map(
                GrantedAuthority::getAuthority
        ).collect(Collectors.toList());

        claims.put("roles", roles);

        Date issuedAt = new Date();
        Date expiredDate = new Date(issuedAt.getTime() + lifetime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
    public String getUsername(String token){
        return getAllClaimsFromToken(token).getSubject();
    }

    public List<String> getRoles(String token){
        return getAllClaimsFromToken(token).get("roles", List.class);
    }

    public Claims getAllClaimsFromToken(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
}