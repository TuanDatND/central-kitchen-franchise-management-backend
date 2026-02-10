package com.CocOgreen.CenFra.MS.security;

import com.CocOgreen.CenFra.MS.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    private static final String SECRET = "6F2vpZX1h09hCTOXafWvrAeKdyH1d3XDMDzBR9KGWpL4xJ8AuhTMbFxPLiyvhJTa";
    private static final long EXP = 86400000;

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUserName())
                .claim("userId", user.getUserId())
                .claim("role", user.getRole().getRoleName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXP))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
