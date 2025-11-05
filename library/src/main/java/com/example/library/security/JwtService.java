//package com.example.library.security;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.security.Key;
//import java.util.Date;
//
//@Service
//public class JwtService {
//    private final Key key;
//    private final long expiration;
//
//    public JwtService(@Value("${jwt.secret}") String secret,
//                      @Value("${jwt.expiration}") long expiration) {
//        this.key = Keys.hmacShaKeyFor(secret.getBytes());
//        this.expiration = expiration;
//    }
//
//    public String generate(String username) {
//        Date now = new Date();
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(now)
//                .setExpiration(new Date(now.getTime() + expiration))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public String extractUsername(String token) {
//        return Jwts.parserBuilder().setSigningKey(key).build()
//                .parseClaimsJws(token).getBody().getSubject();
//    }
//}
