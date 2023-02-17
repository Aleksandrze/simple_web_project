package com.ashes.web.project.component;


import com.ashes.web.project.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@NoArgsConstructor

public class JwtProvider {
    @Value("${jwt.secret.access}")
    private String jwtAccessSecret;

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getLogin())) // login
                .claim("role", user.getRole()) // rol
                .setExpiration(Date.from(LocalDateTime.now().plusMinutes(15).atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(String.valueOf(jwtAccessSecret))))
                .compact();
    }


    public Map<String, String> decodeJwt(String encodedJWT){
        DecodedJWT decodedJWT = JWT.decode(encodedJWT);
        Map<String, String> decodeToken = new HashMap<String, String>();
        decodeToken.put("role", String.valueOf(decodedJWT.getClaim("role")));
        decodeToken.put("username", String.valueOf(decodedJWT.getSubject()));
        return decodeToken;
        }

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, Keys.hmacShaKeyFor(Decoders.BASE64.decode(String.valueOf(jwtAccessSecret))));
    }

    private boolean validateToken(@NonNull String token, @NonNull Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
        } catch (Exception e) {
            log.error("invalid token", e);
        }
        return false;
    }


}
